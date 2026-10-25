package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplenishmentOrderDAO {
    public void add(ReplenishmentOrder o) throws SQLException {
        String sql = "INSERT INTO replenishment_order(stock_id,state,f1,f2,f3,risk_index,order_date) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(p, o);
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) {
                    o.setId(keys.getLong(1));
                }
            }
        }
    }
    public List<ReplenishmentOrder> getAll() throws SQLException {
        List<ReplenishmentOrder> list = new ArrayList<>();
        String sql = "SELECT id,stock_id,state,f1,f2,f3,risk_index,order_date FROM replenishment_order ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }
    public ReplenishmentOrder getById(long id) throws SQLException {
        String sql = "SELECT id,stock_id,state,f1,f2,f3,risk_index,order_date FROM replenishment_order WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }
    public void update(ReplenishmentOrder o) throws SQLException {
        String sql = "UPDATE replenishment_order SET stock_id=?,state=?,f1=?,f2=?,f3=?,risk_index=?,order_date=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, o);
            p.setLong(8, o.getId());
            p.executeUpdate();
        }
    }
    public void updateState(long id, OrderState state) throws SQLException {
        String sql = "UPDATE replenishment_order SET state=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, state.name());
            p.setLong(2, id);
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM replenishment_order WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, ReplenishmentOrder o) throws SQLException {
        p.setLong(1, o.getStockId());
        p.setString(2, o.getState().name());
        p.setDouble(3, o.getF1());
        p.setDouble(4, o.getF2());
        p.setDouble(5, o.getF3());
        p.setDouble(6, o.getRiskIndex());
        p.setString(7, o.getOrderDate());
    }

    private ReplenishmentOrder map(ResultSet r) throws SQLException {
        return new ReplenishmentOrder(r.getLong("id"), r.getLong("stock_id"),
                OrderState.valueOf(r.getString("state")), r.getDouble("f1"), r.getDouble("f2"),
                r.getDouble("f3"), r.getDouble("risk_index"), r.getString("order_date"));
    }
}
