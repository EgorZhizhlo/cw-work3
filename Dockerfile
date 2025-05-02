# Stage 1: build
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем pom и исходники, собираем jar
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

# Копируем собранный jar из build-стадии
COPY --from=build /app/target/cinema-info-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
