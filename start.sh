#!/bin/bash
echo "╔══════════════════════════════════════════╗"
echo "║      PDF_Forge System — Démarrage        ║"
echo "╚══════════════════════════════════════════╝"

# ── Vérifier Java ───────────────────────────────────────
echo "[0] Vérification Java..."
java -version 2>&1 | head -1
if [ $? -ne 0 ]; then
    echo "✗ Java non trouvé !"
    exit 1
fi
echo "✓ Java OK"

# ── Démarrer corba-server SANS orbd ─────────────────────
echo "[1] Démarrage Serveur CORBA..."
java \
  --add-opens java.base/java.io=ALL-UNNAMED \
  --add-opens java.management/javax.management.openmbean=ALL-UNNAMED \
  --add-opens java.management/javax.management=ALL-UNNAMED \
  -Dcom.sun.corba.ee.impl.orb.ORBImpl.disableManagedObjectManager=true \
  -jar /app/corba-server.jar > /tmp/corba.log 2>&1 &
CORBA_PID=$!
echo "✓ Serveur CORBA lancé (PID: $CORBA_PID)"

# ── Attendre le fichier IOR ──────────────────────────────
echo "[2] Attente du fichier IOR..."
for i in $(seq 1 50); do
    if [ -f "/app/PDFProcessor.ior" ]; then
        echo "✓ Fichier IOR trouvé après ${i} tentatives !"
        break
    fi
    echo "  attente... ($i/50)"
    sleep 3
done

# ── Vérifier que le fichier IOR existe ──────────────────
if [ ! -f "/app/PDFProcessor.ior" ]; then
    echo "✗ Fichier IOR non créé après 150 secondes"
    echo "── Logs CORBA ──"
    cat /tmp/corba.log
    exit 1
fi

echo "── Contenu IOR ──"
cat /app/PDFProcessor.ior
echo ""

# ── Vérifier que corba-server tourne encore ─────────────
if ! kill -0 $CORBA_PID 2>/dev/null; then
    echo "✗ Serveur CORBA s'est arrêté !"
    echo "── Logs CORBA ──"
    cat /tmp/corba.log
    exit 1
fi
echo "✓ Serveur CORBA toujours actif"

# ── Démarrer web-gateway ─────────────────────────────────
echo "[3] Démarrage Web Gateway (port ${PORT:-8085})..."
java -jar /app/web-gateway.jar \
     --server.port=${PORT:-8085} \
     --corba.ior.path=/app/PDFProcessor.ior

wait
