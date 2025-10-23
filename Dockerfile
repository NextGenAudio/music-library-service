# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy the Maven or Gradle wrapper and project files
COPY . .

# Build the JAR file (use the correct command for your build tool)
# For Maven:
RUN ./mvnw clean package -DskipTests
# For Gradle, use this instead:
# RUN ./gradlew build -x test

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar
# (If you use Gradle, replace with /app/build/libs/*.jar)

# Expose the port your Spring app runs on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
