package ua.edu.nupp.kpp.server;

public class Product {
    private Long id;
    private long supplierId;
    private String name;
    private double unitPrice;

    public Product() {}
    public Product(Long id, long supplierId, String name, double unitPrice) {
        this.id = id;
        this.supplierId = supplierId;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getSupplierId() { return supplierId; }
    public void setSupplierId(long supplierId) { this.supplierId = supplierId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}