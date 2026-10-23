package com.coursework.Dao;

import com.coursework.model.Supplier;
import java.sql.SQLException;
import java.util.List;
public class SupplierService {
    private final SupplierDao dao = new SupplierDao();
    private void validate(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Постачальник не може бути null");
        }
        if (supplier.getName() == null || supplier.getName().isBlank()) {
            throw new IllegalArgumentException("Назва постачальника є обов'язковою");
        }
        if (supplier.getContactInfo() == null || supplier.getContactInfo().isBlank()) {
            throw new IllegalArgumentException("Контактна інформація є обов'язковою");
        }
        if (supplier.getDeliveryDays() < 0) {
            throw new IllegalArgumentException("Термін доставки не може бути від'ємним");
        }
    }
    public List<Supplier> getAll() throws SQLException {
        return dao.findAll();
    }
    public Supplier getById(long id) throws SQLException {
        return dao.findById(id);
    }
    public void add(Supplier supplier) throws SQLException {
        validate(supplier);
        dao.save(supplier);
    }
    public void update(Supplier supplier) throws SQLException {
        validate(supplier);
        dao.update(supplier);
    }
    public void delete(long id) throws SQLException {
        dao.delete(id);
    }
}