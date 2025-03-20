FROM openjdk:17
COPY --from=build /app/target/rabobank-assignment.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]