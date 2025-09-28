FROM eclipse-temurin:21-jdk-jammy
ARG JAR_FILE=target/*.jar
COPY ./target/music-library-service-0.0.1-SNAPSHOT.jar app.jar
LABEL authors="Kasun Chamara"

ENTRYPOINT ["java", "-jar", "app.jar"]
