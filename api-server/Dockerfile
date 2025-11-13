# Etap build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Kopiujemy Maven Wrapper i .mvn
COPY mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw

# Kopiujemy wszystkie moduły + główny pom.xml
COPY pom.xml ./pom.xml
COPY api-model api-model
COPY api-server api-server
COPY market-data-service market-data-service
COPY portfolio-service portfolio-service
COPY transaction-service transaction-service

# Budujemy cały projekt od razu, pomijając testy
RUN ./mvnw clean package -DskipTests

# Etap run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiujemy tylko JAR api-server
COPY --from=build /app/api-server/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
