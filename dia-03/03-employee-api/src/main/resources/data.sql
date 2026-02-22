-- Departamentos
INSERT INTO departments (id, name) VALUES (1, 'Tecnologia');
INSERT INTO departments (id, name) VALUES (2, 'Recursos Humanos');
INSERT INTO departments (id, name) VALUES (3, 'Financeiro');
INSERT INTO departments (id, name) VALUES (4, 'Comercial');

-- Funcionários
INSERT INTO employees (name, email, cpf, salary, department_id, created_at, updated_at)
VALUES ('João Silva', 'joao.silva@empresa.com', '52998224725', 5000.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO employees (name, email, cpf, salary, department_id, created_at, updated_at)
VALUES ('Maria Santos', 'maria.santos@empresa.com', '11144477735', 6500.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO employees (name, email, cpf, salary, department_id, created_at, updated_at)
VALUES ('Carlos Oliveira', 'carlos.oliveira@empresa.com', '98765432100', 4200.00, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO employees (name, email, cpf, salary, department_id, created_at, updated_at)
VALUES ('Ana Costa', 'ana.costa@empresa.com', '45678912300', 7800.00, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
