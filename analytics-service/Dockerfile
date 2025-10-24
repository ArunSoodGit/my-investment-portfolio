# analytics-service/Dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/analytics-service-0.1.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]