package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class ReplenishmentOrderService {
    private static final Logger LOG = Logger.getLogger(ReplenishmentOrderService.class.getName());
    private final ReplenishmentOrderDAO dao = new ReplenishmentOrderDAO();
    private final StockDAO stockDAO = new StockDAO();

    public List<ReplenishmentOrder> getAll() throws SQLException {
        return dao.getAll();
    }

    public ReplenishmentOrder getById(long id) throws SQLException {
        return dao.getById(id);
    }

    /** Створює замовлення для запасу: F1, F2, F3 та riskIndex розраховує сервер, стан — CREATED. */
    public void add(long stockId) throws SQLException {
        dao.add(fromStock(null, stockId, OrderState.CREATED, LocalDate.now().toString()));
        LOG.info("Створено замовлення поповнення");
    }
    /** Змінює запас замовлення; фактори й riskIndex перераховуються, стан не змінюється. */
    public void update(long id, long stockId) throws SQLException {
        ReplenishmentOrder old = dao.getById(id);
        if (old == null) {
            throw new IllegalArgumentException("Замовлення поповнення не знайдено");
        }
        dao.update(fromStock(id, stockId, old.getState(), old.getOrderDate()));
        LOG.info("Оновлено замовлення поповнення");
    }
    /** Зміна стану життєвого циклу з перевіркою допустимості переходу. */
    public void changeState(long id, OrderState next) throws SQLException {
        ReplenishmentOrder current = dao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Замовлення поповнення не знайдено");
        }
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану: " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану");
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан: " + current.getState() + " -> " + next);
    }
    public void delete(long id) throws SQLException {
        if (dao.getById(id) == null) {
            throw new IllegalArgumentException("Замовлення поповнення не знайдено");
        }
        dao.delete(id);
        LOG.info("Видалено замовлення поповнення");
    }
    public static boolean isAllowedTransition(OrderState from, OrderState to) {
        return (from == OrderState.CREATED && to == OrderState.APPROVED)
                || (from == OrderState.APPROVED && to == OrderState.ORDERED)
                || (from == OrderState.ORDERED && to == OrderState.RECEIVED);
    }
    private ReplenishmentOrder fromStock(Long id, long stockId, OrderState state, String orderDate)
            throws SQLException {
        Stock stock = stockDAO.getById(stockId);
        if (stock == null) {
            throw new IllegalArgumentException("Запас не знайдено");
        }
        double f1 = RiskCalculator.factor1(stock.getCurrentQuantity(), stock.getMinThreshold());
        double f2 = stock.getF2();
        double f3 = stock.getF3();
        ReplenishmentOrder order = new ReplenishmentOrder(id, stockId, state, f1, f2, f3,
                RiskCalculator.calculate(f1, f2, f3), orderDate);
        return order;
    }
}
