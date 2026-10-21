DROP DATABASE IF EXISTS coursework_db;
CREATE DATABASE coursework_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE coursework_db;
CREATE TABLE suppliers (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(150) NOT NULL,
                           contact_info VARCHAR(255)
);
CREATE TABLE products (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(150) NOT NULL,
                          sku VARCHAR(50) UNIQUE NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          supplier_id INT,
                          FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
);
CREATE TABLE stock (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       product_id INT NOT NULL,
                       quantity INT NOT NULL,
                       min_threshold INT NOT NULL,
                       FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
CREATE TABLE replenishment_orders (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      product_id INT NOT NULL,
                                      quantity INT NOT NULL,
                                      state VARCHAR(50) NOT NULL DEFAULT 'CREATED',
                                      order_date DATE NOT NULL,
                                      FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
-- Демонстраційні дані
INSERT INTO suppliers (name, contact_info) VALUES
                                               ('ТОВ "ПостачТрейд"', 'contact@postachtrade.ua'),
                                               ('ПП "ТехноСвіт"', 'info@technosvit.ua');
INSERT INTO products (name, sku, price, supplier_id) VALUES
                                                         ('Лазерний принтер', 'PRN-001', 4500.00, 1),
                                                         ('Офісний папір А4', 'PAP-002', 180.50, 2);

INSERT INTO stock (product_id, quantity, min_threshold) VALUES
                                                            (1, 12, 5),
                                                            (2, 45, 20);
INSERT INTO replenishment_orders (product_id, quantity, state, order_date) VALUES
    (1, 10, 'CREATED', '2026-06-01');