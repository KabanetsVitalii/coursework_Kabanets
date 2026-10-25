package ua.edu.nupp.kpp.server;

public class ReplenishmentOrder {
    private Long id;
    private long stockId;
    private OrderState state;
    private double f1;
    private double f2;
    private double f3;
    private double riskIndex;
    private String orderDate;

    public ReplenishmentOrder() {
        this.state = OrderState.CREATED;
    }
    public ReplenishmentOrder(Long id, long stockId, OrderState state, double f1, double f2, double f3, double riskIndex, String orderDate) {
        this.id = id;
        this.stockId = stockId;
        this.state = state;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.riskIndex = riskIndex;
        this.orderDate = orderDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getStockId() { return stockId; }
    public void setStockId(long stockId) { this.stockId = stockId; }

    public OrderState getState() { return state; }
    public void setState(OrderState state) { this.state = state; }

    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }

    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }

    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }

    public double getRiskIndex() { return riskIndex; }
    public void setRiskIndex(double riskIndex) { this.riskIndex = riskIndex; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
}