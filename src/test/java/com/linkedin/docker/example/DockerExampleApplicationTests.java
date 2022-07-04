package com.linkedin.docker.example;

import com.linkedin.docker.example.entity.Product;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.codec.RedisCodec;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.io.File;
import java.util.Optional;
import java.util.Set;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.*;
import static com.mongodb.client.model.Filters.eq;


@Testcontainers
class DockerExampleApplicationTests {
  private static final int REDIS_PORT = 6379;
  private static final int MONGO_PORT = 27017;
  private static final int BACKEND_PORT = 8080;
  private static final String REDIS_SERVICE = "docker-example-redis_1";
  private static final String MONGO_SERVICE = "docker-example-mongo_1";
  private static final String EXAMPLE_SERVICE = "docker-example_1";

  public static DockerComposeContainer environment;

  @BeforeAll
  public static void setUpClass() {
    environment =
        new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
            .withExposedService(REDIS_SERVICE, REDIS_PORT, Wait.forListeningPort())
            .withExposedService(MONGO_SERVICE, MONGO_PORT, Wait.forListeningPort())
            .withExposedService(EXAMPLE_SERVICE, BACKEND_PORT, Wait.forListeningPort())
            .waitingFor(EXAMPLE_SERVICE, Wait.forLogMessage(".* Started DockerExampleApplication .*", 1));

    environment.start();
  }

  @AfterAll
  public static void tearDownClass() {
    environment.stop();
  }

  @Test
  public void test_this() {
    System.out.printf("Docker Redis - %s \n", redisConnectionString());
    System.out.printf("Docker Mongo - %s \n", mongoConnectionString());
    System.out.printf("Example Application - %s \n", backendConnectionString());

    System.out.println("Making call to application");

    Response post = given().baseUri(backendConnectionString())
        .basePath("/product")
        .body("{\n" + "\"id\": 110,\n" + "\"name\": \"Washing Machine\", \n" + "\"batchNo\": \"38753BK9\",\n"
            + "\"price\": 9000.00,\n" + "\"noOfProduct\": 7\n" + "}")
        .contentType(ContentType.JSON)
        .post();

    System.out.println(post.asString());

    System.out.println("Checking in Redis");

    final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration( environment.getServiceHost(REDIS_SERVICE, REDIS_PORT) , environment.getServicePort(
        REDIS_SERVICE, REDIS_PORT));
    final RedisTemplate<Integer, Product> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
    redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
    JedisConnectionFactory connectionFactory = new JedisConnectionFactory(config);
    connectionFactory.afterPropertiesSet();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.afterPropertiesSet();
    Set<Product> members = redisTemplate.opsForSet().members(110);
    System.out.println("Redis value for key 110-" + members.stream().findFirst().orElse(null));
    System.out.println("Hello");

    final String uri = mongoConnectionString();

    try (MongoClient mongoClient = MongoClients.create(uri)) {
      MongoDatabase database = mongoClient.getDatabase("example");
      MongoCollection<Document> collection = database.getCollection("products");

      Bson projectionFields = Projections.fields(
          Projections.include("name", "batchNo", "price"));
      Document doc = collection.find(eq("_id", 110))
          .projection(projectionFields)
          .first();
      System.out.println(Optional.ofNullable(doc).map(Document::toJson).orElse(null));
    }
  }

  private String redisConnectionString() {
//    return "redis://localhost:6379";
    return "redis://" + environment.getServiceHost(REDIS_SERVICE, REDIS_PORT) + ":" + environment.getServicePort(
        REDIS_SERVICE, REDIS_PORT);
  }

  private String mongoConnectionString() {
//    return "mongodb://localhost:27017";
    return "mongodb://" + environment.getServiceHost(MONGO_SERVICE, MONGO_PORT) + ":" + environment.getServicePort(
        MONGO_SERVICE, MONGO_PORT);
  }

  private String backendConnectionString() {
//    return "http://localhost:8080";
    return "http://" + environment.getServiceHost(EXAMPLE_SERVICE, BACKEND_PORT) + ":" + environment.getServicePort(
        EXAMPLE_SERVICE, BACKEND_PORT);
  }
}
