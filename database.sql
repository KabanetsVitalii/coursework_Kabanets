DROP DATABASE IF EXISTS coursework_db;
CREATE DATABASE coursework_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE coursework_db;
-- Постачальники
CREATE TABLE supplier (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(150) NOT NULL,
                          contact_info VARCHAR(255) NOT NULL,
                          delivery_days INT NOT NULL
);
-- Товари
CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         supplier_id BIGINT NOT NULL,
                         name VARCHAR(150) NOT NULL,
                         unit_price DOUBLE NOT NULL,
                         FOREIGN KEY (supplier_id) REFERENCES supplier(id) ON DELETE CASCADE
);
--  Запаси на складі
CREATE TABLE stock (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       product_id BIGINT NOT NULL,
                       current_quantity DOUBLE NOT NULL,
                       min_threshold DOUBLE NOT NULL,
                       f2 DOUBLE NOT NULL,
                       f3 DOUBLE NOT NULL,
                       FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);
-- Замовлення на поповнення
CREATE TABLE replenishment_order (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     stock_id BIGINT NOT NULL,
                                     state VARCHAR(50) NOT NULL,
                                     f1 DOUBLE NOT NULL,
                                     f2 DOUBLE NOT NULL,
                                     f3 DOUBLE NOT NULL,
                                     risk_index DOUBLE NOT NULL,
                                     order_date VARCHAR(50) NOT NULL,
                                     FOREIGN KEY (stock_id) REFERENCES stock(id) ON DELETE CASCADE
);
-- Демонстраційні дані
INSERT INTO supplier (name, contact_info, delivery_days) VALUES
                                                             ('ТОВ "ПостачТрейд"', 'contact@postachtrade.ua', 5),
                                                             ('ПП "ТехноСвіт"', 'info@technosvit.ua', 3);

INSERT INTO product (supplier_id, name, unit_price) VALUES
                                                        (1, 'Лазерний принтер', 4500.00),
                                                        (2, 'Офісний папір А4', 180.50);
INSERT INTO stock (product_id, current_quantity, min_threshold, f2, f3) VALUES
                                                                            (1, 15.0, 60.0, 40.0, 80.0),
                                                                            (1, 45.0, 50.0, 50.0, 40.0),
                                                                            (1, 10.0, 40.0, 45.0, 65.0),
                                                                            (1, 70.0, 50.0, 45.0, 35.0);

-- Початковий приклад створеного замовлення для Запасу №1 (I = 64.0)
INSERT INTO replenishment_order (stock_id, state, f1, f2, f3, risk_index, order_date) VALUES
    (1, 'CREATED', 75.0, 40.0, 80.0, 64.00, '2026-10-24');