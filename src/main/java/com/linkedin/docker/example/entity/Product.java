package com.linkedin.docker.example.entity;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product implements Serializable {
  @Id
  private int id;
  private String name;
  private String batchNo;
  private double price;
  private int noOfProduct;
  private String source;

  //default constructor
  public Product() {

  }

  //constructor using fields
  public Product(int id, String name, String batchNo, double price, int noOfProduct) {
    super();
    this.id = id;
    this.name = name;
    this.batchNo = batchNo;
    this.price = price;
    this.noOfProduct = noOfProduct;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBatchNo() {
    return batchNo;
  }

  public void setBatchNo(String batchNo) {
    this.batchNo = batchNo;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getNoOfProduct() {
    return noOfProduct;
  }

  public void setNoOfProduct(int noOfProduct) {
    this.noOfProduct = noOfProduct;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Product setSourceAndGetProduct(String source) {
    setSource(source);
    return this;
  }

  @Override
  public String toString() {
    return "Product{" + "id=" + id + ", name='" + name + '\'' + ", batchNo='" + batchNo + '\'' + ", price=" + price
        + ", noOfProduct=" + noOfProduct + ", source='" + source + '\'' + '}';
  }
}
