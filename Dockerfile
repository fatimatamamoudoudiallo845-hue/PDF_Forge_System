# ── Étape 1 : Compilation ──────────────────────────────────
FROM maven:3.9-amazoncorretto-17 AS builder
WORKDIR /app

COPY corba-server/ corba-server/
RUN cd corba-server && mvn package -DskipTests -q

COPY web-gateway/ web-gateway/
RUN cd web-gateway && mvn package -DskipTests -q

# ── Étape 2 : Image finale ─────────────────────────────────
FROM amazoncorretto:17
WORKDIR /app

# Installer orbd via Java 8
RUN yum install -y java-1.8.0-amazon-corretto && \
    yum clean all

COPY --from=builder /app/corba-server/target/corba-server-*-jar-with-dependencies.jar corba-server.jar
COPY --from=builder /app/web-gateway/target/web-gateway-*.jar web-gateway.jar
COPY start.sh .
RUN chmod +x start.sh

EXPOSE 8085
CMD ["./start.sh"]
