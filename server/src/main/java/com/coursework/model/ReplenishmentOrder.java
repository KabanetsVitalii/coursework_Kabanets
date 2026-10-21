package com.coursework.model;
import java.time.LocalDate;

public class ReplenishmentOrder {
    private Long id;
    private Long productId;
    private int quantity;
    private OrderState state;
    private LocalDate orderDate;

    public ReplenishmentOrder() {
        this.state = OrderState.CREATED; // Початковий стан
        this.orderDate = LocalDate.now();
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public OrderState getState() { return state; }
    public void setState(OrderState state) { this.state = state; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
}