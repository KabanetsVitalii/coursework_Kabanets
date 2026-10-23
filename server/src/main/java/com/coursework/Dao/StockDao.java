package com.coursework.Dao;

import com.coursework.model.Stock;
import com.coursework.server.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class StockDao {
    // CREATE
    public void save(Stock stock) {
        String sql = "INSERT INTO stock (product_id, current_quantity, min_threshold, f2, f3) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, stock.getProductId());
            pstmt.setDouble(2, stock.getCurrentQuantity());
            pstmt.setDouble(3, stock.getMinThreshold());
            pstmt.setDouble(4, stock.getF2());
            pstmt.setDouble(5, stock.getF3());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    stock.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //  READ ALL
    public List<Stock> findAll() {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT * FROM stock";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Stock(
                        rs.getLong("id"),
                        rs.getLong("product_id"),
                        rs.getDouble("current_quantity"),
                        rs.getDouble("min_threshold"),
                        rs.getDouble("f2"),
                        rs.getDouble("f3")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // READ BY ID
    public Stock findById(Long id) {
        String sql = "SELECT * FROM stock WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Stock(
                            rs.getLong("id"),
                            rs.getLong("product_id"),
                            rs.getDouble("current_quantity"),
                            rs.getDouble("min_threshold"),
                            rs.getDouble("f2"),
                            rs.getDouble("f3")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    //  UPDATE
    public void update(Stock stock) {
        String sql = "UPDATE stock SET product_id = ?, current_quantity = ?, min_threshold = ?, f2 = ?, f3 = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, stock.getProductId());
            pstmt.setDouble(2, stock.getCurrentQuantity());
            pstmt.setDouble(3, stock.getMinThreshold());
            pstmt.setDouble(4, stock.getF2());
            pstmt.setDouble(5, stock.getF3());
            pstmt.setLong(6, stock.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // DELETE
    public void delete(Long id) {
        String sql = "DELETE FROM stock WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}