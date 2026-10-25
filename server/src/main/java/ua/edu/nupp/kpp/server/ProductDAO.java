package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public void add(Product pr) throws SQLException {
        String sql = "INSERT INTO product(supplier_id,name,unit_price) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(p, pr);
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) {
                    pr.setId(keys.getLong(1));
                }
            }
        }
    }

    public List<Product> getAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id,supplier_id,name,unit_price FROM product ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public Product getById(long id) throws SQLException {
        String sql = "SELECT id,supplier_id,name,unit_price FROM product WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(Product pr) throws SQLException {
        String sql = "UPDATE product SET supplier_id=?,name=?,unit_price=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, pr);
            p.setLong(4, pr.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM product WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, Product pr) throws SQLException {
        p.setLong(1, pr.getSupplierId());
        p.setString(2, pr.getName());
        p.setDouble(3, pr.getUnitPrice());
    }

    private Product map(ResultSet r) throws SQLException {
        return new Product(r.getLong("id"), r.getLong("supplier_id"),
                r.getString("name"), r.getDouble("unit_price"));
    }
}
