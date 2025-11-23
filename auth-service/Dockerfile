FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/auth-service-*.jar app.jar

EXPOSE 8082 50050

ENV JWT_SECRET=my-secret-key-for-jwt-token-generation-and-validation
ENV JWT_EXPIRATION=3600000

ENTRYPOINT ["java", "-jar", "app.jar"]
