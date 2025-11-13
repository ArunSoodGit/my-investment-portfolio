# --------------------------
# Etap build: budowanie api-server
# --------------------------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Kopiujemy Maven Wrapper i główny pom
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Nadajemy prawa do wykonywania mvnw
RUN chmod +x mvnw

# Pobieramy zależności offline
RUN ./mvnw dependency:go-offline

# Kopiujemy wszystkie moduły potrzebne do budowy api-server
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

# Kopiujemy JAR mikroserwisu api-server z etapu build
COPY --from=build /app/api-server/target/*.jar app.jar

# Port, na którym aplikacja będzie nasłuchiwać w Railway
EXPOSE 8081

# Uruchomienie aplikacji
ENTRYPOINT ["java", "-jar", "app.jar"]
