package com.coursework.Dao;

import com.coursework.model.Supplier;
import com.coursework.server.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class SupplierDao {
    // CREATE
    public void save(Supplier supplier) {
        String sql = "INSERT INTO supplier (name, contact_info, delivery_days) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getContactInfo());
            pstmt.setInt(3, supplier.getDeliveryDays());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    supplier.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // READ ALL
    public List<Supplier> findAll() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Supplier(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getInt("delivery_days")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // READ BY ID
    public Supplier findById(Long id) {
        String sql = "SELECT * FROM supplier WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("contact_info"),
                            rs.getInt("delivery_days")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // UPDATE
    public void update(Supplier supplier) {
        String sql = "UPDATE supplier SET name = ?, contact_info = ?, delivery_days = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getContactInfo());
            pstmt.setInt(3, supplier.getDeliveryDays());
            pstmt.setLong(4, supplier.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // DELETE
    public void delete(Long id) {
        String sql = "DELETE FROM supplier WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}