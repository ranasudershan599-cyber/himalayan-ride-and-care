-- Case-study tables for the Himalayan Ride & Care rental/support workflow.
-- Run after schema.sql in MySQL Workbench. Hibernate also creates/updates these
-- tables on application startup for an easy classroom setup.
USE himalayan_ride_care;

CREATE TABLE IF NOT EXISTS customers (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL,
 phone VARCHAR(20) NOT NULL, email VARCHAR(120) UNIQUE
);
CREATE TABLE IF NOT EXISTS motorcycles (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, registration_number VARCHAR(20) NOT NULL UNIQUE,
 model VARCHAR(100) NOT NULL, daily_rate DECIMAL(10,2) NOT NULL,
 odometer_km INT NOT NULL DEFAULT 0, status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
 CHECK (daily_rate >= 0), CHECK (odometer_km >= 0),
 CHECK (status IN ('AVAILABLE','MAINTENANCE','INACTIVE'))
);
CREATE TABLE IF NOT EXISTS destinations (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL,
 description VARCHAR(500) NOT NULL
);
CREATE TABLE IF NOT EXISTS rental_bookings (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, customer_id BIGINT NOT NULL,
 motorcycle_id BIGINT NOT NULL, destination_id BIGINT NULL,
 start_date DATE NOT NULL, end_date DATE NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
 pickup_odometer_km INT NULL, return_odometer_km INT NULL, staff_name VARCHAR(100) NULL,
 FOREIGN KEY (customer_id) REFERENCES customers(id),
 FOREIGN KEY (motorcycle_id) REFERENCES motorcycles(id),
 FOREIGN KEY (destination_id) REFERENCES destinations(id),
 CHECK (end_date > start_date), CHECK (pickup_odometer_km IS NULL OR pickup_odometer_km >= 0),
 CHECK (return_odometer_km IS NULL OR return_odometer_km >= 0),
 CHECK (status IN ('PENDING','CONFIRMED','ACTIVE','COMPLETED','CANCELLED')),
 INDEX idx_rental_dates (motorcycle_id,start_date,end_date,status)
);
CREATE TABLE IF NOT EXISTS payment_records (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, booking_id BIGINT NOT NULL,
 amount DECIMAL(10,2) NOT NULL, method VARCHAR(30) NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'RECORDED', paid_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (booking_id) REFERENCES rental_bookings(id), CHECK (amount > 0)
);
CREATE TABLE IF NOT EXISTS assistance_requests (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, customer_id BIGINT NOT NULL,
 booking_id BIGINT NULL, category VARCHAR(50) NOT NULL, priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
 description VARCHAR(1000) NOT NULL, location VARCHAR(200) NOT NULL,
 assigned_to VARCHAR(100) NULL, status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (customer_id) REFERENCES customers(id),
 FOREIGN KEY (booking_id) REFERENCES rental_bookings(id),
 CHECK (priority IN ('LOW','NORMAL','HIGH','URGENT')),
 CHECK (status IN ('OPEN','ASSIGNED','IN_PROGRESS','RESOLVED','CLOSED','CANCELLED')),
 INDEX idx_assistance_status (status,priority)
);

INSERT INTO customers(name,phone,email) SELECT 'Asha Sharma','9876543210','asha@example.com'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email='asha@example.com');
INSERT INTO customers(name,phone,email) SELECT 'Rohan Mehta','9876501234','rohan@example.com'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email='rohan@example.com');
INSERT INTO motorcycles(registration_number,model,daily_rate,odometer_km,status)
VALUES ('HP-01-HR-2026','Royal Enfield Himalayan 450',2200.00,12500,'AVAILABLE'),
       ('HP-02-HR-2026','Royal Enfield Scram 411',1800.00,8900,'AVAILABLE'),
       ('HP-03-HR-2026','Adventure Bike 390',2000.00,15300,'MAINTENANCE')
ON DUPLICATE KEY UPDATE registration_number=VALUES(registration_number);
INSERT INTO destinations(name,description) SELECT 'Manali','Mountain town and common Himalayan ride starting point'
WHERE NOT EXISTS (SELECT 1 FROM destinations WHERE name='Manali');
INSERT INTO destinations(name,description) SELECT 'Spiti Valley','High-altitude cold desert valley route'
WHERE NOT EXISTS (SELECT 1 FROM destinations WHERE name='Spiti Valley');
INSERT INTO destinations(name,description) SELECT 'Leh','Ladakh destination for long-distance motorcycle tours'
WHERE NOT EXISTS (SELECT 1 FROM destinations WHERE name='Leh');
