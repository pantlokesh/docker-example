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
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.*;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
class DockerExampleApplicationTests {
  private static final int REDIS_PORT = 6379;
  private static final int MONGO_PORT = 27017;
  private static final int BACKEND_PORT = 8080;
  private static final String REDIS_SERVICE = "docker-example-redis_1";
  private static final String MONGO_SERVICE = "docker-example-mongo_1";
  private static final String EXAMPLE_SERVICE = "docker-example_1";

  public static DockerComposeContainer environment;

  private static SetOperations<Integer, Product> redisSetOpClient;
  private static MongoCollection<Document> collection;

  @BeforeAll
  public static void setUpClass() {
    environment =
        new DockerComposeContainer(new File("docker-compose.yml")).withExposedService(REDIS_SERVICE,
                REDIS_PORT, Wait.forListeningPort())
            .withExposedService(MONGO_SERVICE, MONGO_PORT, Wait.forListeningPort())
            .withExposedService(EXAMPLE_SERVICE, BACKEND_PORT, Wait.forListeningPort())
            .waitingFor(EXAMPLE_SERVICE, Wait.forLogMessage(".* Started DockerExampleApplication .*", 1));

    environment.start();
    final RedisStandaloneConfiguration config =
        new RedisStandaloneConfiguration(environment.getServiceHost(REDIS_SERVICE, REDIS_PORT),
            environment.getServicePort(REDIS_SERVICE, REDIS_PORT));
    final RedisTemplate<Integer, Product> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
    redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
    JedisConnectionFactory connectionFactory = new JedisConnectionFactory(config);
    connectionFactory.afterPropertiesSet();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.afterPropertiesSet();
    redisSetOpClient = redisTemplate.opsForSet();
    final String uri = mongoConnectionString();
    MongoClient mongoClient = MongoClients.create(uri);
    MongoDatabase database = mongoClient.getDatabase("example");
    collection = database.getCollection("products");
  }

  @AfterAll
  public static void tearDownClass() {
    environment.stop();
  }

  @Test
  public void product_addANewProduct_productPresentInRedisMongoAndAlsoThroughGetAPI() {
    final String postBody =
        "{\n" + "\"id\": 110,\n" + "\"name\": \"Washing Machine\", \n" + "\"batchNo\": \"38753BK9\",\n"
            + "\"price\": 9000.00,\n" + "\"noOfProduct\": 7\n" + "}";
    final Response post = given().baseUri(backendConnectionString())
        .basePath("/product")
        .body(postBody)
        .contentType(ContentType.JSON)
        .post();

    assertEquals(200, post.getStatusCode());

    // Assert Data in Redis
    final Set<Product> members = redisSetOpClient.members(110);
    final Product productFromRedis = members.stream().findFirst().orElse(null);
    assertNotNull(productFromRedis);
    assertEquals("Washing Machine", productFromRedis.getName());
    assertEquals("38753BK9", productFromRedis.getBatchNo());
    assertEquals("Redis", productFromRedis.getSource());

    // Assert Data in Mongo
    final Document productInMongo = collection.find(eq("_id", 110)).first();
    assertNotNull(productInMongo);
    assertEquals("Washing Machine", productInMongo.get("name"));
    assertEquals("Mongo", productInMongo.get("source"));
    assertEquals(7, productInMongo.get("noOfProduct"));


    // Assert Data can be fetched from GET API
    final Response get = given().baseUri(backendConnectionString())
        .basePath("/product")
        .accept(ContentType.JSON)
        .get("/110");

    assertEquals(200, get.getStatusCode());
    final Product productFromGetRequest = get.getBody().as(Product.class);
    assertEquals("Washing Machine", productFromGetRequest.getName());
    assertEquals("38753BK9", productFromGetRequest.getBatchNo());
    assertEquals("Redis", productFromGetRequest.getSource());
  }

  private static String mongoConnectionString() {
    return "mongodb://" + environment.getServiceHost(MONGO_SERVICE, MONGO_PORT) + ":" + environment.getServicePort(
        MONGO_SERVICE, MONGO_PORT);
  }

  private static String backendConnectionString() {
    return "http://" + environment.getServiceHost(EXAMPLE_SERVICE, BACKEND_PORT) + ":" + environment.getServicePort(
        EXAMPLE_SERVICE, BACKEND_PORT);
  }
}
