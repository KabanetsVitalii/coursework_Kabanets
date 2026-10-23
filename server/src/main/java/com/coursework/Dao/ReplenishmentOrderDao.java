package com.coursework.Dao;

import com.coursework.model.OrderState;
import com.coursework.model.ReplenishmentOrder;
import com.coursework.server.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ReplenishmentOrderDao {

    // CREATE
    public void save(ReplenishmentOrder order) {
        String sql = "INSERT INTO replenishment_order (stock_id, state, f1, f2, f3, risk_index, order_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, order.getStockId());
            pstmt.setString(2, order.getState().name());
            pstmt.setDouble(3, order.getF1());
            pstmt.setDouble(4, order.getF2());
            pstmt.setDouble(5, order.getF3());
            pstmt.setDouble(6, order.getRiskIndex());
            pstmt.setString(7, order.getOrderDate());
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //  READ ALL
    public List<ReplenishmentOrder> findAll() {
        List<ReplenishmentOrder> list = new ArrayList<>();
        String sql = "SELECT * FROM replenishment_order";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ReplenishmentOrder(
                        rs.getLong("id"),
                        rs.getLong("stock_id"),
                        OrderState.valueOf(rs.getString("state")),
                        rs.getDouble("f1"),
                        rs.getDouble("f2"),
                        rs.getDouble("f3"),
                        rs.getDouble("risk_index"),
                        rs.getString("order_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // READ BY ID
    public ReplenishmentOrder findById(Long id) {
        String sql = "SELECT * FROM replenishment_order WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ReplenishmentOrder(
                            rs.getLong("id"),
                            rs.getLong("stock_id"),
                            OrderState.valueOf(rs.getString("state")),
                            rs.getDouble("f1"),
                            rs.getDouble("f2"),
                            rs.getDouble("f3"),
                            rs.getDouble("risk_index"),
                            rs.getString("order_date")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // UPDATE
    public void update(ReplenishmentOrder order) {
        String sql = "UPDATE replenishment_order SET stock_id = ?, state = ?, f1 = ?, f2 = ?, f3 = ?, risk_index = ?, order_date = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, order.getStockId());
            pstmt.setString(2, order.getState().name());
            pstmt.setDouble(3, order.getF1());
            pstmt.setDouble(4, order.getF2());
            pstmt.setDouble(5, order.getF3());
            pstmt.setDouble(6, order.getRiskIndex());
            pstmt.setString(7, order.getOrderDate());
            pstmt.setLong(8, order.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // DELETE
    public void delete(Long id) {
        String sql = "DELETE FROM replenishment_order WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // UPDATE STATE
    public void updateState(long id, OrderState state) {
        String sql = "UPDATE replenishment_order SET state = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, state.name());
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}