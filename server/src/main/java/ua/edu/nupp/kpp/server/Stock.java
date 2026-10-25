package ua.edu.nupp.kpp.server;

public class Stock {
    private Long id;
    private long productId;
    private double currentQuantity;
    private double minThreshold;
    private double f2;
    private double f3;
    public Stock() {}
    public Stock(Long id, long productId, double currentQuantity, double minThreshold, double f2, double f3) {
        this.id = id;
        this.productId = productId;
        this.currentQuantity = currentQuantity;
        this.minThreshold = minThreshold;
        this.f2 = f2;
        this.f3 = f3;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public double getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(double currentQuantity) { this.currentQuantity = currentQuantity; }

    public double getMinThreshold() { return minThreshold; }
    public void setMinThreshold(double minThreshold) { this.minThreshold = minThreshold; }

    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }

    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }
}