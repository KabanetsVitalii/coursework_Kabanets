package com.coursework.server;

import com.coursework.Dao.ReplenishmentOrderDao;
import com.coursework.model.OrderState;
import com.coursework.model.ReplenishmentOrder;
import com.coursework.Dao.ReplenishmentOrderService;
import java.sql.SQLException;
import java.util.List;

public class ServerDemo {
    public static void main(String[] args) {
        System.out.println("=== ПОЧАТОК ДЕМОНСТРАЦІЇ СЕРВЕРНОЇ ЛОГІКИ ===");
        ReplenishmentOrderService service = new ReplenishmentOrderService();
        try {
            // Демонстрація створення замовлення валідація + RiskCalculator + DAO.save + LOG.info
            long targetStockId = 1L;
            System.out.println("\n1. Створення замовлення для запасу з ID: " + targetStockId );
            service.add(targetStockId);
            // Отримання всіх замовлень з бази даних
            System.out.println("\n2. Читання всіх замовлень з MySQL ");
            List<ReplenishmentOrder> orders = service.getAll();
            for (ReplenishmentOrder ord : orders) {
                System.out.println("ID: " + ord.getId() +
                        ", StockID: " + ord.getStockId() +
                        ", State: " + ord.getState() +
                        ", RiskIndex: " + ord.getRiskIndex());
            }
            if (!orders.isEmpty()) {
                long lastId = orders.get(orders.size() - 1).getId();
                //  Демонстрація правильного життєвого циклу (CREATED -> APPROVED -> ORDERED)
                System.out.println("\n3. Перевірка життєвого циклу (Успішні переходи)");
                System.out.println("Зміна стану: CREATED -> APPROVED");
                service.changeState(lastId, OrderState.APPROVED);
                System.out.println("Зміна стану: APPROVED -> ORDERED");
                service.changeState(lastId, OrderState.ORDERED);
                // Демонстрація забороненого переходу
                System.out.println("\n4. Спроба забороненого переходу ");
                try {
                    System.out.println("Спроба зробити зворотний перехід: ORDERED -> CREATED");
                    service.changeState(lastId, OrderState.CREATED);
                } catch (IllegalArgumentException e) {
                    System.out.println("Очікувана помилка спіймана: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Помилка бази даних: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Помилка валідації/предметної логіки: " + e.getMessage());
        }
        System.out.println("\n Кінець демонстрації");
    }
}