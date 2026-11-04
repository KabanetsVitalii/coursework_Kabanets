package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());

    private final HttpReplenishmentClient api = new HttpReplenishmentClient();
    private final TableView<Supplier> suppliers = new TableView<>();
    private final TableView<Product> products = new TableView<>();
    private final TableView<Stock> stocks = new TableView<>();
    private final TableView<ReplenishmentOrder> orders = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Постачальники", supplierPane()),
                new Tab("Товари", productPane()),
                new Tab("Запаси", stockPane()),
                new Tab("Замовлення поповнення", orderPane()));
        tabs.getTabs().forEach(t -> t.setClosable(false));
        stage.setScene(new Scene(tabs, 1100, 650));
        stage.setTitle("Поповнення складських запасів");
        stage.show();
        refreshAll();
    }
    // Постачальник
    private Pane supplierPane() {
        suppliers.getColumns().setAll(col("ID", "id"), col("Назва", "name"),
                col("Контакти", "contactInfo"), col("Термін доставки, днів", "deliveryDays"));
        TextField name = new TextField(), contact = new TextField(), days = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addSupplier(new Supplier(null, name.getText(), contact.getText(), intOf(days, "Термін доставки")));
            refreshSuppliers();
        }));
        update.setOnAction(e -> safe(() -> {
            Supplier s = selected(suppliers);
            api.updateSupplier(new Supplier(s.getId(), name.getText(), contact.getText(), intOf(days, "Термін доставки")));
            refreshSuppliers();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteSupplier(selected(suppliers).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshSuppliers());
        suppliers.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                name.setText(s.getName());
                contact.setText(s.getContactInfo());
                days.setText("" + s.getDeliveryDays());
            }
        });
        return layout(suppliers, new VBox(8, new Label("Назва постачальника"), name,
                new Label("Контактна інформація"), contact,
                new Label("Термін доставки, днів"), days,
                new HBox(8, add, update, delete, refresh)));
    }

    //Товари
    private Pane productPane() {
        products.getColumns().setAll(col("ID", "id"), col("ID постачальника", "supplierId"),
                col("Назва товару", "name"), col("Ціна за одиницю", "unitPrice"));
        TextField supplierId = new TextField(), name = new TextField(), price = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addProduct(new Product(null, longOf(supplierId, "ID постачальника"), name.getText(),
                    doubleOf(price, "Ціна")));
            refreshProducts();
        }));
        update.setOnAction(e -> safe(() -> {
            Product p = selected(products);
            api.updateProduct(new Product(p.getId(), longOf(supplierId, "ID постачальника"), name.getText(),
                    doubleOf(price, "Ціна")));
            refreshProducts();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteProduct(selected(products).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshProducts());
        products.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> {
            if (p != null) {
                supplierId.setText("" + p.getSupplierId());
                name.setText(p.getName());
                price.setText("" + p.getUnitPrice());
            }
        });
        return layout(products, new VBox(8, new Label("ID постачальника"), supplierId,
                new Label("Назва товару"), name, new Label("Ціна за одиницю"), price,
                new HBox(8, add, update, delete, refresh)));
    }
    // Запаси
    private Pane stockPane() {
        stocks.getColumns().setAll(col("ID", "id"), col("ID товару", "productId"),
                col("Поточний запас", "currentQuantity"), col("Мінімальний поріг", "minThreshold"),
                col("F2 (інтенсивність споживання)", "f2"), col("F3 (ризик затримки постачання)", "f3"));
        TextField productId = new TextField(), quantity = new TextField(), threshold = new TextField(),
                f2 = new TextField(), f3 = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"),
                delete = new Button("Видалити"), refresh = new Button("Оновити"),
                importCsv = new Button("Імпортувати CSV");
        Label status = new Label("CSV-імпорт не запускався");

        add.setOnAction(e -> safe(() -> {
            api.addStock(readStock(null, productId, quantity, threshold, f2, f3));
            refreshStocks();
        }));
        update.setOnAction(e -> safe(() -> {
            Stock s = selected(stocks);
            api.updateStock(readStock(s.getId(), productId, quantity, threshold, f2, f3));
            refreshStocks();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteStock(selected(stocks).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshStocks());
        stocks.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                productId.setText("" + s.getProductId());
                quantity.setText("" + s.getCurrentQuantity());
                threshold.setText("" + s.getMinThreshold());
                f2.setText("" + s.getF2());
                f3.setText("" + s.getF3());
            }
        });
        importCsv.setOnAction(e -> safe(() -> {
            try {
                StockCsvImporter.Result r = StockCsvImporter.importFile(Path.of("sample-data", "inventory.csv"), api);
                status.setText("Імпортовано: " + r.getImported() + ", пропущено: " + r.getSkipped().size());
                if (!r.getSkipped().isEmpty()) {
                    LOG.warning("Пропущені рядки CSV: " + r.getSkipped());
                }
            } catch (IOException ex) {
                throw new RuntimeException("Не вдалося прочитати CSV-файл: " + ex.getMessage(), ex);
            }
            refreshStocks();
        }));
        return layout(stocks, new VBox(6, new Label("ID товару"), productId,
                new Label("Поточний запас"), quantity, new Label("Мінімальний поріг"), threshold,
                new Label("F2 — інтенсивність споживання (0–100)"), f2,
                new Label("F3 — ризик затримки постачання (0–100)"), f3,
                new HBox(8, add, update, delete, refresh), importCsv, status));
    }
    // Замовлення поповнення
    private Pane orderPane() {
        orders.getColumns().setAll(col("ID", "id"), col("ID запасу", "stockId"), col("Стан", "state"),
                col("F1 (дефіцит запасу)", "f1"), col("F2", "f2"), col("F3", "f3"),
                col("I (індекс ризику)", "riskIndex"), col("Дата", "orderDate"));
        TextField stockId = new TextField();
        ComboBox<OrderState> state = new ComboBox<>(FXCollections.observableArrayList(OrderState.values()));
        Button add = new Button("Створити"), update = new Button("Перерахувати"),
                change = new Button("Змінити стан"), delete = new Button("Видалити"),
                refresh = new Button("Оновити");
        // Клієнт передає лише ID запасу: F1, F2, F3 та I розраховує сервер (ReplenishmentOrderService)
        add.setOnAction(e -> safe(() -> {
            api.addOrder(longOf(stockId, "ID запасу"));
            refreshOrders();
        }));
        update.setOnAction(e -> safe(() -> {
            api.updateOrder(selected(orders).getId(), longOf(stockId, "ID запасу"));
            refreshOrders();
        }));
        // Допустимість переходу перевіряє сервер; клієнт лише показує результат або помилку
        change.setOnAction(e -> safe(() -> {
            if (state.getValue() == null) {
                throw new IllegalArgumentException("Виберіть новий стан");
            }
            api.changeOrderState(selected(orders).getId(), state.getValue());
            refreshOrders();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteOrder(selected(orders).getId());
            refreshOrders();
        }));
        refresh.setOnAction(e -> refreshOrders());
        orders.getSelectionModel().selectedItemProperty().addListener((o, a, v) -> {
            if (v != null) {
                stockId.setText("" + v.getStockId());
            }
        });
        return layout(orders, new VBox(8, new Label("ID запасу"), stockId,
                new Label("Новий стан замовлення"), state,
                new HBox(8, add, update, change, delete, refresh)));
    }
    // допоміжні методи
    private Stock readStock(Long id, TextField productId, TextField quantity, TextField threshold,
                            TextField f2, TextField f3) {
        return new Stock(id, longOf(productId, "ID товару"), doubleOf(quantity, "Поточний запас"),
                doubleOf(threshold, "Мінімальний поріг"), doubleOf(f2, "F2"), doubleOf(f3, "F3"));
    }
    /** Оновлює всі таблиці; якщо сервер недоступний */
    private void refreshAll() {
        safe(() -> {
            suppliers.setItems(FXCollections.observableArrayList(api.getSuppliers()));
            products.setItems(FXCollections.observableArrayList(api.getProducts()));
            stocks.setItems(FXCollections.observableArrayList(api.getStocks()));
            orders.setItems(FXCollections.observableArrayList(api.getOrders()));
        });
    }
    private void refreshSuppliers() {
        safe(() -> suppliers.setItems(FXCollections.observableArrayList(api.getSuppliers())));
    }
    private void refreshProducts() {
        safe(() -> products.setItems(FXCollections.observableArrayList(api.getProducts())));
    }
    private void refreshStocks() {
        safe(() -> stocks.setItems(FXCollections.observableArrayList(api.getStocks())));
    }
    private void refreshOrders() {
        safe(() -> orders.setItems(FXCollections.observableArrayList(api.getOrders())));
    }
    private <T> T selected(TableView<T> table) {
        T v = table.getSelectionModel().getSelectedItem();
        if (v == null) {
            throw new IllegalArgumentException("Виберіть запис у таблиці");
        }
        return v;
    }
    private long longOf(TextField field, String name) {
        try {
            return Long.parseLong(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + name + "» має бути цілим числом");
        }
    }
    private int intOf(TextField field, String name) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + name + "» має бути цілим числом");
        }
    }
    private double doubleOf(TextField field, String name) {
        try {
            return Double.parseDouble(field.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + name + "» має бути числом");
        }
    }
    /** Усі дії користувача виконуються тут: помилка показується діалогом, а не в консолі. */
    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }
    private void showError(Throwable e) {
        Alert d = new Alert(Alert.AlertType.ERROR, e == null ? "Помилка" : e.getMessage(), ButtonType.OK);
        d.setHeaderText(null);
        d.showAndWait();
    }
    private Pane layout(TableView<?> table, Pane form) {
        SplitPane p = new SplitPane(table, form);
        p.setDividerPositions(0.68);
        BorderPane root = new BorderPane(p);
        root.setPadding(new Insets(10));
        return root;
    }
    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
