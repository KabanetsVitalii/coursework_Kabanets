package com.coursework.Dao;
import com.coursework.model.Stock;
import com.coursework.model.Product;
import java.sql.SQLException;
import java.util.List;

public class StockService {
    private final StockDao dao = new StockDao();
    private final ProductDao productDao = new ProductDao();

    private void validate(Stock stock) {
        if (stock == null) {
            throw new IllegalArgumentException("Запас не може бути null");
        }
        if (stock.getProductId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID товару");
        }
        if (stock.getCurrentQuantity() < 0) {
            throw new IllegalArgumentException("Поточна кількість не може бути від'ємною");
        }
        if (stock.getMinThreshold() < 0) {
            throw new IllegalArgumentException("Мінімальний поріг не може бути від'ємним");
        }
        if (stock.getF2() < 0 || stock.getF2() > 100 ||
                stock.getF3() < 0 || stock.getF3() > 100) {
            throw new IllegalArgumentException("Фактори F2 і F3 мають бути в діапазоні 0..100");
        }
    }
    private void requireProduct(long productId) throws SQLException {
        Product product = productDao.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Товар з ID " + productId + " не знайдено");
        }
    }
    public List<Stock> getAll() throws SQLException {
        return dao.findAll();
    }
    public Stock getById(long id) throws SQLException {
        return dao.findById(id);
    }
    public void add(Stock stock) throws SQLException {
        validate(stock);
        requireProduct(stock.getProductId());
        dao.save(stock);
    }
    public void update(Stock stock) throws SQLException {
        validate(stock);
        requireProduct(stock.getProductId());
        dao.update(stock);
    }
    public void delete(long id) throws SQLException {
        dao.delete(id);
    }
}