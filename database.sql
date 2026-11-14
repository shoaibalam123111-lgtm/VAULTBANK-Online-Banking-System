CREATE DATABASE IF NOT EXISTS vaultbank;

USE vaultbank;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    balance DECIMAL(12,2) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT,
    receiver_id INT,
    amount DECIMAL(12,2) NOT NULL,
    type VARCHAR(50),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);

INSERT INTO users (name, email, password, role, balance)
VALUES
('VAULTBANK Admin', 'admin@gmail.com', 'admin123', 'admin', 0),
('Customer', 'customer@gmail.com', 'customer123', 'customer', 50000)
ON DUPLICATE KEY UPDATE email = email;