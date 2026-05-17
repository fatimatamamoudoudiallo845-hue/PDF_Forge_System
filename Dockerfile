# ── Étape 1 : Compilation ──────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY corba-server/ corba-server/
RUN cd corba-server && mvn package -DskipTests -q

COPY web-gateway/ web-gateway/
RUN cd web-gateway && mvn package -DskipTests -q

# ── Étape 2 : Image finale ─────────────────────────────────
FROM maven:3.9-eclipse-temurin-17
WORKDIR /app

# Installer Java 8 pour orbd
RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get clean

COPY --from=builder /app/corba-server/target/corba-server-*-jar-with-dependencies.jar corba-server.jar
COPY --from=builder /app/web-gateway/target/web-gateway-*.jar web-gateway.jar
COPY start.sh .
RUN chmod +x start.sh

EXPOSE 8085
CMD ["./start.sh"]
