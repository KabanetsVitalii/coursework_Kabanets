package com.coursework.Dao;

import com.coursework.model.OrderState;
import com.coursework.model.ReplenishmentOrder;
import com.coursework.model.Stock;
import com.coursework.server.RiskCalculator;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class ReplenishmentOrderService {
    private static final Logger LOG = Logger.getLogger(ReplenishmentOrderService.class.getName());
    private final ReplenishmentOrderDao orderDao = new ReplenishmentOrderDao();
    private final StockDao stockDao = new StockDao();

    public ReplenishmentOrder fromStock(long stockId) throws SQLException {
        Stock stock = stockDao.findById(stockId);
        if (stock == null) {
            throw new IllegalArgumentException("Запас із ID " + stockId + " не знайдено в базі даних.");
        }
        double f1 = RiskCalculator.factor1(stock.getCurrentQuantity(), stock.getMinThreshold());
        double f2 = stock.getF2();
        double f3 = stock.getF3();
        double riskIndex = RiskCalculator.calculate(f1, f2, f3);
        ReplenishmentOrder order = new ReplenishmentOrder();
        order.setStockId(stockId);
        order.setState(OrderState.CREATED);
        order.setF1(f1);
        order.setF2(f2);
        order.setF3(f3);
        order.setRiskIndex(riskIndex);
        order.setOrderDate(LocalDate.now().toString());

        return order;
    }
    /**
     * Додавання замовлення до бази даних
     */
    public void add(long stockId) throws SQLException {
        ReplenishmentOrder order = fromStock(stockId);
        orderDao.save(order);
        LOG.info("Створено замовлення на поповнення для stock_id=" + stockId + " з індексом ризику: " + order.getRiskIndex());
    }
    /**
     * Отримання всіх замовлень
     */
    public List<ReplenishmentOrder> getAll() throws SQLException {
        return orderDao.findAll();
    }
    /**
     * Керування життєвим циклом та зміна стану з перевіркою коректності переходів
     */
    public void changeState(long orderId, OrderState newState) throws SQLException {
        ReplenishmentOrder order = orderDao.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Замовлення з ID " + orderId + " не знайдено.");
        }
        OrderState currentState = order.getState();

        if (!isValidTransition(currentState, newState)) {
            throw new IllegalArgumentException("Заборонений перехід стану: " + currentState + " -> " + newState);
        }
        order.setState(newState);
        orderDao.update(order);
        LOG.info("Стан замовлення ID " + orderId + " успішно змінено з " + currentState + " на " + newState);
    }
    /**
     * Правила переходів чотиристанного життєвого циклу
     */
    private boolean isValidTransition(OrderState current, OrderState next) {
        return (current == OrderState.CREATED && next == OrderState.APPROVED) ||
                (current == OrderState.APPROVED && next == OrderState.ORDERED) ||
                (current == OrderState.ORDERED && next == OrderState.RECEIVED);
    }
}