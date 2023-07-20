FROM openjdk:17
WORKDIR /app
COPY /target/format_changer-0.0.1-SNAPSHOT.jar format-changer.jar
ENTRYPOINT ["java", "-jar", "./format-changer.jar"]