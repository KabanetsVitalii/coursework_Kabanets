package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ProductService {
    private static final Logger LOG = Logger.getLogger(ProductService.class.getName());
    private final ProductDAO dao = new ProductDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public List<Product> getAll() throws SQLException {
        return dao.getAll();
    }

    public Product getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Product p) throws SQLException {
        validate(p);
        requireSupplier(p.getSupplierId());
        dao.add(p);
        LOG.info("Додано товар");
    }

    public void update(Product p) throws SQLException {
        validate(p);
        if (p.getId() == null || dao.getById(p.getId()) == null) {
            throw new IllegalArgumentException("Товар не знайдено");
        }
        requireSupplier(p.getSupplierId());
        dao.update(p);
        LOG.info("Оновлено товар");
    }

    public void delete(long id) throws SQLException {
        if (dao.getById(id) == null) {
            throw new IllegalArgumentException("Товар не знайдено");
        }
        dao.delete(id);
        LOG.info("Видалено товар");
    }

    static void validate(Product p) {
        if (p == null) {
            throw new IllegalArgumentException("Товар не може бути null");
        }
        if (p.getSupplierId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID постачальника");
        }
        if (p.getName() == null || p.getName().isBlank()) {
            throw new IllegalArgumentException("Назва товару є обов'язковою");
        }
        if (p.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("Ціна товару має бути більшою за 0");
        }
    }

    private void requireSupplier(long supplierId) throws SQLException {
        if (supplierDAO.getById(supplierId) == null) {
            throw new IllegalArgumentException("Постачальника не знайдено");
        }
    }
}
