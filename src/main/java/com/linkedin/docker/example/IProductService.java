package com.linkedin.docker.example;

import java.util.Collection;

public interface IProductService {
  Collection<Product> findAll();

  Product find(String id);

  void add(Product product);

  void delete(String id);
}
