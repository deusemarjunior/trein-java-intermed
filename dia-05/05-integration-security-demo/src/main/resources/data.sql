-- Inserir usuários de teste (senha: admin123 / user123 — BCrypt hash)
INSERT INTO users (id, email, password, role) VALUES (1, 'admin@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');
INSERT INTO users (id, email, password, role) VALUES (2, 'user@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER');

-- Inserir produtos de teste
INSERT INTO products (id, name, sku, price, description, created_at, updated_at) VALUES (1, 'Notebook Dell', 'NOT-0001', 4500.00, 'Notebook Dell Inspiron 15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, sku, price, description, created_at, updated_at) VALUES (2, 'Mouse Logitech', 'MOU-0001', 150.00, 'Mouse sem fio Logitech MX Master', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, sku, price, description, created_at, updated_at) VALUES (3, 'Teclado Mecânico', 'TEC-0001', 350.00, 'Teclado mecânico RGB', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, sku, price, description, created_at, updated_at) VALUES (4, 'Monitor 27"', 'MON-0001', 1800.00, 'Monitor LG 27" 4K', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, sku, price, description, created_at, updated_at) VALUES (5, 'Headset Gamer', 'HEA-0001', 250.00, 'Headset HyperX Cloud II', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
