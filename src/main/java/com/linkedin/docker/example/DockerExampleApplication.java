package com.linkedin.docker.example;

import com.linkedin.docker.example.entity.Product;
import com.linkedin.docker.example.repository.ProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableMongoRepositories(basePackageClasses = {Product.class, ProductRepository.class})
public class DockerExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(DockerExampleApplication.class, args);
  }
}
