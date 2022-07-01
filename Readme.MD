
Milestone 1 - Spring Boot Application running on Docker.
1. Build Project `./gradlew clean build`
2. Create Docker Image `docker build -t docker-example:0.0.1 .`
3. Run docker image `docker run --name docker-example -dp 8082:8080 docker-example:0.0.1`
4. Check running docker container `docker ps`
5. Check logs for docker container `docker logs docker-example`

Milestone 2 - Added POST, DELETE and GET for id APIs.
1. Get all products - `curl 'localhost:8080/product'`
2. Get one product - `curl 'localhost:8080/product/100'`
3. Add a product - `curl -XPOST 'localhost:8080/product' --header 'Content-Type: application/json' --data-raw '{"id": 102,"name": "Washing Machine","batchNo": "38753BK9","price": 9000.00,"noOfProduct": 7}'`
4. Delete one product - `curl -XDELETE 'localhost:8080/product/102'`

Milestone 3 - Moved storage to Database