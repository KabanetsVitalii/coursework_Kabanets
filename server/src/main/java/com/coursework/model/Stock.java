package com.coursework.model;

public class Stock {
    private Long id;
    private Long productId;
    private int quantity;
    private int minThreshold;

    public Stock() {}
    public Stock(Long id, Long productId, int quantity, int minThreshold) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.minThreshold = minThreshold;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getMinThreshold() { return minThreshold; }
    public void setMinThreshold(int minThreshold) { this.minThreshold = minThreshold; }
}