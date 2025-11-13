# --------------------------
# Etap build: budowanie portfolio-service
# --------------------------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Kopiujemy Maven Wrapper i pom.xml modułu portfolio-service
COPY portfolio-service/pom.xml ./pom.xml
COPY portfolio-service/.mvn .mvn
COPY mvnw ./
RUN chmod +x mvnw

# Pobieramy zależności offline
RUN ./mvnw dependency:go-offline

# Kopiujemy cały kod źródłowy portfolio-service
COPY portfolio-service/src ./src

# Budujemy projekt (pomijamy testy)
RUN ./mvnw clean package -DskipTests

# --------------------------
# Etap run: uruchamianie portfolio-service
# --------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiujemy JAR z etapu build
COPY --from=build /app/target/*.jar app.jar

# Port, na którym Railway uruchomi serwis
EXPOSE 8080

# Uruchomienie aplikacji
ENTRYPOINT ["java", "-jar", "app.jar"]
