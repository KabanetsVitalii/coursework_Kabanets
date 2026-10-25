package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    public void add(Supplier s) throws SQLException {
        String sql = "INSERT INTO supplier(name,contact_info,delivery_days) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(p, s);
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) {
                    s.setId(keys.getLong(1));
                }
            }
        }
    }

    public List<Supplier> getAll() throws SQLException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT id,name,contact_info,delivery_days FROM supplier ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public Supplier getById(long id) throws SQLException {
        String sql = "SELECT id,name,contact_info,delivery_days FROM supplier WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(Supplier s) throws SQLException {
        String sql = "UPDATE supplier SET name=?,contact_info=?,delivery_days=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, s);
            p.setLong(4, s.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM supplier WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, Supplier s) throws SQLException {
        p.setString(1, s.getName());
        p.setString(2, s.getContactInfo());
        p.setInt(3, s.getDeliveryDays());
    }

    private Supplier map(ResultSet r) throws SQLException {
        return new Supplier(r.getLong("id"), r.getString("name"),
                r.getString("contact_info"), r.getInt("delivery_days"));
    }
}
