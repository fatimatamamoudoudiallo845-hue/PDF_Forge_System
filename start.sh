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

# ── Démarrer orbd ───────────────────────────────────────
echo "[1] Démarrage orbd (port 1050)..."
/usr/lib/jvm/java-8-openjdk-amd64/bin/orbd -ORBInitialPort 1050 &
ORBD_PID=$!
sleep 3

if kill -0 $ORBD_PID 2>/dev/null; then
    echo "✓ orbd démarré (PID: $ORBD_PID)"
else
    echo "✗ Échec démarrage orbd"
    exit 1
fi

# ── Démarrer corba-server ────────────────────────────────
echo "[2] Démarrage Serveur CORBA..."
java \
  --add-opens java.base/java.io=ALL-UNNAMED \
  --add-opens java.management/javax.management.openmbean=ALL-UNNAMED \
  --add-opens java.management/javax.management=ALL-UNNAMED \
  -Dcom.sun.corba.ee.impl.orb.ORBImpl.disableManagedObjectManager=true \
  -jar /app/corba-server.jar \
  -ORBInitialPort 1050 \
  -ORBInitialHost localhost > /tmp/corba.log 2>&1 &
CORBA_PID=$!

# ── Attendre le fichier IOR ──────────────────────────────
echo "Attente du fichier IOR..."
for i in $(seq 1 30); do
    if [ -f "/app/PDFProcessor.ior" ]; then
        echo "✓ Fichier IOR trouvé !"
        break
    fi
    echo "  attente... ($i/30)"
    sleep 2
done

# ── Vérifier que le fichier IOR existe ──────────────────
if [ ! -f "/app/PDFProcessor.ior" ]; then
    echo "✗ Fichier IOR non créé — Logs CORBA :"
    cat /tmp/corba.log
    kill $ORBD_PID 2>/dev/null
    exit 1
fi

# ── Démarrer web-gateway ─────────────────────────────────
echo "[3] Démarrage Web Gateway (port ${PORT:-8085})..."
java -jar /app/web-gateway.jar \
     --server.port=${PORT:-8085} \
     --corba.ior.path=/app/PDFProcessor.ior

wait
