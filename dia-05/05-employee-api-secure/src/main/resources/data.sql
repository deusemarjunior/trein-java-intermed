-- ============================================================
-- Dados iniciais — Employee API Secure (Exercício Dia 05)
-- ============================================================

-- Departamentos
INSERT INTO departments (name, code) VALUES ('Tecnologia', 'TEC');
INSERT INTO departments (name, code) VALUES ('Recursos Humanos', 'RH');
INSERT INTO departments (name, code) VALUES ('Financeiro', 'FIN');

-- Usuários (senhas com BCrypt — admin123 / user123)
INSERT INTO users (email, password, role) VALUES
    ('admin@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');
INSERT INTO users (email, password, role) VALUES
    ('user@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER');

-- Funcionários
INSERT INTO employees (name, email, cpf, salary, department_id, created_at, updated_at) VALUES
    ('Ana Silva', 'ana.silva@empresa.com', '111.111.111-11', 8500.00, 1, NOW(), NOW());
INSERT INTO employees (name, email, cpf, salary, department_id, created_at, updated_at) VALUES
    ('Bruno Costa', 'bruno.costa@empresa.com', '222.222.222-22', 6200.00, 2, NOW(), NOW());
INSERT INTO employees (name, email, cpf, salary, department_id, created_at, updated_at) VALUES
    ('Carla Mendes', 'carla.mendes@empresa.com', '333.333.333-33', 9100.00, 1, NOW(), NOW());
INSERT INTO employees (name, email, cpf, salary, department_id, created_at, updated_at) VALUES
    ('Daniel Oliveira', 'daniel.oliveira@empresa.com', '444.444.444-44', 5800.00, 3, NOW(), NOW());
