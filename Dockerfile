# ---- Build stage ----
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build without using ./mvnw (skip tests for faster build)
RUN mvn clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*-runner.jar app.jar

# Expose the port your Quarkus app runs on (default is 8080)
EXPOSE 8080

# Start the app
CMD ["java", "-jar", "app.jar"]
