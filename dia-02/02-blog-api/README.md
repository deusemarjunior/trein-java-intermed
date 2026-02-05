# Blog API - Exercício 1

## 🎯 Objetivo do Exercício

Este projeto foca em **relacionamentos JPA** e demonstra:

- ✅ **OneToMany** / **ManyToOne** (Post ↔ Comment)
- ✅ **ManyToMany** (Post ↔ Tag)
- ✅ **FetchType.LAZY** vs **FetchType.EAGER**
- ✅ **Problema N+1** e como resolver com **JOIN FETCH**
- ✅ **Helper methods** para relacionamentos bidirecionais
- ✅ **Cascade operations**
- ✅ **orphanRemoval**

---

## 🏗️ Modelo de Dados

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│    Post      │         │   Comment    │         │     Tag      │
├──────────────┤         ├──────────────┤         ├──────────────┤
│ id (PK)      │◄────┐   │ id (PK)      │   ┌────►│ id (PK)      │
│ title        │     └───│ post_id (FK) │   │     │ name         │
│ content      │         │ text         │   │     │ color        │
│ author       │         │ author       │   │     └──────────────┘
│ created_at   │         └──────────────┘   │              ▲
│ updated_at   │                            │              │
└──────────────┘                            │              │
       │                                    │              │
       └────────────────────────────────────┘              │
                  post_tags (join table)  ─────────────────┘
```

### Relacionamentos:

1. **Post ↔ Comment** (OneToMany / ManyToOne)
   - Um Post tem muitos Comments
   - Um Comment pertence a um Post
   - Cascade ALL: deletar Post deleta Comments
   - orphanRemoval: remover comment da lista deleta do BD

2. **Post ↔ Tag** (ManyToMany)
   - Um Post tem muitas Tags
   - Uma Tag está em muitos Posts
   - Tabela intermediária: `post_tags`

---

## 🚀 Como Executar

```bash
cd dia-02/02-blog-api
mvn spring-boot:run
```

**Porta:** 8081  
**H2 Console:** http://localhost:8081/h2-console

**Configurações H2:**
- JDBC URL: `jdbc:h2:mem:blogdb`
- Username: `sa`
- Password: (vazio)

---

## 📚 Conceitos Demonstrados

### 1. Problema N+1

**❌ O Problema:**

```java
// Busca 1 post
Post post = postRepository.findById(1L).get();

// Acessa comments - NOVA QUERY para buscar comments!
int commentCount = post.getComments().size();  // Query #2

// Acessa tags - NOVA QUERY para buscar tags!
int tagCount = post.getTags().size();  // Query #3

// Total: 3 queries!
```

**Isso acontece com:**
```java
Post findById(Long id)  // Método padrão do JpaRepository
```

**✅ A Solução: JOIN FETCH**

```java
@Query("SELECT DISTINCT p FROM Post p " +
       "LEFT JOIN FETCH p.comments " +
       "LEFT JOIN FETCH p.tags " +
       "WHERE p.id = :id")
Optional<Post> findByIdWithCommentsAndTags(@Param("id") Long id);

// Agora: apenas 1 query traz tudo!
```

### 2. FetchType.LAZY vs EAGER

```java
// LAZY (padrão para OneToMany e ManyToMany)
@OneToMany(fetch = FetchType.LAZY)
private List<Comment> comments;
// Comments NÃO são buscados automaticamente
// Busca apenas quando você acessa: post.getComments()

// EAGER (padrão para ManyToOne e OneToOne)
@ManyToOne(fetch = FetchType.EAGER)
private Post post;
// Post É buscado automaticamente junto com Comment
```

**⚠️ Regra de Ouro:** Use LAZY sempre que possível!

### 3. Helper Methods

**Por que usar?**

Relacionamentos bidirecionais precisam ser sincronizados em AMBOS os lados:

```java
// ❌ ERRADO - dessincronia!
Comment comment = new Comment("Ótimo post!", "João");
comment.setPost(post);
post.getComments().add(comment);  // Esqueceu de fazer isso!

// ✅ CORRETO - use helper method
public void addComment(Comment comment) {
    comments.add(comment);      // Adiciona na lista
    comment.setPost(this);      // Seta o post no comment
}

// Uso:
post.addComment(comment);  // Sincroniza tudo automaticamente!
```

### 4. Cascade Operations

```java
@OneToMany(cascade = CascadeType.ALL)
private List<Comment> comments;

// CascadeType.ALL significa:
// - PERSIST: salvar Post salva Comments automaticamente
// - MERGE: atualizar Post atualiza Comments
// - REMOVE: deletar Post deleta Comments
// - REFRESH, DETACH
```

### 5. orphanRemoval

```java
@OneToMany(orphanRemoval = true)
private List<Comment> comments;

// Quando você remove da lista:
post.getComments().remove(comment);
// O comment é DELETADO do banco de dados automaticamente!
```

---

## 🧪 Exemplos de Teste

### Teste 1: Problema N+1

```bash
# 1. Buscar post SEM JOIN FETCH (problema N+1)
GET http://localhost:8081/api/posts/1

# Veja nos logs: múltiplas queries!
# Query 1: SELECT * FROM posts WHERE id = 1
# Query 2: SELECT * FROM comments WHERE post_id = 1
# Query 3: SELECT * FROM post_tags WHERE post_id = 1
# Query 4: SELECT * FROM tags WHERE id IN (...)
```

### Teste 2: Solução com JOIN FETCH

```bash
# 2. Buscar post COM JOIN FETCH (solução)
GET http://localhost:8081/api/posts/1/with-details

# Veja nos logs: apenas 1 query!
# Query única com JOINs
```

### Teste 3: Cascade e orphanRemoval

```bash
# Criar post com comments
POST http://localhost:8081/api/posts
{
  "title": "Novo Post",
  "content": "Conteúdo...",
  "author": "Teste"
}

# Deletar post - comments são deletados automaticamente (cascade)
DELETE http://localhost:8081/api/posts/4
```

---

## 📊 Queries SQL Geradas

### Estrutura das Tabelas

```sql
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

CREATE TABLE tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    color VARCHAR(7)
);

-- Tabela intermediária ManyToMany
CREATE TABLE post_tags (
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);
```

---

## 💡 Exercícios Práticos

### Exercício 1: Adicionar Categoria

Expanda o modelo adicionando:

```java
@Entity
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @OneToMany(mappedBy = "category")
    private List<Post> posts;
}

// Em Post:
@ManyToOne
@JoinColumn(name = "category_id")
private Category category;
```

### Exercício 2: Busca por Tags

Implemente endpoint:

```
GET /api/posts/by-tag/{tagName}
```

### Exercício 3: Posts Mais Comentados

Implemente endpoint que retorna top 5 posts com mais comentários:

```
GET /api/posts/most-commented?limit=5
```

---

## 🎓 Lições Aprendidas

1. ✅ Use **LAZY** por padrão, **EAGER** apenas quando necessário
2. ✅ Sempre use **JOIN FETCH** quando precisar carregar relacionamentos
3. ✅ Use **helper methods** para manter sincronização bidirecional
4. ✅ Configure **cascade** apropriadamente (cuidado com CascadeType.ALL!)
5. ✅ **orphanRemoval** é útil para composições (parte não existe sem o todo)
6. ✅ Monitore logs SQL para identificar problema N+1

---

## 🔗 Próximos Passos

Após dominar este projeto, vá para:
- **03-tasks-api** - API completa com todas as features
- Estude **@EntityGraph** como alternativa a JOIN FETCH
- Explore **Criteria API** para queries dinâmicas

---

**Bom estudo! 🚀**
