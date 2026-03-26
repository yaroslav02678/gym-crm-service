FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY target/gym-service-1.0.0.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", \
    "-Dspring.profiles.active=dev", \
    "-Dspring.datasource.url=jdbc:h2:mem:gymdb;DB_CLOSE_DELAY=-1", \
    "-Dspring.datasource.driver-class-name=org.h2.Driver", \
    "-Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect", \
    "-Dspring.activemq.broker-url=vm://localhost?broker.persistent=false", \
    "-jar", "app.jar"]