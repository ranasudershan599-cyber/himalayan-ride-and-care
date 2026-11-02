-- Add-on tables for the ADBMS practicals, written for MySQL 8.
-- Run schema.sql first, then run this file in MySQL Workbench.
USE himalayan_ride_care;

CREATE TABLE IF NOT EXISTS service_parts (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  part_name VARCHAR(100) NOT NULL UNIQUE,
  quantity_in_stock INT NOT NULL DEFAULT 0,
  reorder_level INT NOT NULL DEFAULT 5,
  unit_price DECIMAL(10,2) NOT NULL,
  CONSTRAINT chk_part_stock CHECK (quantity_in_stock >= 0),
  CONSTRAINT chk_part_reorder CHECK (reorder_level >= 0),
  CONSTRAINT chk_part_price CHECK (unit_price >= 0)
);

CREATE TABLE IF NOT EXISTS part_orders (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  part_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  order_total DECIMAL(10,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_part_order_part FOREIGN KEY (part_id) REFERENCES service_parts(id),
  CONSTRAINT chk_part_order_quantity CHECK (quantity > 0)
);

CREATE TABLE IF NOT EXISTS wallet_accounts (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  customer_name VARCHAR(100) NOT NULL,
  balance DECIMAL(12,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_wallet_balance CHECK (balance >= 0)
);

CREATE TABLE IF NOT EXISTS wallet_transfers (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  from_account_id BIGINT NOT NULL,
  to_account_id BIGINT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  transferred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_transfer_from FOREIGN KEY (from_account_id) REFERENCES wallet_accounts(id),
  CONSTRAINT fk_transfer_to FOREIGN KEY (to_account_id) REFERENCES wallet_accounts(id),
  CONSTRAINT chk_transfer_amount CHECK (amount > 0)
);

CREATE TABLE IF NOT EXISTS wallet_balance_audit (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id BIGINT NOT NULL,
  old_balance DECIMAL(12,2) NOT NULL,
  new_balance DECIMAL(12,2) NOT NULL,
  changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_audit_account FOREIGN KEY (account_id) REFERENCES wallet_accounts(id)
);

INSERT INTO service_parts (part_name, quantity_in_stock, reorder_level, unit_price) VALUES
('Brake pads', 3, 5, 850.00),
('Engine oil 1L', 18, 6, 620.00),
('Chain kit', 2, 4, 2400.00),
('Clutch cable', 9, 3, 450.00)
ON DUPLICATE KEY UPDATE part_name = VALUES(part_name);

INSERT INTO wallet_accounts (customer_name, balance)
SELECT 'Asha Sharma', 5000.00 WHERE NOT EXISTS (SELECT 1 FROM wallet_accounts WHERE customer_name = 'Asha Sharma');
INSERT INTO wallet_accounts (customer_name, balance)
SELECT 'Rohan Mehta', 2500.00 WHERE NOT EXISTS (SELECT 1 FROM wallet_accounts WHERE customer_name = 'Rohan Mehta');
