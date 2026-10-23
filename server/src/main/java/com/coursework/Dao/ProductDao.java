package com.coursework.Dao;

import com.coursework.model.Product;
import com.coursework.server.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    //  CREATE
    public void save(Product product) {
        String sql = "INSERT INTO product (supplier_id, name, unit_price) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, product.getSupplierId());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getUnitPrice());
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    product.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // READ ALL
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getLong("id"),
                        rs.getLong("supplier_id"),
                        rs.getString("name"),
                        rs.getDouble("unit_price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // READ BY ID
    public Product findById(Long id) {
        String sql = "SELECT * FROM product WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getLong("id"),
                            rs.getLong("supplier_id"),
                            rs.getString("name"),
                            rs.getDouble("unit_price")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // UPDATE
    public void update(Product product) {
        String sql = "UPDATE product SET supplier_id = ?, name = ?, unit_price = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, product.getSupplierId());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getUnitPrice());
            pstmt.setLong(4, product.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //  DELETE
    public void delete(Long id) {
        String sql = "DELETE FROM product WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}