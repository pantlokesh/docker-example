package com.linkedin.docker.example.service;

import com.linkedin.docker.example.entity.Product;
import java.util.Collection;

public interface IProductService {
  Collection<Product> findAll();

  Product find(String id);

  void add(Product product);

  void delete(String id);
}
