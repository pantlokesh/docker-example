package com.linkedin.docker.example;

public class Product {
  private int id;
  private String name;
  private String batchNo;
  private double price;
  private int noOfProduct;

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

}
