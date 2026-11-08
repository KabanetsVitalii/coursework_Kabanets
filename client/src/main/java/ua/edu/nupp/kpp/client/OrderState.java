package ua.edu.nupp.kpp.client;

public enum OrderState {
    CREATED("СТВОРЕНО"),
    APPROVED("СХВАЛЕНО"),
    ORDERED("ЗАМОВЛЕНО"),
    RECEIVED("ОТРИМАНО");

    private final String label;

    OrderState(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label + " (" + name() + ")";
    }
}
