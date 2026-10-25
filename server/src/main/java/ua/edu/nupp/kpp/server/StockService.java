package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class StockService {
    private static final Logger LOG = Logger.getLogger(StockService.class.getName());
    private final StockDAO dao = new StockDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public List<Stock> getAll() throws SQLException {
        return dao.getAll();
    }

    public Stock getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Stock s) throws SQLException {
        validate(s);
        requireProduct(s.getProductId());
        dao.add(s);
        LOG.info("Додано запас");
    }

    public void update(Stock s) throws SQLException {
        validate(s);
        if (s.getId() == null || dao.getById(s.getId()) == null) {
            throw new IllegalArgumentException("Запас не знайдено");
        }
        requireProduct(s.getProductId());
        dao.update(s);
        LOG.info("Оновлено запас");
    }

    public void delete(long id) throws SQLException {
        if (dao.getById(id) == null) {
            throw new IllegalArgumentException("Запас не знайдено");
        }
        dao.delete(id);
        LOG.info("Видалено запас");
    }

    static void validate(Stock s) {
        if (s == null) {
            throw new IllegalArgumentException("Запас не може бути null");
        }
        if (s.getProductId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID товару");
        }
        if (s.getCurrentQuantity() < 0) {
            throw new IllegalArgumentException("Поточна кількість не може бути від'ємною");
        }
        if (s.getMinThreshold() <= 0) {
            throw new IllegalArgumentException("Мінімальний поріг має бути більшим за 0");
        }
        if (s.getF2() < 0 || s.getF2() > 100 || s.getF3() < 0 || s.getF3() > 100) {
            throw new IllegalArgumentException("F2 і F3 мають бути в діапазоні 0..100");
        }
    }

    private void requireProduct(long productId) throws SQLException {
        if (productDAO.getById(productId) == null) {
            throw new IllegalArgumentException("Товар не знайдено");
        }
    }
}
