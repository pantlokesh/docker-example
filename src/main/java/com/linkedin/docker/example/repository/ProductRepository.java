package com.linkedin.docker.example.repository;

import com.linkedin.docker.example.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, Integer> {
}

