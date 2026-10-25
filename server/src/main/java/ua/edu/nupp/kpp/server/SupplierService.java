package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class SupplierService {
    private static final Logger LOG = Logger.getLogger(SupplierService.class.getName());
    private final SupplierDAO dao = new SupplierDAO();

    public List<Supplier> getAll() throws SQLException {
        return dao.getAll();
    }

    public Supplier getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Supplier s) throws SQLException {
        validate(s);
        dao.add(s);
        LOG.info("Додано постачальника");
    }

    public void update(Supplier s) throws SQLException {
        validate(s);
        if (s.getId() == null || dao.getById(s.getId()) == null) {
            throw new IllegalArgumentException("Постачальника не знайдено");
        }
        dao.update(s);
        LOG.info("Оновлено постачальника");
    }

    public void delete(long id) throws SQLException {
        if (dao.getById(id) == null) {
            throw new IllegalArgumentException("Постачальника не знайдено");
        }
        dao.delete(id);
        LOG.info("Видалено постачальника");
    }

    static void validate(Supplier s) {
        if (s == null) {
            throw new IllegalArgumentException("Постачальник не може бути null");
        }
        if (blank(s.getName())) {
            throw new IllegalArgumentException("Назва постачальника є обов'язковою");
        }
        if (blank(s.getContactInfo())) {
            throw new IllegalArgumentException("Контактна інформація є обов'язковою");
        }
        if (s.getDeliveryDays() <= 0) {
            throw new IllegalArgumentException("Термін доставки має бути більшим за 0");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
