FROM openjdk:latest

COPY ./build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/app.jar"]