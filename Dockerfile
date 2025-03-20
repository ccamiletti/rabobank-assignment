# FROM maven:3.9.8-eclipse-temurin-21 AS build
#
# COPY /src /app/src
#
# COPY /pom.xml /app
#
# WORKDIR /app
# RUN mvn clean install -U

FROM openjdk:17
COPY --from=build /app/target/rabobank-assignment.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]