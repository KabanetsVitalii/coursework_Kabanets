package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class StockDAO {
    public void add(Stock s) throws SQLException {
        String sql = "INSERT INTO stock(product_id,current_quantity,min_threshold,f2,f3) VALUES(?,?,?,?,?)";
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
    public List<Stock> getAll() throws SQLException {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT id,product_id,current_quantity,min_threshold,f2,f3 FROM stock ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public Stock getById(long id) throws SQLException {
        String sql = "SELECT id,product_id,current_quantity,min_threshold,f2,f3 FROM stock WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(Stock s) throws SQLException {
        String sql = "UPDATE stock SET product_id=?,current_quantity=?,min_threshold=?,f2=?,f3=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, s);
            p.setLong(6, s.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM stock WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, Stock s) throws SQLException {
        p.setLong(1, s.getProductId());
        p.setDouble(2, s.getCurrentQuantity());
        p.setDouble(3, s.getMinThreshold());
        p.setDouble(4, s.getF2());
        p.setDouble(5, s.getF3());
    }

    private Stock map(ResultSet r) throws SQLException {
        return new Stock(r.getLong("id"), r.getLong("product_id"), r.getDouble("current_quantity"),
                r.getDouble("min_threshold"), r.getDouble("f2"), r.getDouble("f3"));
    }
}
