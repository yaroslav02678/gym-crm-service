FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY target/trainer-workload-service-1.0.0-exec.jar app.jar
EXPOSE 8081

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]