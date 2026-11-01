package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {
    private static final String URL =
            "jdbc:mysql://localhost:3306/warehouse_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Драйвер MySQL (mysql-connector-j) не знайдено у WEB-INF/lib", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}