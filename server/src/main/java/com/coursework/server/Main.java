package com.coursework.server;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Успішне підключення до бази данниї coursework_db");
            }
        } catch (SQLException e) {
            System.out.println("Помилка підключення");
            e.printStackTrace();
        }
    }
}