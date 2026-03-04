# Spring Data JPA Demo - Dia 02

Projeto demonstrativo completo de **Spring Data JPA** com exemplos práticos dos conceitos apresentados no Dia 02 do treinamento.

## 📋 Conteúdo do Projeto

Este projeto demonstra:

### ✅ Entidades JPA
- **Category** e **Product** (ManyToOne / OneToMany)
- **Post**, **Comment** e **Tag** (OneToMany e ManyToMany)
- **User** e **UserProfile** (OneToOne)
- Uso de `@PrePersist`, `@PreUpdate`, timestamps automáticos
- Relacionamentos bidirecionais com helper methods

### ✅ DTOs (Data Transfer Objects)
- Request DTOs com validação Bean Validation
- Response DTOs separando entidade da API
- Projeções para retornar apenas campos necessários

### ✅ Repositories
- **Query Methods** (findBy, existsBy, countBy, deleteBy)
- **JPQL** com `@Query`
- **Native SQL** com `@Query(nativeQuery = true)`
- **JOIN FETCH** para resolver N+1 problem
- **Paginação e Ordenação** com `Pageable`
- **@Modifying** para UPDATE e DELETE
- Queries complexas com múltiplos parâmetros opcionais

### ✅ Services
- Camada de lógica de negócio
- `@Transactional` para controle de transações
- Validações e regras de negócio

### ✅ Controllers REST
- CRUD completo
- Paginação com `@RequestParam`
- Validação com `@Valid`
- ResponseEntity com status HTTP adequados

### ✅ Exception Handling
- Exceções customizadas (ResourceNotFoundException, BusinessException, DuplicateResourceException)
- `@RestControllerAdvice` para tratamento global
- Respostas de erro padronizadas

---

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.6+
- (Opcional) PostgreSQL 15+ ou Podman

### Opção 1: H2 Database (Em Memória)

```bash
# Clone o repositório
cd dia-02/01-spring-data-jpa-demo

# Executar com Maven
mvn spring-boot:run

# Ou compilar e executar
mvn clean package
java -jar target/spring-data-jpa-demo-1.0.0.jar
```

A aplicação inicia em: **http://localhost:8080**  
H2 Console: **http://localhost:8080/h2-console**

**Configurações H2 Console:**
- JDBC URL: `jdbc:h2:mem:jpadb`
- Username: `sa`
- Password: (deixe em branco)

### Opção 2: PostgreSQL

1. **Criar banco de dados:**

```bash
# Com Podman
podman run --name postgres-jpa \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=java_training \
  -p 5432:5432 \
  -d docker.io/library/postgres:15

# Ou com PostgreSQL instalado
psql -U postgres
CREATE DATABASE java_training;
```

2. **Executar com profile prod:**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 📚 Endpoints da API

### Categories

```http
GET    /api/categories           # Listar todas
GET    /api/categories/{id}      # Buscar por ID
GET    /api/categories/active    # Listar ativas
POST   /api/categories           # Criar
PUT    /api/categories/{id}      # Atualizar
DELETE /api/categories/{id}      # Deletar
```

### Products

```http
GET    /api/products                           # Listar todos
GET    /api/products/{id}                      # Buscar por ID
GET    /api/products/paged?page=0&size=10      # Listar com paginação
GET    /api/products/search?name=laptop        # Busca avançada
GET    /api/products/summaries                 # Listar resumo (id, name, price)
GET    /api/products/category/{categoryId}     # Por categoria
GET    /api/products/category/{categoryId}/average-price  # Preço médio
POST   /api/products                           # Criar
PUT    /api/products/{id}                      # Atualizar
DELETE /api/products/{id}                      # Deletar
POST   /api/products/deactivate-out-of-stock   # Desativar sem estoque
```

### Posts

```http
GET    /api/posts                 # Listar todos
GET    /api/posts/{id}            # Buscar por ID
GET    /api/posts/paged           # Com paginação
GET    /api/posts/published       # Apenas publicados
GET    /api/posts/drafts          # Apenas rascunhos
POST   /api/posts                 # Criar
PUT    /api/posts/{id}            # Atualizar
POST   /api/posts/{id}/publish    # Publicar
DELETE /api/posts/{id}            # Deletar
```

### Comments

```http
GET    /api/posts/{postId}/comments              # Listar comentários
POST   /api/posts/{postId}/comments              # Criar comentário
DELETE /api/posts/{postId}/comments/{commentId}  # Deletar comentário
```

---

## 🧪 Exemplos de Requisições

### Criar Produto

```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "iPhone 15 Pro",
  "description": "Smartphone Apple mais recente",
  "price": 7999.00,
  "stock": 20,
  "categoryId": 1,
  "imageUrl": "https://example.com/iphone15.jpg"
}
```

### Busca Avançada de Produtos

```http
GET http://localhost:8080/api/products/search?name=laptop&minPrice=2000&maxPrice=5000&active=true&page=0&size=10
```

### Criar Post com Tags

```http
POST http://localhost:8080/api/posts
Content-Type: application/json

{
  "title": "Meu Primeiro Post sobre Spring",
  "content": "Conteúdo do post...",
  "author": "João Silva",
  "tagIds": [1, 2, 3]
}
```

### Adicionar Comentário

```http
POST http://localhost:8080/api/posts/1/comments
Content-Type: application/json

{
  "text": "Excelente post!",
  "author": "Maria Santos"
}
```

---

## 🎯 Conceitos Demonstrados

### 1. N+1 Problem e Soluções

**Problema:**
```java
// ❌ Gera N+1 queries
Post post = postRepository.findById(1L);
post.getComments().size(); // Lazy loading = nova query!
```

**Solução com JOIN FETCH:**
```java
// ✅ Uma única query
@Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :id")
Optional<Post> findByIdWithDetails(@Param("id") Long id);
```

### 2. Paginação

```java
// Controller
@GetMapping("/paged")
public Page<ProductResponse> findAll(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "id,asc") String[] sort) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(sort[0]));
    return productService.findAllPaged(pageable);
}
```

### 3. Validação com Bean Validation

```java
public record CreateProductRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 200)
    String name,
    
    @NotNull @Positive
    BigDecimal price,
    
    @NotNull @Min(0)
    Integer stock
) {}
```

### 4. Exception Handling Global

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(ResourceNotFoundException ex) {
        // Retorna 404 com mensagem customizada
    }
}
```

---

## 📊 Diagrama do Modelo de Dados

```
┌──────────────┐         ┌──────────────┐
│  Category    │         │   Product    │
├──────────────┤         ├──────────────┤
│ id (PK)      │◄────┐   │ id (PK)      │
│ name         │     └───│ category_id  │
│ description  │         │ name         │
│ active       │         │ price        │
└──────────────┘         │ stock        │
                         └──────────────┘

┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│    Post      │         │   Comment    │         │     Tag      │
├──────────────┤         ├──────────────┤         ├──────────────┤
│ id (PK)      │◄────┐   │ id (PK)      │   ┌────►│ id (PK)      │
│ title        │     └───│ post_id (FK) │   │     │ name         │
│ content      │         │ text         │   │     │ color        │
│ published    │         │ author       │   │     └──────────────┘
└──────────────┘         └──────────────┘   │              ▲
       │                                     │              │
       └─────────────────────────────────────┘              │
                   post_tags (join table)                   │
                                                            │
┌──────────────┐         ┌──────────────┐                  │
│    User      │         │ UserProfile  │                  │
├──────────────┤         ├──────────────┤                  │
│ id (PK)      │─────────│ id (PK)      │                  │
│ username     │  1:1    │ bio          │                  │
│ email        │         │ location     │                  │
│ password     │         │ avatar_url   │                  │
└──────────────┘         └──────────────┘                  │
```

---

## 🔍 Queries Interessantes

### Query Method
```java
List<Product> findByNameContainingIgnoreCaseAndPriceBetween(
    String keyword, BigDecimal min, BigDecimal max
);
```

### JPQL com Agregação
```java
@Query("SELECT AVG(p.price) FROM Product p WHERE p.category.id = :categoryId")
BigDecimal getAveragePriceByCategory(@Param("categoryId") Long categoryId);
```

### Native SQL
```java
@Query(value = "SELECT * FROM products WHERE price > :price " +
               "AND category_id IN (SELECT id FROM categories WHERE active = true)",
       nativeQuery = true)
List<Product> findExpensiveInActiveCategories(@Param("price") BigDecimal price);
```

### @Modifying
```java
@Modifying
@Transactional
@Query("UPDATE Product p SET p.active = false WHERE p.stock = 0")
int deactivateOutOfStock();
```

---

## 💡 Dicas de Estudo

1. **Explore o H2 Console** para ver as tabelas geradas e queries SQL
2. **Use o Postman** para testar os endpoints
3. **Leia os logs** - está configurado para mostrar SQL e parâmetros
4. **Experimente modificar** as queries e validações
5. **Teste cenários de erro** para ver o exception handling funcionando

---

## 🎓 Próximos Passos

- [ ] Adicionar testes unitários com JUnit e Mockito
- [ ] Implementar Specifications para queries dinâmicas
- [ ] Adicionar cache com Redis
- [ ] Implementar auditoria com @CreatedBy e @LastModifiedBy
- [ ] Adicionar Swagger/OpenAPI para documentação
- [ ] Implementar soft delete
- [ ] Adicionar filtros avançados com Criteria API

---

## 📝 Notas

- **Senha padrão dos usuários:** `password` (criptografada com BCrypt)
- **Profile dev:** Usa H2 em memória, recria schema a cada restart
- **Profile prod:** Usa PostgreSQL, mantém dados persistentes
- **Logs:** Nível DEBUG para Spring e Hibernate (ver SQL queries)

---

## 🤝 Contribuições

Este é um projeto educacional. Sinta-se à vontade para:
- Adicionar novos endpoints
- Implementar novos recursos
- Melhorar validações
- Adicionar testes

---

**Bons estudos! 🚀**
