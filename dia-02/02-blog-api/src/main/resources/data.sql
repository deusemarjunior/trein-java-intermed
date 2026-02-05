-- Dados iniciais para Blog API

-- Tags
INSERT INTO tags (name, color) VALUES
('Java', '#007396'),
('Spring', '#6DB33F'),
('JPA', '#FF6B6B'),
('Tutorial', '#4ECDC4'),
('Avançado', '#FFD93D');

-- Posts
INSERT INTO posts (title, content, author, created_at, updated_at) VALUES
('Entendendo Relacionamentos JPA', 'Neste post vamos explorar OneToOne, OneToMany e ManyToMany em profundidade...', 'João Silva', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Problema N+1 e Como Resolver', 'O problema N+1 é um dos mais comuns em aplicações JPA. Veja como identificar e corrigir...', 'Maria Santos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fetch Types: LAZY vs EAGER', 'Quando usar cada tipo de fetch? Esta é uma dúvida comum...', 'Pedro Costa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Relacionamento Post-Tags (ManyToMany)
INSERT INTO post_tags (post_id, tag_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(2, 1), (2, 3), (2, 5),
(3, 1), (3, 3), (3, 4);

-- Comments
INSERT INTO comments (text, author, post_id, created_at) VALUES
('Excelente explicação! Muito útil.', 'Ana Paula', 1, CURRENT_TIMESTAMP),
('Consegui resolver meu problema graças a este post!', 'Carlos Mendes', 1, CURRENT_TIMESTAMP),
('Poderia dar mais exemplos práticos?', 'Lucia Ferreira', 1, CURRENT_TIMESTAMP),
('Finalmente entendi o problema N+1!', 'Rafael Lima', 2, CURRENT_TIMESTAMP),
('Salvou meu projeto. Estava com performance horrível.', 'Beatriz Souza', 2, CURRENT_TIMESTAMP),
('JOIN FETCH é a solução mesmo.', 'Fernando Alves', 2, CURRENT_TIMESTAMP),
('Sempre uso LAZY, nunca EAGER.', 'Julia Martins', 3, CURRENT_TIMESTAMP);
