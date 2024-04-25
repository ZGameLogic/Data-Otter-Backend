FROM ubuntu:latest
LABEL authors="Ben Shabowski"

FROM openjdk:21

WORKDIR /app
COPY /target/Monitors-1.0.0.jar /app/Monitors-1.0.0.jar

RUN mkdir /app/history
VOLUME /app/history

EXPOSE 8080

CMD ["java", "-jar", "Monitors-1.0.0.jar"]
