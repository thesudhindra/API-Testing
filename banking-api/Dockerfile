# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S playground && adduser -S playground -G playground
USER playground

COPY banking-api/target/banking-api-*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
