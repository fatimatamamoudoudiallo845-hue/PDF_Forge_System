# ── Étape 1 : Compilation ──────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Compiler corba-server
COPY corba-server/ corba-server/
RUN cd corba-server && mvn package -DskipTests -q

# Compiler web-gateway
COPY web-gateway/ web-gateway/
RUN cd web-gateway && mvn package -DskipTests -q

# ── Étape 2 : Image finale ─────────────────────────────────
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/corba-server/target/corba-server-*-jar-with-dependencies.jar corba-server.jar
COPY --from=builder /app/web-gateway/target/web-gateway-*.jar web-gateway.jar
COPY start.sh .
RUN chmod +x start.sh

EXPOSE 8085
CMD ["./start.sh"]
