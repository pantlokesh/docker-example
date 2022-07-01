Milestone 1 - Spring Boot Application running on Docker.

1. Build Project `./gradlew clean build`
2. Create Docker Image `docker build -t docker-example:0.0.1 .`
3. Run docker image `docker run --name docker-example -dp 8082:8080 docker-example:0.0.1`
4. Check running docker container `docker ps`
5. Check logs for docker container `docker logs docker-example`

Milestone 2 - Added POST, DELETE and GET for id APIs.

1. Get all products - `curl 'localhost:8080/product'`
2. Get one product - `curl 'localhost:8080/product/100'`
3. Add a product
   - `curl -XPOST 'localhost:8080/product' --header 'Content-Type: application/json' --data-raw '{"id": 102,"name": "Washing Machine","batchNo": "38753BK9","price": 9000.00,"noOfProduct": 7}'`
4. Delete one product - `curl -XDELETE 'localhost:8080/product/102'`

Milestone 3 - Moved storage to Database (Mongo)

1. Create a docker network - `docker network create example-network`
2. List all docker networks - `docker network ls`
3. Run Mongo container and attach it to the network created in step 1
   - `docker run --name docker-example-mongo -p 27017:27017 --network example-network -d mongo:latest`
4. Run application and attach it to the same network
   - `docker run -dp 8081:8080 --name docker-example --network example-network docker-example:0.0.1`

**Caveat**

Please make sure the name of the docker container in step 3 is the same as the property `spring.data.mongodb.host`
in `src/main/resources/application.properties`. Also make sure to build the project and docker images again after making
any changes (Step 1 and 2 of Milestone 1)

Exercise. Which of the Curl commands would fail and which would succeed? -

1. `curl localhost:8080/product`
2. `curl docker-example:8080/product`
3. `docker run --rm curlimages/curl -L -v docker-example:8080/product`
4. `docker run --network example-network --rm curlimages/curl -L -v docker-example:8080/product`