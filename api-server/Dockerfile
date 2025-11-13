# Etap build: budowanie JAR
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Kopiujemy Maven Wrapper i pliki pom
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Nadajemy prawa do wykonywania mvnw
RUN chmod +x mvnw

# Pobieramy zależności offline
RUN ./mvnw dependency:go-offline

# Kopiujemy kod źródłowy
COPY src src

# Budujemy JAR (pomijamy testy)
RUN ./mvnw clean package -DskipTests

# Etap run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
