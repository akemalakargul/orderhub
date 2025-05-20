/* ----------  CUSTOMERS  ---------- */
INSERT INTO customers (id, username, password, customer_id, email, full_name) VALUES
(1, 'customer1', '$2a$10$asIuiDfJh2MQtB5/O5s/I.skTXeh6sqj10FcJcJDcC0yAVhStc3.O', '1', 'customer1@example.com', 'John Doe'),
(2, 'customer2', '$2a$10$asIuiDfJh2MQtB5/O5s/I.skTXeh6sqj10FcJcJDcC0yAVhStc3.O', '2', 'customer2@example.com', 'Jane Smith'),
(3, 'customer3', '$2a$10$asIuiDfJh2MQtB5/O5s/I.skTXeh6sqj10FcJcJDcC0yAVhStc3.O', '3', 'customer3@example.com', 'Robert Johnson');

/* ----------  ASSETS  ---------- */
INSERT INTO assets (id, customer_id, asset_name, size, usable_size) VALUES
/* Customer 1  ---------------------------------------------------------- */
(1,  1, 'TRY',  1_000_000, 500_000),   -- cash: half already "reserved"
(2,  1, 'AAPL',        1_000,   800),
(3,  1, 'MSFT',         50,    50),
/* Customer 2  ---------------------------------------------------------- */
(4,  2, 'TRY',    750_000, 750_000),
(5,  2, 'GOOGL',        300,   300),
/* Customer 3  ---------------------------------------------------------- */
(6,  3, 'TRY',    250_000, 100_000),
(7,  3, 'TSLA',          50,    20);

/* ----------  ORDERS  ---------- */
INSERT INTO orders
  (id, customer_id, asset_name, order_side,  size,  price,  status,    create_date)
VALUES
/* Cust 1 orders -------------------------------------------------------- */
(101, 1, 'AAPL',  'BUY',   100, 180.50, 'PENDING',  '2025-05-15T09:00:00'),
(102, 1, 'AAPL',  'BUY',    80, 179.80, 'MATCHED',  '2025-05-14T14:32:10'),
(103, 1, 'AAPL',  'SELL',   20, 181.00, 'CANCELED', '2025-05-13T11:45:37'),
(104, 1, 'MSFT',  'SELL',    5, 300.00, 'PENDING', CURRENT_TIMESTAMP()),
/* Cust 2 orders -------------------------------------------------------- */
(105, 2, 'GOOGL', 'SELL',   50, 2700.00,'PENDING',  '2025-05-15T10:05:21'),
(106, 2, 'GOOGL', 'SELL',   30, 2685.00,'MATCHED',  '2025-05-14T15:12:00'),
(107, 2, 'GOOGL', 'BUY',     2, 2500.00,'MATCHED', DATEADD('DAY', -1, CURRENT_TIMESTAMP())),
/* Cust 3 orders -------------------------------------------------------- */
(108, 3, 'TSLA',  'BUY',    10,  720.00,'PENDING',  '2025-05-15T08:30:00'),
(109, 3, 'AAPL',  'SELL',   15, 155.00, 'CANCELED', DATEADD('DAY', -2, CURRENT_TIMESTAMP()));
