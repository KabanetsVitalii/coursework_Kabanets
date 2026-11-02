package ua.edu.nupp.kpp.client;

/**
 * Стани життєвого циклу замовлення поповнення. Імена (name) збігаються з серверним enum,
 * бо стан передається через HTTP/JSON. toString() дає підпис для ComboBox і TableView.
 */
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
