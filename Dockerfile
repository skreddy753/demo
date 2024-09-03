FROM openjdk:11-jdk-slim
EXPOSE 8080

ADD target/demo.jar demo.jar
ENTRYPOINT ["java", "-jar", "/demo.jar"]

