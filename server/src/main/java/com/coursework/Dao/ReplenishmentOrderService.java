package com.coursework.Dao;

import com.coursework.model.ReplenishmentOrder;
import com.coursework.model.Stock;
import com.coursework.model.Product;
import com.coursework.model.Supplier;
import com.coursework.model.OrderState;
import com.coursework.server.RiskCalculator;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class ReplenishmentOrderService {
    private static final Logger LOG = Logger.getLogger(ReplenishmentOrderService.class.getName());
    private final ReplenishmentOrderDao dao = new ReplenishmentOrderDao();
    private final StockDao stockDao = new StockDao();
    private final ProductDao productDao = new ProductDao();
    private final SupplierDao supplierDao = new SupplierDao();

    private void validate(ReplenishmentOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Замовлення не може бути null");
        }
        if (order.getStockId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID складу/запасу");
        }
        if (order.getState() == null) {
            throw new IllegalArgumentException("Стан замовлення є обов'язковим");
        }
        if (order.getOrderDate() == null || order.getOrderDate().isBlank()) {
            throw new IllegalArgumentException("Дата замовлення є обов'язковою");
        }
    }
    private Stock requireStock(long stockId) throws SQLException {
        Stock stock = stockDao.findById(stockId);
        if (stock == null) {
            throw new IllegalArgumentException("Запас/склад з ID " + stockId + " не знайдено");
        }
        return stock;
    }
    // Розрахунок та створення на основі RiskCalculator
    public ReplenishmentOrder fromStock(long stockId) throws SQLException {
        Stock stock = requireStock(stockId);
        Product product = productDao.findById(stock.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Товар для заданого запасу не знайдено");
        }
        Supplier supplier = supplierDao.findById(product.getSupplierId());
        double f1 = RiskCalculator.factor1(stock.getCurrentQuantity(), stock.getMinThreshold());
        double deliveryDays = (supplier != null) ? supplier.getDeliveryDays() : 1.0;
        double f2 = Math.min(100.0, (deliveryDays / 30.0) * 100.0);
        double unitPrice = product.getUnitPrice();
        double f3 = Math.min(100.0, (unitPrice / 10000.0) * 100.0);
        double riskIndex = RiskCalculator.calculate(f1, f2, f3);

        ReplenishmentOrder order = new ReplenishmentOrder();
        order.setStockId(stockId);
        order.setOrderDate(LocalDate.now().toString());
        order.setState(OrderState.CREATED); // Початковий стан замовлення
        order.setF1(f1);
        order.setF2(f2);
        order.setF3(f3);
        order.setRiskIndex(riskIndex);
        return order;
    }
    public static boolean isAllowedTransition(OrderState from, OrderState to) {
        return (from == OrderState.CREATED && to == OrderState.APPROVED)
                || (from == OrderState.APPROVED && to == OrderState.ORDERED)
                || (from == OrderState.ORDERED && to == OrderState.RECEIVED);
    }
    public void changeState(long id, OrderState next) throws SQLException {
        ReplenishmentOrder current = dao.findById(id);
        if (current == null) {
            throw new IllegalArgumentException("Замовлення на поповнення не знайдено");
        }
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану: " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану");
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан замовлення: " + current.getState() + " -> " + next);
    }
    public List<ReplenishmentOrder> getAll() throws SQLException {
        return dao.findAll();
    }
    public ReplenishmentOrder getById(long id) throws SQLException {
        return dao.findById(id);
    }
    public void add(long stockId) throws SQLException {
        ReplenishmentOrder order = fromStock(stockId);
        validate(order);
        dao.save(order);
        LOG.info("Створено замовлення на поповнення");
    }
    public void update(long id, long newStockId) throws SQLException {
        ReplenishmentOrder existing = dao.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Замовлення з ID " + id + " не знайдено");
        }
        ReplenishmentOrder updated = fromStock(newStockId);
        updated.setId(id);
        updated.setState(existing.getState());
        validate(updated);
        dao.update(updated);
        LOG.info("Оновлено замовлення на поповнення");
    }
    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено замовлення на поповнення");
    }
}