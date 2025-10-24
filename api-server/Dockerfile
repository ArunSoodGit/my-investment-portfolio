# api-server/Dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/api-server-0.1.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]