-- Script de dados iniciais para H2
-- Executado automaticamente no profile 'dev'

-- Categorias
INSERT INTO categories (name, description, active, created_at, updated_at) VALUES
('Eletrônicos', 'Produtos eletrônicos e gadgets', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Livros', 'Livros e e-books', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Roupas', 'Vestuário e acessórios', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Alimentos', 'Produtos alimentícios', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Esportes', 'Artigos esportivos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Produtos
INSERT INTO products (name, description, price, stock, active, category_id, image_url, created_at, updated_at) VALUES
('Laptop Dell XPS 13', 'Notebook ultrafino com processador Intel i7', 4500.00, 15, true, 1, 'https://example.com/dell-xps.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mouse Logitech MX Master', 'Mouse ergonômico sem fio', 350.00, 50, true, 1, 'https://example.com/mouse.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Teclado Mecânico Keychron K2', 'Teclado mecânico wireless', 550.00, 30, true, 1, 'https://example.com/keyboard.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Clean Code - Robert Martin', 'Livro sobre código limpo e boas práticas', 85.00, 100, true, 2, 'https://example.com/clean-code.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Design Patterns - Gang of Four', 'Padrões de projeto clássicos', 120.00, 45, true, 2, 'https://example.com/design-patterns.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Camiseta Básica Preta', 'Camiseta 100% algodão', 45.00, 200, true, 3, 'https://example.com/tshirt.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Calça Jeans Slim', 'Calça jeans modelo slim fit', 150.00, 80, true, 3, 'https://example.com/jeans.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Café Premium 500g', 'Café especial torrado em grãos', 35.00, 150, true, 4, 'https://example.com/coffee.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Chocolate 70% Cacau', 'Chocolate amargo premium', 12.00, 300, true, 4, 'https://example.com/chocolate.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bola de Futebol Nike', 'Bola oficial de futebol', 180.00, 25, true, 5, 'https://example.com/soccer-ball.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Raquete de Tênis Wilson', 'Raquete profissional de tênis', 800.00, 12, true, 5, 'https://example.com/tennis-racket.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fone Bluetooth JBL', 'Fone de ouvido sem fio', 250.00, 0, true, 1, 'https://example.com/headphone.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Tags
INSERT INTO tags (name, color) VALUES
('Java', '#007396'),
('Spring', '#6DB33F'),
('Tutorial', '#FF6B6B'),
('Novidade', '#4ECDC4'),
('Dica', '#FFD93D');

-- Posts
INSERT INTO posts (title, content, author, published, created_at, updated_at) VALUES
('Introdução ao Spring Data JPA', 'Neste post vamos aprender os conceitos básicos de Spring Data JPA e como utilizá-lo em projetos reais...', 'João Silva', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Otimizando Queries com JPQL', 'Aprenda técnicas avançadas para escrever queries eficientes usando JPQL...', 'Maria Santos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Relacionamentos em JPA', 'Entenda os diferentes tipos de relacionamentos: OneToOne, OneToMany, ManyToMany...', 'Pedro Costa', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Post-Tags (relacionamento ManyToMany)
INSERT INTO post_tags (post_id, tag_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 5),
(3, 1), (3, 2), (3, 3);

-- Comments
INSERT INTO comments (text, author, post_id, created_at) VALUES
('Ótimo tutorial! Muito bem explicado.', 'Ana Paula', 1, CURRENT_TIMESTAMP),
('Consegui implementar no meu projeto. Obrigado!', 'Carlos Mendes', 1, CURRENT_TIMESTAMP),
('Poderia fazer um sobre Criteria API também?', 'Lucia Ferreira', 1, CURRENT_TIMESTAMP),
('Essas dicas salvaram meu projeto!', 'Rafael Lima', 2, CURRENT_TIMESTAMP),
('Muito útil para otimização de performance.', 'Beatriz Souza', 2, CURRENT_TIMESTAMP);

-- Users e UserProfiles
INSERT INTO user_profiles (bio, location, website, avatar_url) VALUES
('Desenvolvedor Full Stack apaixonado por tecnologia', 'São Paulo, SP', 'https://johndoe.dev', 'https://example.com/avatar1.jpg'),
('Tech Lead com 10 anos de experiência', 'Rio de Janeiro, RJ', 'https://mariadev.com', 'https://example.com/avatar2.jpg'),
('Estudante de Ciência da Computação', 'Belo Horizonte, MG', null, 'https://example.com/avatar3.jpg');

INSERT INTO users (username, email, password, profile_id, created_at) VALUES
('johndoe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, CURRENT_TIMESTAMP),
('mariasilva', 'maria@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, CURRENT_TIMESTAMP),
('pedroalves', 'pedro@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 3, CURRENT_TIMESTAMP);
