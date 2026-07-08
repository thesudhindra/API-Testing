# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S playground && adduser -S playground -G playground
USER playground

COPY enterprise-api/target/enterprise-api-*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
