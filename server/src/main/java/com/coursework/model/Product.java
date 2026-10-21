package com.coursework.model;

public class Product {
    private Long id;
    private String name;
    private String sku;
    private double price;
    private Long supplierId;

    public Product() {}
    public Product(Long id, String name, String sku, double price, Long supplierId) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.supplierId = supplierId;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getsku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
}