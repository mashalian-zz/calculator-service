# How to run calculator-service
1.Navigate to root of project.

2.Build the project(mvn clean install)

3.Build docker image(docker build -t calculator-service-docker.jar .)

4.Run docker(docker run -p 9090:8080 calculator-service-docker.jar)

5.User Swagger ui to see the endpoints(http://localhost:9090/swagger-ui/index.html)


