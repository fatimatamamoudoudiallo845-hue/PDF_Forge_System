#!/bin/bash
echo "╔══════════════════════════════════════╗"
echo "║     PDF_Forge — Build complet        ║"
echo "╚══════════════════════════════════════╝"

# 1. Compiler l'IDL
echo "[1/4] Compilation IDL..."
mkdir -p corba-server/src/main/java
idlj -fall -td corba-server/src/main/java PDFProcessor.idl

# 2. Copier les stubs vers web-gateway
echo "[2/4] Copie des stubs..."
mkdir -p web-gateway/src/main/java/PDFModule
cp -r corba-server/src/main/java/PDFModule/* \
      web-gateway/src/main/java/PDFModule/

# 3. Build serveur CORBA
echo "[3/4] Build corba-server..."
cd corba-server && mvn clean package -q && cd ..

# 4. Build web-gateway
echo "[4/4] Build web-gateway..."
cd web-gateway && mvn clean package -q && cd ..

echo ""
echo "✓ Build terminé !"
echo ""
echo "Démarrage :"
echo "  Terminal 1 : orbd -ORBInitialPort 1050"
echo "  Terminal 2 : java -jar corba-server/target/corba-server-jar-with-dependencies.jar -ORBInitialPort 1050 -ORBInitialHost localhost"
echo "  Terminal 3 : cd web-gateway && mvn spring-boot:run"
echo "  Accès      : http://localhost:8085"
