# --------------------------
# Etap build: budowanie całego projektu
# --------------------------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Kopiujemy Maven Wrapper i główny pom.xml
COPY mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw

# Pobieramy zależności offline
RUN ./mvnw dependency:go-offline

# Kopiujemy wszystkie moduły multi-module
COPY pom.xml ./pom.xml
COPY api-model api-model
COPY api-server api-server
COPY market-data-service market-data-service
COPY portfolio-service portfolio-service
COPY transaction-service transaction-service

# Budujemy cały projekt (pomijamy testy)
RUN ./mvnw clean package -DskipTests

# --------------------------
# Etap run: uruchamianie api-server
# --------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiujemy tylko JAR api-server
COPY --from=build /app/api-server/target/*.jar app.jar

# Ustawiamy port dla Railway
EXPOSE 8081

# Uruchomienie serwisu
ENTRYPOINT ["java", "-jar", "app.jar"]
