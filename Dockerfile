# base image with a JRE
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# copy your already-built JAR from the CI workspace
COPY target/bylt-api.jar app.jar

# tell Java how to start
ENTRYPOINT ["java", "-jar", "app.jar"]