FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY corba-server/ ./corba-server/
COPY web-gateway/ ./web-gateway/

RUN cd corba-server && mvn clean package -DskipTests
RUN cd web-gateway && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/corba-server/target/corba-server-*-jar-with-dependencies.jar corba-server.jar
COPY --from=builder /app/web-gateway/target/web-gateway-*.jar web-gateway.jar
COPY start.sh .
RUN chmod +x start.sh

EXPOSE 8085
CMD ["./start.sh"]
