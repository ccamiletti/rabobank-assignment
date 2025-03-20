FROM openjdk:17

WORKDIR /app

COPY ./target/rabobank-assignment.jar /app

EXPOSE 8080

CMD ["java", "-jar", "rabobank-assignment.jar"]