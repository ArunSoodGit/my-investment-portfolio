# notification-service/Dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/notification-service-0.1.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]