# ============================================
# Multi-stage build — Poker Texas Hold'em
# ============================================

# Stage 1: Build with Maven + JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy sources & build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Lightweight runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
