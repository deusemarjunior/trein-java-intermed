-- Produtos para pedidos
INSERT INTO product (id, name, price, category) VALUES (1, 'Notebook Dell', 4500.00, 'ELECTRONICS');
INSERT INTO product (id, name, price, category) VALUES (2, 'Mouse Logitech', 150.00, 'ELECTRONICS');
INSERT INTO product (id, name, price, category) VALUES (3, 'Cadeira Gamer', 1200.00, 'FURNITURE');
INSERT INTO product (id, name, price, category) VALUES (4, 'Monitor 27"', 2000.00, 'ELECTRONICS');
INSERT INTO product (id, name, price, category) VALUES (5, 'Teclado Mecânico', 350.00, 'ELECTRONICS');

-- Pedidos
INSERT INTO customer_order (id, customer_name, customer_email, status, shipping_region, total, created_at) VALUES (1, 'João Silva', 'joao@email.com', 'COMPLETED', 'SUDESTE', 4650.00, '2024-01-15 10:30:00');
INSERT INTO customer_order (id, customer_name, customer_email, status, shipping_region, total, created_at) VALUES (2, 'Maria Santos', 'maria@email.com', 'PENDING', 'NORDESTE', 1200.00, '2024-01-16 14:00:00');

-- Itens dos pedidos
INSERT INTO order_item (id, order_id, product_id, quantity, unit_price, subtotal) VALUES (1, 1, 1, 1, 4500.00, 4500.00);
INSERT INTO order_item (id, order_id, product_id, 	quantity, unit_price, subtotal) VALUES (2, 1, 2, 1, 150.00, 150.00);
INSERT INTO order_item (id, order_id, product_id, quantity, unit_price, subtotal) VALUES (3, 2, 3, 1, 1200.00, 1200.00);
