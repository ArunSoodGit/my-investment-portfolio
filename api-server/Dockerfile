# =========================
# Etap 1: Build (Maven + JDK)
# =========================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Kopiujemy Maven Wrapper i jego pliki
COPY mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw

# Kopiujemy cały projekt (multi-module)
COPY pom.xml ./pom.xml
COPY api-model api-model
COPY api-server api-server
COPY market-data-service market-data-service
COPY portfolio-service portfolio-service
COPY transaction-service transaction-service

# Budujemy wszystkie moduły, pomijając testy
RUN ./mvnw clean package -DskipTests

# =========================
# Etap 2: Runtime (uruchomienie api-server)
# =========================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Kopiujemy tylko gotowy JAR api-server z etapu build
COPY --from=build /app/api-server/target/*.jar app.jar

# Port, na którym działa api-server
EXPOSE 8081

# Polecenie startowe
ENTRYPOINT ["java", "-jar", "app.jar"]
