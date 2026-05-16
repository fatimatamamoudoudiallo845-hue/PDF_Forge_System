FROM openjdk:17-jdk-slim

WORKDIR /app

# Copier les JARs compilés
COPY corba-server/target/corba-server-*.jar corba-server.jar
COPY web-gateway/target/web-gateway-*.jar web-gateway.jar

# Script de démarrage
COPY start.sh .
RUN chmod +x start.sh

EXPOSE 8085

CMD ["./start.sh"]
