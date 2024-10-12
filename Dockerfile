FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY ./pom.xml ./
COPY ./src ./src
RUN mvn clean install -DskipTests



FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar ./app.jar

EXPOSE 9000

ENTRYPOINT [ "java", "-jar", "./app.jar" ]