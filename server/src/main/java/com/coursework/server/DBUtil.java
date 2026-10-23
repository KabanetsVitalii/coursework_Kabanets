package com.coursework.server;

import java.sql.Connection;
import java.sql.SQLException;

public class DBUtil {

    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Успішне підключення до бази даних coursework_db");
            }
        } catch (SQLException e) {
            System.out.println("Помилка підключення");
            e.printStackTrace();
        }
    }
}