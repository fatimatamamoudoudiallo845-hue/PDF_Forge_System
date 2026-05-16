#!/bin/bash

# Aller dans le bon répertoire
cd ~/PDF_Forge_System/corba-server

echo "🚀 Démarrage du serveur CORBA PDF_Forge..."

java \
  --add-opens java.base/java.io=ALL-UNNAMED \
  --add-opens java.management/javax.management.openmbean=ALL-UNNAMED \
  --add-opens java.management/javax.management=ALL-UNNAMED \
  -Dcom.sun.corba.ee.impl.orb.ORBImpl.disableManagedObjectManager=true \
  -jar target/corba-server-1.0.0-jar-with-dependencies.jar \
  -ORBInitialPort 1050 \
  -ORBInitialHost localhost
