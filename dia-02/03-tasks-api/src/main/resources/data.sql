-- Tasks test data
INSERT INTO tasks (title, description, completed, priority, due_date, completed_at, created_at, updated_at) 
VALUES 
('Estudar Spring Data JPA', 'Revisar conceitos de repositórios e query methods', false, 'HIGH', '2024-12-31 18:00:00', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Implementar API REST', 'Criar endpoints para CRUD completo', false, 'URGENT', '2024-12-25 17:00:00', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Revisar código do projeto', 'Code review e refatoração', false, 'MEDIUM', '2024-12-28 12:00:00', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Documentar API', 'Criar documentação Swagger/OpenAPI', false, 'LOW', '2025-01-05 10:00:00', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Setup CI/CD', 'Configurar pipeline de deploy', true, 'HIGH', '2024-12-20 15:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Escrever testes unitários', 'Cobertura mínima de 80%', false, 'HIGH', '2024-12-27 16:00:00', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Configurar monitoramento', 'Adicionar logs e métricas', false, 'MEDIUM', '2025-01-10 14:00:00', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fix bug de validação', 'Corrigir validação de campos obrigatórios', true, 'URGENT', '2024-12-22 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Migrar para PostgreSQL', 'Substituir H2 por PostgreSQL em produção', false, 'MEDIUM', '2025-01-15 13:00:00', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Adicionar autenticação', 'Implementar Spring Security', false, 'HIGH', '2025-01-08 11:00:00', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
