package com.linkedin.docker.example.controller;

import com.linkedin.docker.example.service.IProductService;
import com.linkedin.docker.example.entity.Product;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ProductController {
  private final IProductService productService;

  @Autowired
  public ProductController(IProductService productService) {
    this.productService = productService;
  }

  @GetMapping(value = "/product")
  public Collection<Product> getProduct() {
    return productService.findAll();
  }

  @GetMapping(value = "/product/{id}")
  public Product getProduct(@PathVariable int id) {
    return productService.find(id);
  }

  @PostMapping(value = "/product")
  public void addProduct(@RequestBody Product product) {
    productService.add(product);
  }

  @DeleteMapping(value = "/product/{id}")
  public Product deleteProduct(@PathVariable int id) {
    return productService.delete(id);
  }

  @DeleteMapping(value = "/product/cache/{id}")
  public Product deleteProductFromCache(@PathVariable int id) {
    return productService.deleteFromCache(id);
  }
}