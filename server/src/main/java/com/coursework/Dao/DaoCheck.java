package com.coursework.Dao;

import com.coursework.model.Supplier;
import com.coursework.model.Product;
import com.coursework.model.Stock;
import com.coursework.model.ReplenishmentOrder;
import com.coursework.model.OrderState;

import java.util.List;

public class DaoCheck {
    public static void main(String[] args) {
        SupplierDao supplierDao = new SupplierDao();
        ProductDao productDao = new ProductDao();
        StockDao stockDao = new StockDao();
        ReplenishmentOrderDao orderDao = new ReplenishmentOrderDao();
        System.out.println("ПОЧАТОК ПЕРЕВІРКИ DAO");
        try {
            // Перевірка Supplier
            Supplier supplier = new Supplier(null, "Тестовий Постачальник", "test@supplier.com", 5);
            supplierDao.save(supplier);
            System.out.println("Supplier ADD — OK (ID: " + supplier.getId() + ")");

            Supplier foundSupplier = supplierDao.findById(supplier.getId());
            System.out.println("Supplier FIND BY ID — OK (" + foundSupplier.getName() + ")");

            foundSupplier.setName("Тестовий Постачальник (Оновлено)");
            supplierDao.update(foundSupplier);
            System.out.println("Supplier UPDATE — OK");

            //  Перевірка Product
            Product product = new Product(null, supplier.getId(), "Тестовий Товар", 150.0);
            productDao.save(product);
            System.out.println("Product ADD — OK (ID: " + product.getId() + ")");

            Product foundProduct = productDao.findById(product.getId());
            System.out.println("Product FIND BY ID — OK (" + foundProduct.getName() + ")");

            foundProduct.setUnitPrice(175.0);
            productDao.update(foundProduct);
            System.out.println("Product UPDATE — OK");

            //  Перевірка Stock
            Stock stock = new Stock(null, product.getId(), 50.0, 10.0, 0.8, 0.9);
            stockDao.save(stock);
            System.out.println("Stock ADD — OK (ID: " + stock.getId() + ")");

            Stock foundStock = stockDao.findById(stock.getId());
            System.out.println("Stock FIND BY ID — OK (Quantity: " + foundStock.getCurrentQuantity() + ")");

            foundStock.setCurrentQuantity(60.0);
            stockDao.update(foundStock);
            System.out.println("Stock UPDATE — OK");

            // Перевірка ReplenishmentOrder
            ReplenishmentOrder order = new ReplenishmentOrder(
                    null, stock.getId(), OrderState.CREATED, 0.7, 0.8, 0.9, 0.75, "2026-06-01"
            );
            orderDao.save(order);
            System.out.println("ReplenishmentOrder ADD — OK (ID: " + order.getId() + ")");

            ReplenishmentOrder foundOrder = orderDao.findById(order.getId());
            System.out.println("ReplenishmentOrder FIND BY ID — OK (State: " + foundOrder.getState() + ")");

            foundOrder.setState(OrderState.APPROVED);
            orderDao.update(foundOrder);
            System.out.println("ReplenishmentOrder UPDATE — OK");
            orderDao.delete(order.getId());
            System.out.println("ReplenishmentOrder DELETE — OK");

            stockDao.delete(stock.getId());
            System.out.println("Stock DELETE — OK");

            productDao.delete(product.getId());
            System.out.println("Product DELETE — OK");

            supplierDao.delete(supplier.getId());
            System.out.println("Supplier DELETE — OK");
            System.out.println("CRUD-ПЕРЕВІРКА ЗАВЕРШЕНА УСПІШНО");
        } catch (Exception e) {
            System.err.println("ПОМИЛКА ПІД ЧАС ПЕРЕВІРКИ DAO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}