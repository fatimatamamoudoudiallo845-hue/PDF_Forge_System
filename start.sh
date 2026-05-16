#!/bin/bash

# Démarrer le naming service CORBA
orbd -ORBInitialPort 1050 &
sleep 3

# Démarrer le serveur CORBA
java -jar corba-server.jar -ORBInitialPort 1050 -ORBInitialHost localhost &
sleep 5

# Démarrer le web gateway
java -jar web-gateway.jar --server.port=8085
