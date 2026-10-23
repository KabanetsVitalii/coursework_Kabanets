package com.coursework.Dao;
import com.coursework.model.Product;
import com.coursework.model.Supplier;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDao dao = new ProductDao();
    private final SupplierDao supplierDao = new SupplierDao();
    private void validate(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Товар не може бути null");
        }
        if (product.getSupplierId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID постачальника");
        }
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Назва товару є обов'язковою");
        }
        if (product.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("Ціна товару має бути більшою за 0");
        }
    }
    private void requireSupplier(long supplierId) throws SQLException {
        Supplier supplier = supplierDao.findById(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Постачальника з ID " + supplierId + " не знайдено");
        }
    }
    public List<Product> getAll() throws SQLException {
        return dao.findAll();
    }
    public Product getById(long id) throws SQLException {
        return dao.findById(id);
    }

    public void add(Product product) throws SQLException {
        validate(product);
        requireSupplier(product.getSupplierId());
        dao.save(product);
    }
    public void update(Product product) throws SQLException {
        validate(product);
        requireSupplier(product.getSupplierId());
        dao.update(product);
    }
    public void delete(long id) throws SQLException {
        dao.delete(id);
    }
}