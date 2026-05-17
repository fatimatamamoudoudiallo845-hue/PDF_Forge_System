FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copier et compiler corba-server
COPY corba-server/ corba-server/
RUN cd corba-server && mvn package -DskipTests -q

# Copier et compiler web-gateway
COPY web-gateway/ web-gateway/
RUN cd web-gateway && mvn package -DskipTests -q

# ── Image finale ──
FROM eclipse-temurin:17-jre-slim
WORKDIR /app

COPY --from=builder /app/corba-server/target/corba-server-*-jar-with-dependencies.jar corba-server.jar
COPY --from=builder /app/web-gateway/target/web-gateway-*.jar web-gateway.jar
COPY start.sh .
RUN chmod +x start.sh

EXPOSE 8085
CMD ["./start.sh"]
