# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S playground && adduser -S playground -G playground

COPY playground-api/target/playground-api-*.jar app.jar
COPY contracts /app/contracts

USER playground

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
