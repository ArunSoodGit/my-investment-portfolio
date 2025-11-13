# --------------------------
# Etap build: budowanie JAR
# --------------------------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Kopiujemy Maven Wrapper i pliki pom
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Pobieramy zależności offline
RUN ./mvnw dependency:go-offline

# Kopiujemy kod źródłowy
COPY src src

# Budujemy JAR (pomijamy testy dla szybszego builda)
RUN ./mvnw clean package -DskipTests

# --------------------------
# Etap run: uruchamianie JAR
# --------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiujemy gotowy JAR z etapu build
COPY --from=build /app/target/*.jar app.jar

# Port, na którym aplikacja będzie nasłuchiwać w Railway
EXPOSE 8080

# Uruchomienie aplikacji
ENTRYPOINT ["java", "-jar", "app.jar"]
