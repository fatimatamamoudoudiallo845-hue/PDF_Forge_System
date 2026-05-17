#!/bin/bash

echo "=== Démarrage PDF_Forge ==="

# Démarrer orbd
orbd -ORBInitialPort 1050 &
sleep 3

# Démarrer corba-server et attendre le fichier IOR
java -jar corba-server.jar \
     -ORBInitialPort 1050 \
     -ORBInitialHost localhost > /tmp/corba.log 2>&1 &

echo "Attente du fichier IOR..."
for i in $(seq 1 30); do
    if [ -f "/app/PDFProcessor.ior" ]; then
        echo "✓ Fichier IOR trouvé !"
        break
    fi
    sleep 1
done

# Démarrer web-gateway
java -jar web-gateway.jar \
     --server.port=8085 \
     --corba.ior.path=/app/PDFProcessor.ior

wait
