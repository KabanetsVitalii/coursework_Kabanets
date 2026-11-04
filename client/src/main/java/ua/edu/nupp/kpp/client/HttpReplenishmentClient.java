package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
public class HttpReplenishmentClient {
    private static final String BASE = "http://localhost:8080/warehouse-server";
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // Постачальники: /suppliers
    public List<Supplier> getSuppliers() {
        return Arrays.asList(read(get("/suppliers"), Supplier[].class));
    }

    public void addSupplier(Supplier s) {
        post("/suppliers", "action=add&" + supplierForm(s));
    }

    public void updateSupplier(Supplier s) {
        post("/suppliers", "action=update&id=" + s.getId() + "&" + supplierForm(s));
    }
    public void deleteSupplier(long id) {
        post("/suppliers", "action=delete&id=" + id);
    }

    //Товари: /products
    public List<Product> getProducts() {
        return Arrays.asList(read(get("/products"), Product[].class));
    }

    public void addProduct(Product p) {
        post("/products", "action=add&" + productForm(p));
    }

    public void updateProduct(Product p) {
        post("/products", "action=update&id=" + p.getId() + "&" + productForm(p));
    }

    public void deleteProduct(long id) {
        post("/products", "action=delete&id=" + id);
    }

    //  Запаси: /stocks
    public List<Stock> getStocks() {
        return Arrays.asList(read(get("/stocks"), Stock[].class));
    }

    public void addStock(Stock s) {
        post("/stocks", "action=add&" + stockForm(s));
    }

    public void updateStock(Stock s) {
        post("/stocks", "action=update&id=" + s.getId() + "&" + stockForm(s));
    }

    public void deleteStock(long id) {
        post("/stocks", "action=delete&id=" + id);
    }

    //  Замовлення поповнення: /orders
    public List<ReplenishmentOrder> getOrders() {
        return Arrays.asList(read(get("/orders"), ReplenishmentOrder[].class));
    }
    public void addOrder(long stockId) {
        post("/orders", "action=add&stockId=" + stockId);
    }

    public void updateOrder(long id, long stockId) {
        post("/orders", "action=update&id=" + id + "&stockId=" + stockId);
    }
    public void changeOrderState(long id, OrderState state) {
        post("/orders", "action=transition&id=" + id + "&state=" + state.name());
    }

    public void deleteOrder(long id) {
        post("/orders", "action=delete&id=" + id);
    }

    // формування параметрів
    private String supplierForm(Supplier s) {
        return "name=" + enc(s.getName()) + "&contactInfo=" + enc(s.getContactInfo())
                + "&deliveryDays=" + s.getDeliveryDays();
    }

    private String productForm(Product p) {
        return "supplierId=" + p.getSupplierId() + "&name=" + enc(p.getName()) + "&unitPrice=" + p.getUnitPrice();
    }

    private String stockForm(Stock s) {
        return "productId=" + s.getProductId() + "&currentQuantity=" + s.getCurrentQuantity()
                + "&minThreshold=" + s.getMinThreshold() + "&f2=" + s.getF2() + "&f3=" + s.getF3();
    }

    // HTTP
    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(10)).GET().build();
        return send(req);
    }

    private String post(String path, String form) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        return send(req);
    }

    private String send(HttpRequest req) {
        HttpResponse<String> r;
        try {
            r = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Сервер недоступний: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Запит перервано", e);
        }
        if (r.statusCode() >= 400) {
            throw new RuntimeException(errorText(r.statusCode(), r.body(), req.uri().toString()));
        }
        return r.body();
    }
    private String errorText(int status, String body, String uri) {
        try {
            String message = mapper.readTree(body).path("error").asText("");
            if (!message.isBlank()) {
                return message;
            }
        } catch (IOException | RuntimeException ignored) {
            // тіло не є JSON: показуємо код відповіді
        }
        if (status == 404) {
            return "Помилка HTTP 404: за адресою " + uri + " застосунок не знайдено. "
                    + "Перевірте, що Tomcat 9 запущено і server/target/warehouse-server.war розгорнуто.";
        }
        return "Помилка HTTP " + status;
    }
    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Помилка JSON", e);
        }
    }
    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}