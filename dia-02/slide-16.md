# Slide 16: Query Methods & JPQL

**Horário:** 13:20 - 13:40

---

## 📝 JPQL - Java Persistence Query Language

### O que é JPQL?

JPQL é uma linguagem de consulta orientada a **objetos** (não tabelas!), similar ao SQL, mas trabalha com entidades Java ao invés de tabelas de banco de dados.

### Diferença entre SQL e JPQL

```sql
-- SQL (tabelas e colunas)
SELECT p.id, p.name, p.price 
FROM products p 
WHERE p.category_id = 5;

-- JPQL (entidades e atributos)
SELECT p 
FROM Product p 
WHERE p.category.id = 5
```

**Principais diferenças:**
- SQL → Tabelas e colunas
- JPQL → Entidades e atributos Java
- SQL → `category_id` (FK)
- JPQL → `p.category.id` (navegação de objeto)

---

## 🎯 @Query Examples

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 1. JPQL Simples
    @Query("SELECT p FROM Product p WHERE p.active = true")
    List<Product> findAllActive();
    
    // 2. Com parâmetros nomeados (:nome)
    @Query("SELECT p FROM Product p WHERE p.price > :minPrice")
    List<Product> findExpensive(@Param("minPrice") BigDecimal minPrice);
    
    // 3. JOIN (navegação de relacionamento)
    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);
    
    // 4. LEFT JOIN FETCH (evitar N+1) ⭐
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.reviews " +
           "WHERE p.id = :id")
    Optional<Product> findByIdWithReviews(@Param("id") Long id);
    
    // 5. Agregação
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.category.id = :categoryId")
    BigDecimal getAveragePriceByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock > 0")
    long countInStock();
    
    // 6. UPDATE ⚠️ Requer @Modifying
    @Modifying
    @Query("UPDATE Product p SET p.active = false WHERE p.stock = 0")
    int deactivateOutOfStock();
    
    // 7. DELETE ⚠️ Requer @Modifying
    @Modifying
    @Query("DELETE FROM Product p WHERE p.createdAt < :date")
    int deleteOlderThan(@Param("date") LocalDateTime date);
    
    // 8. DTO Projection (construtor)
    @Query("SELECT new com.example.dto.ProductSummary(p.id, p.name, p.price) " +
           "FROM Product p WHERE p.active = true")
    List<ProductSummary> findAllSummaries();
    
    // 9. Native SQL (quando JPQL não é suficiente)
    @Query(value = "SELECT * FROM products p " +
                   "WHERE p.price > :price " +
                   "AND p.category_id IN (SELECT id FROM categories WHERE active = true)",
           nativeQuery = true)
    List<Product> complexNativeQuery(@Param("price") BigDecimal price);
}
```

---

## 🔧 @Modifying - UPDATE e DELETE

Quando usar `@Query` para modificar dados (UPDATE/DELETE), você DEVE usar `@Modifying`:

```java
@Modifying
@Transactional  // ⚠️ Obrigatório!
@Query("UPDATE Task t SET t.completed = true WHERE t.dueDate < :now")
int markOverdueTasks(@Param("now") LocalDateTime now);
```

**Importante:**
- `@Modifying` informa ao Spring que a query altera dados
- Retorna `int` (número de registros afetados)
- Requer `@Transactional` na camada de serviço
- Cuidado: não atualiza o contexto de persistência automaticamente

---

## 🎨 Projeções com JPQL

### 1. Projeção com Construtor (DTO)

```java
// DTO
public class ProductSummary {
    private Long id;
    private String name;
    private BigDecimal price;
    
    public ProductSummary(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}

// Repository
@Query("SELECT new com.example.dto.ProductSummary(p.id, p.name, p.price) " +
       "FROM Product p")
List<ProductSummary> findAllSummaries();
```

### 2. Projeção com Interface

```java
// Interface de projeção
public interface ProductNameAndPrice {
    String getName();
    BigDecimal getPrice();
}

// Repository
@Query("SELECT p.name as name, p.price as price FROM Product p")
List<ProductNameAndPrice> findAllProjected();
```

---

## ⚡ JOIN FETCH vs JOIN

```java
// JOIN normal - pode causar N+1
@Query("SELECT p FROM Product p JOIN p.category c WHERE c.active = true")
List<Product> findWithActiveCategory();

// JOIN FETCH - carrega categoria junto (1 query) ✅
@Query("SELECT DISTINCT p FROM Product p JOIN FETCH p.category WHERE p.category.active = true")
List<Product> findWithActiveCategoryFetch();

// Múltiplos FETCH
@Query("SELECT DISTINCT p FROM Product p " +
       "LEFT JOIN FETCH p.category " +
       "LEFT JOIN FETCH p.reviews")
List<Product> findAllWithDetails();
```

**DISTINCT é importante** quando usa FETCH com coleções para evitar duplicatas.

---

## 🆚 JPQL vs Native SQL

| Critério | JPQL ✅ | Native SQL |
|----------|---------|------------|
| Portabilidade | Funciona em qualquer DB | Específico do DB |
| Sintaxe | Orientada a objetos | SQL puro |
| Relacionamentos | Navegação natural | JOINs manuais |
| Performance | Otimizada pelo JPA | Controle total |
| Quando usar | Maioria dos casos | Queries complexas, funções específicas do DB |

**Use Native SQL quando:**
- Precisa de funções específicas do PostgreSQL (`ARRAY_AGG`, window functions)
- Queries muito complexas
- Performance crítica com hints específicos
- Chamadas a procedures/functions

---

## 🎬 DEMO: Queries Complexas

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Buscar pedidos do último mês com itens
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.items " +
           "WHERE o.createdAt >= :startDate")
    List<Order> findRecentWithItems(@Param("startDate") LocalDateTime startDate);
    
    // Total de vendas por categoria
    @Query("SELECT c.name, SUM(oi.price * oi.quantity) " +
           "FROM OrderItem oi " +
           "JOIN oi.product p " +
           "JOIN p.category c " +
           "GROUP BY c.name " +
           "ORDER BY SUM(oi.price * oi.quantity) DESC")
    List<Object[]> getSalesByCategory();
    
    // Top 10 produtos mais vendidos
    @Query("SELECT p.name, SUM(oi.quantity) as total " +
           "FROM OrderItem oi " +
           "JOIN oi.product p " +
           "GROUP BY p.id, p.name " +
           "ORDER BY total DESC")
    List<Object[]> getTopProducts(Pageable pageable);
    
    // Exemplo de chamada:
    // List<Object[]> top10 = repository.getTopProducts(PageRequest.of(0, 10));
}
```

---

## 🏋️ Exercício Prático (10 min)

Para o `TaskRepository`, crie queries usando `@Query`:

1. Buscar tarefas atrasadas (dueDate < hoje e não completed)
2. Contar tarefas por status
3. Buscar tarefas do usuário com tags (JOIN FETCH)
4. Marcar todas as tarefas de um projeto como arquivadas (UPDATE)
5. Calcular média de tempo de conclusão por projeto

```java
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Implementar aqui
}
```

**Próximo:** Paginação e Ordenação →
