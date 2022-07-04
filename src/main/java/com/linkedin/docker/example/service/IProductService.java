package com.linkedin.docker.example.service;

import com.linkedin.docker.example.entity.Product;
import java.util.Collection;

public interface IProductService {
  Collection<Product> findAll();

  Product find(int id);

  void add(Product product);

  Product delete(int id);

  Product deleteFromCache(int id);
}
