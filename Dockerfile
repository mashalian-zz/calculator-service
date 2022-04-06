FROM openjdk:17-alpine
EXPOSE 8080
ADD target/calculator-service-docker.jar calculator-service-docker.jar
ENTRYPOINT ["java","-jar","/calculator-service-docker.jar"]