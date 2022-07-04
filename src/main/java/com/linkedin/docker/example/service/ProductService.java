package com.linkedin.docker.example.service;

import com.linkedin.docker.example.entity.Product;
import com.linkedin.docker.example.repository.ProductRepository;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;


@Service
public class ProductService implements IProductService {
  private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
  private final ProductRepository productRepository;
  private final SetOperations<Integer, Product> setOperations;

  @Autowired
  public ProductService(ProductRepository productRepository, RedisTemplate<Integer, Product> template) {
    this.productRepository = productRepository;
    this.setOperations = template.opsForSet();
  }

  @Override
  public Collection<Product> findAll() {
    return productRepository.findAll();
  }

  @Override
  public Product find(int id) {
    LOGGER.info("Trying to find in Redis =" + id);
    final Product product = Optional.ofNullable(setOperations.members(id))
        .stream()
        .flatMap(Collection::stream)
        .findFirst()
        .orElseGet(() -> {
          LOGGER.info("Not Found in Redis for id =" + id);
          return productRepository.findById(id).orElse(null);
        });
    if (product != null) {
      LOGGER.info("Found for id =" + id);
    } else {
      LOGGER.info("Not found in Redis or Mongo for id =" + id);
    }
    return product;
  }

  @Override
  public void add(Product product) {
    if (product.getId() % 5 == 0) {
      LOGGER.info("Saved in Redis for id =" + product.getId());
      product.setSource("Redis");
      setOperations.add(product.getId(), product);
    }
    LOGGER.info("Saved in Mongo for id =" + product.getId());
    product.setSource("Mongo");
    productRepository.save(product);
  }

  @Override
  public Product delete(int id) {
    final Product product = find(id);
    productRepository.deleteById(id);
    return Optional.ofNullable(deleteFromCache(id))
        .map(p -> p.setSourceAndGetProduct("Redis & Mongo"))
        .orElse(product);
  }

  @Override
  public Product deleteFromCache(int id) {
    return setOperations.pop(id);
  }
}
