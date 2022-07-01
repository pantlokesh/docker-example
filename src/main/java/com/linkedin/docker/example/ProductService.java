package com.linkedin.docker.example;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service
public class ProductService implements IProductService {
  private final Map<String, Product> productMap = new HashMap<>();

  public ProductService() {
    productMap.put("100", new Product(100, "Mobile", "CLK98123", 9000.00, 6));
    productMap.put("101", new Product(101, "Smart TV", "LGST09167", 60000.00, 3));
//    productMap.put("102", new Product(102, "Washing Machine", "38753BK9", 9000.00, 7));
//    productMap.put("103", new Product(103, "Laptop", "LHP29OCP", 24000.00, 1));
//    productMap.put("104", new Product(104, "Air Conditioner", "ACLG66721", 30000.00, 5));
//    productMap.put("105", new Product(105, "Refrigerator ", "12WP9087", 10000.00, 4));
  }

  @Override
  public Collection<Product> findAll() {
    return productMap.values();
  }

  @Override
  public Product find(String id) {
    return productMap.get(id);
  }

  @Override
  public void add(Product product) {
    productMap.put(String.valueOf(product.getId()), product);
  }

  @Override
  public void delete(String id) {
    productMap.remove(id);
  }
}
