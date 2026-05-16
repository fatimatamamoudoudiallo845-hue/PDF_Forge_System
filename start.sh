#!/bin/bash
# ============================================================
# PDF_Forge System — Démarrage complet
# ============================================================

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║      PDF_Forge System — Démarrage        ║"
echo "╚══════════════════════════════════════════╝"
echo ""

# ── Vérifier Java ───────────────────────────────────────────
echo -e "${YELLOW}[0] Vérification Java...${NC}"
java -version 2>&1 | head -1
if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Java non trouvé !${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java OK${NC}"
echo ""

# ── Tuer les processus existants ────────────────────────────
echo -e "${YELLOW}[1] Nettoyage des processus existants...${NC}"
pkill -f "orbd"          2>/dev/null
pkill -f "corba-server"  2>/dev/null
pkill -f "spring-boot"   2>/dev/null
pkill -f "web-gateway"   2>/dev/null
sleep 2
echo -e "${GREEN}✓ Nettoyage terminé${NC}"
echo ""

# ── Démarrer orbd (Name Service CORBA) ─────────────────────
echo -e "${YELLOW}[2] Démarrage du Name Service CORBA (orbd:1050)...${NC}"
orbd -ORBInitialPort 1050 &
ORBD_PID=$!
sleep 3

if kill -0 $ORBD_PID 2>/dev/null; then
    echo -e "${GREEN}✓ orbd démarré (PID: $ORBD_PID)${NC}"
else
    echo -e "${RED}✗ Échec démarrage orbd${NC}"
    exit 1
fi
echo ""

# ── Démarrer le Serveur CORBA ───────────────────────────────
echo -e "${YELLOW}[3] Démarrage du Serveur CORBA...${NC}"

JAR="corba-server/target/corba-server-jar-with-dependencies.jar"
if [ ! -f "$JAR" ]; then
    echo -e "${RED}✗ JAR non trouvé : $JAR${NC}"
    echo -e "${YELLOW}  → Lancez d'abord : ./build.sh${NC}"
    kill $ORBD_PID 2>/dev/null
    exit 1
fi

java -jar $JAR \
     -ORBInitialPort 1050 \
     -ORBInitialHost localhost \
     > /tmp/corba-server.log 2>&1 &
CORBA_PID=$!
sleep 4

if kill -0 $CORBA_PID 2>/dev/null; then
    echo -e "${GREEN}✓ Serveur CORBA démarré (PID: $CORBA_PID)${NC}"
else
    echo -e "${RED}✗ Échec démarrage serveur CORBA${NC}"
    echo -e "${YELLOW}  → Logs : cat /tmp/corba-server.log${NC}"
    kill $ORBD_PID 2>/dev/null
    exit 1
fi
echo ""

# ── Démarrer le Web Gateway ─────────────────────────────────
echo -e "${YELLOW}[4] Démarrage du Web Gateway Spring Boot (:8085)...${NC}"

cd web-gateway
mvn spring-boot:run > /tmp/gateway.log 2>&1 &
GATEWAY_PID=$!
cd ..
sleep 8

if kill -0 $GATEWAY_PID 2>/dev/null; then
    echo -e "${GREEN}✓ Web Gateway démarré (PID: $GATEWAY_PID)${NC}"
else
    echo -e "${RED}✗ Échec démarrage Web Gateway${NC}"
    echo -e "${YELLOW}  → Logs : cat /tmp/gateway.log${NC}"
    kill $ORBD_PID $CORBA_PID 2>/dev/null
    exit 1
fi
echo ""

# ── Résumé ──────────────────────────────────────────────────
echo "╔══════════════════════════════════════════╗"
echo "║        Système démarré avec succès !     ║"
echo "╚══════════════════════════════════════════╝"
echo ""
echo -e "  ${BLUE}orbd${NC}       PID : ${GREEN}$ORBD_PID${NC}     port : 1050"
echo -e "  ${BLUE}CORBA${NC}      PID : ${GREEN}$CORBA_PID${NC}     port : 1050"
echo -e "  ${BLUE}Gateway${NC}    PID : ${GREEN}$GATEWAY_PID${NC}    port : 8085"
echo ""
echo -e "  Interface  : ${GREEN}http://localhost:8085${NC}"
echo -e "  Santé      : ${GREEN}http://localhost:8085/actuator/health${NC}"
echo -e "  Endpoints  : ${GREEN}http://localhost:8085/actuator/mappings${NC}"
echo ""
echo -e "  Logs CORBA  : ${YELLOW}tail -f /tmp/corba-server.log${NC}"
echo -e "  Logs Gateway: ${YELLOW}tail -f /tmp/gateway.log${NC}"
echo ""
echo -e "  Pour arrêter : ${RED}kill $ORBD_PID $CORBA_PID $GATEWAY_PID${NC}"
echo "  Ou : pkill -f orbd && pkill -f corba-server && pkill -f spring-boot"
echo ""

# ── Attendre ────────────────────────────────────────────────
wait $GATEWAY_PID
