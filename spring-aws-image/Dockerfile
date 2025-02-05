FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY target/spring-aws-image*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
