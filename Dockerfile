FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY checkstyle.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-jdk AS runner

WORKDIR /app

COPY --from=builder ./app/target/contractor-service-0.0.1-SNAPSHOT.jar ./app.jar
COPY --from=builder ./app/src/main/resources ./app/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]