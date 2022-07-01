
Milestone 1 - Spring Boot Application running on Docker.
1. Build Project `./gradlew clean build`
2. Create Docker Image `docker build -t docker-example:0.0.1 .`
3. Run docker image `docker run --name docker-example -dp 8082:8080 docker-example:0.0.1`
4. Check running docker container `docker ps`
5. Check logs for docker container `docker logs docker-example`