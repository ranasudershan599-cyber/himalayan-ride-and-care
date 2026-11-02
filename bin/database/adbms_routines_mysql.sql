-- ADBMS practicals adapted to MySQL 8 stored-program syntax.
-- Oracle PL/SQL syntax is not accepted by MySQL; these routines demonstrate
-- the same syllabus concepts using MySQL procedures, functions, cursors,
-- transactions, SIGNAL, and triggers.
USE himalayan_ride_care;

DROP TRIGGER IF EXISTS trg_wallet_balance_audit;
DROP PROCEDURE IF EXISTS create_wallet_account;
DROP PROCEDURE IF EXISTS report_low_stock;
DROP FUNCTION IF EXISTS calculate_discount;
DROP PROCEDURE IF EXISTS process_part_order;
DROP PROCEDURE IF EXISTS transfer_wallet_funds;

DELIMITER $$

-- Practical 1: validate minimum starting balance before inserting an account.
CREATE PROCEDURE create_wallet_account(
  IN p_customer_name VARCHAR(100),
  IN p_initial_balance DECIMAL(12,2),
  OUT p_new_account_id BIGINT
)
BEGIN
  IF p_initial_balance < 500.00 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Minimum opening balance is 500.00';
  END IF;
  IF p_customer_name IS NULL OR TRIM(p_customer_name) = '' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Customer name is required';
  END IF;
  INSERT INTO wallet_accounts(customer_name, balance) VALUES (TRIM(p_customer_name), p_initial_balance);
  SET p_new_account_id = LAST_INSERT_ID();
END$$

-- Practical 2: explicit cursor reads all parts that need replenishment.
CREATE PROCEDURE report_low_stock()
BEGIN
  DECLARE v_done BOOLEAN DEFAULT FALSE;
  DECLARE v_name VARCHAR(100);
  DECLARE v_qty INT;
  DECLARE v_reorder INT;
  DECLARE low_stock_cursor CURSOR FOR
    SELECT part_name, quantity_in_stock, reorder_level
    FROM service_parts WHERE quantity_in_stock <= reorder_level ORDER BY part_name;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;

  DROP TEMPORARY TABLE IF EXISTS low_stock_report;
  CREATE TEMPORARY TABLE low_stock_report (
    part_name VARCHAR(100), quantity_in_stock INT, reorder_level INT
  );
  OPEN low_stock_cursor;
  stock_loop: LOOP
    FETCH low_stock_cursor INTO v_name, v_qty, v_reorder;
    IF v_done THEN LEAVE stock_loop; END IF;
    INSERT INTO low_stock_report VALUES (v_name, v_qty, v_reorder);
  END LOOP;
  CLOSE low_stock_cursor;
  SELECT * FROM low_stock_report;
END$$

-- Practical 3: sample tiers for a service-parts order subtotal.
CREATE FUNCTION calculate_discount(p_amount DECIMAL(12,2))
RETURNS DECIMAL(5,2)
DETERMINISTIC
BEGIN
  IF p_amount >= 10000.00 THEN RETURN 15.00;
  ELSEIF p_amount >= 5000.00 THEN RETURN 10.00;
  ELSEIF p_amount >= 2000.00 THEN RETURN 5.00;
  ELSE RETURN 0.00;
  END IF;
END$$

-- Practical 4: atomically record a parts order and deduct stock.
CREATE PROCEDURE process_part_order(IN p_part_id BIGINT, IN p_quantity INT)
BEGIN
  DECLARE v_stock INT;
  DECLARE v_price DECIMAL(10,2);
  DECLARE v_subtotal DECIMAL(12,2);
  DECLARE v_discount DECIMAL(5,2);
  DECLARE v_total DECIMAL(12,2);
  DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

  IF p_quantity IS NULL OR p_quantity <= 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Order quantity must be greater than zero';
  END IF;
  START TRANSACTION;
  SELECT quantity_in_stock, unit_price INTO v_stock, v_price
    FROM service_parts WHERE id = p_part_id FOR UPDATE;
  IF v_stock IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Part was not found';
  END IF;
  IF v_stock < p_quantity THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient part stock';
  END IF;
  SET v_subtotal = v_price * p_quantity;
  SET v_discount = calculate_discount(v_subtotal);
  SET v_total = v_subtotal * (1 - v_discount / 100);
  INSERT INTO part_orders(part_id, quantity, order_total) VALUES (p_part_id, p_quantity, v_total);
  UPDATE service_parts SET quantity_in_stock = quantity_in_stock - p_quantity WHERE id = p_part_id;
  COMMIT;
  SELECT LAST_INSERT_ID() AS order_id, v_subtotal AS subtotal, v_discount AS discount_percent, v_total AS amount_to_pay;
END$$

-- Practical 5: transfer funds as one transaction; any error rolls everything back.
CREATE PROCEDURE transfer_wallet_funds(
  IN p_from_account BIGINT, IN p_to_account BIGINT, IN p_amount DECIMAL(12,2)
)
BEGIN
  DECLARE v_from_balance DECIMAL(12,2);
  DECLARE v_to_balance DECIMAL(12,2);
  DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

  IF p_amount IS NULL OR p_amount <= 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Transfer amount must be greater than zero';
  END IF;
  IF p_from_account = p_to_account THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Source and destination must differ';
  END IF;
  START TRANSACTION;
  SELECT balance INTO v_from_balance FROM wallet_accounts WHERE id = p_from_account FOR UPDATE;
  SELECT balance INTO v_to_balance FROM wallet_accounts WHERE id = p_to_account FOR UPDATE;
  IF v_from_balance IS NULL OR v_to_balance IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid account number';
  END IF;
  IF v_from_balance < p_amount THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient balance';
  END IF;
  UPDATE wallet_accounts SET balance = balance - p_amount WHERE id = p_from_account;
  UPDATE wallet_accounts SET balance = balance + p_amount WHERE id = p_to_account;
  INSERT INTO wallet_transfers(from_account_id, to_account_id, amount)
    VALUES (p_from_account, p_to_account, p_amount);
  COMMIT;
  SELECT 'Transfer completed' AS message, p_amount AS amount;
END$$

-- Practical 6: automatically record every wallet-balance change.
CREATE TRIGGER trg_wallet_balance_audit
AFTER UPDATE ON wallet_accounts
FOR EACH ROW
BEGIN
  IF OLD.balance <> NEW.balance THEN
    INSERT INTO wallet_balance_audit(account_id, old_balance, new_balance)
    VALUES (NEW.id, OLD.balance, NEW.balance);
  END IF;
END$$

DELIMITER ;

-- Try each routine after running the schema and routines scripts:
-- CALL create_wallet_account('New Rider', 1000.00, @new_id); SELECT @new_id;
-- CALL report_low_stock();
-- SELECT calculate_discount(6000.00) AS discount_percent;
-- CALL process_part_order(1, 1);
-- CALL transfer_wallet_funds(1, 2, 300.00);
-- SELECT * FROM wallet_balance_audit ORDER BY id DESC;
