FROM openjdk:8-jdk-alpine
EXPOSE 8080

ADD target/demo.jar demo.jar
ENTRYPOINT ["java", "-jar", "/demo.jar"]

