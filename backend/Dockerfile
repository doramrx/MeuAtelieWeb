FROM maven:3.9.6-eclipse-temurin-17 as build-stage

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17.0.10_7-jdk-jammy

WORKDIR /backend

COPY --from=build-stage /build/target/backend-0.0.1-SNAPSHOT.jar /backend

ENTRYPOINT ["java", "-jar","-Duser.country=BR", "-Duser.language=pt", "backend-0.0.1-SNAPSHOT.jar"]