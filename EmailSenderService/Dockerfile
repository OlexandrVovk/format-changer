FROM openjdk:17
WORKDIR /email-sender-service
COPY /target/EmailSenderService-0.0.1-SNAPSHOT.jar email-sender-service.jar
ENTRYPOINT ["java", "-jar", "./email-sender-service.jar"]