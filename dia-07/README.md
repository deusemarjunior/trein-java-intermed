# Dia 7 - Bancos de Dados e Cache

**Duração**: 5 horas  
**Objetivo**: Dominar persistência com SQL/NoSQL, otimização de queries e estratégias de cache

## 📋 Conteúdo Programático

### Manhã (3 horas)

#### 1. SQL Avançado com JPA/Hibernate (1.5h)

**Otimização de queries**
```java
// Problema N+1
@Entity
public class Order {
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items; // Cada item gera uma query!
}

// Solução 1: JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
Optional<Order> findByIdWithItems(@Param("id") Long id);

// Solução 2: EntityGraph
@EntityGraph(attributePaths = {"items", "items.product"})
Optional<Order> findById(Long id);

// Solução 3: DTO Projection
@Query("""
    SELECT new com.example.dto.OrderSummary(
        o.id, o.customer.name, COUNT(i), SUM(i.total)
    )
    FROM Order o
    JOIN o.items i
    WHERE o.status = :status
    GROUP BY o.id, o.customer.name
    """)
List<OrderSummary> findOrderSummaries(@Param("status") OrderStatus status);
```

**Índices e performance**
```java
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_name", columnList = "name"),
    @Index(name = "idx_product_category", columnList = "category_id"),
    @Index(name = "idx_product_name_category", columnList = "name, category_id")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
```

**Queries nativas e procedures**
```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Query nativa
    @Query(value = """
        SELECT p.* FROM products p
        WHERE p.price BETWEEN :minPrice AND :maxPrice
        AND p.stock > 0
        ORDER BY p.created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Product> findAvailableInPriceRange(
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("limit") int limit
    );
    
    // Stored procedure
    @Procedure(name = "Product.updatePriceByCategory")
    void updatePricesByCategory(
        @Param("categoryId") Long categoryId,
        @Param("percentage") BigDecimal percentage
    );
}
```

**Batch operations**
```java
@Service
public class ProductService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public void importProducts(List<Product> products) {
        int batchSize = 50;
        for (int i = 0; i < products.size(); i++) {
            entityManager.persist(products.get(i));
            
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
    
    @Transactional
    public void updatePricesBatch(Map<Long, BigDecimal> updates) {
        // Usar batch update
        String jpql = "UPDATE Product p SET p.price = :price WHERE p.id = :id";
        Query query = entityManager.createQuery(jpql);
        
        for (Map.Entry<Long, BigDecimal> entry : updates.entrySet()) {
            query.setParameter("id", entry.getKey());
            query.setParameter("price", entry.getValue());
            query.executeUpdate();
        }
    }
}
```

**Transações e isolamento**
```java
@Service
public class OrderService {
    
    @Transactional(
        isolation = Isolation.SERIALIZABLE,
        propagation = Propagation.REQUIRED,
        rollbackFor = Exception.class,
        timeout = 30
    )
    public OrderId createOrder(CreateOrderCommand command) {
        // Operações transacionais
        // Se qualquer exceção ocorrer, rollback automático
        
        Product product = productRepository.findById(command.productId())
            .orElseThrow(() -> new ProductNotFoundException());
        
        product.decreaseStock(command.quantity());
        productRepository.save(product);
        
        Order order = Order.create(command);
        orderRepository.save(order);
        
        return order.getId();
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logOrderEvent(Long orderId, String event) {
        // Nova transação independente
        // Commit mesmo se a transação principal falhar
        auditRepository.save(new AuditLog(orderId, event));
    }
}
```

#### 2. Azure SQL / PostgreSQL (30min)

**Configuração Spring Boot**
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://server.postgres.database.azure.com:5432/mydb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
  
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
    show-sql: false
```

**Connection pooling e performance**
```java
@Configuration
public class DatabaseConfig {
    
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return config;
    }
}
```

#### 3. NoSQL com MongoDB (1h)

**Configuração**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/ecommerce
      auto-index-creation: true
```

**Document model**
```java
@Document(collection = "products")
public class ProductDocument {
    
    @Id
    private String id;
    
    @Indexed
    private String name;
    
    private String description;
    
    private BigDecimal price;
    
    @Indexed
    private String categoryId;
    
    @DBRef
    private Category category;
    
    private List<String> tags;
    
    private Map<String, Object> attributes;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

**Repository com queries**
```java
public interface ProductDocumentRepository extends MongoRepository<ProductDocument, String> {
    
    // Query methods
    List<ProductDocument> findByNameContainingIgnoreCase(String name);
    
    List<ProductDocument> findByPriceBetween(BigDecimal min, BigDecimal max);
    
    List<ProductDocument> findByTagsContaining(String tag);
    
    // Query annotation
    @Query("{ 'category': ?0, 'price': { $lte: ?1 } }")
    List<ProductDocument> findByCategoryAndMaxPrice(String categoryId, BigDecimal maxPrice);
    
    // Aggregation
    @Aggregation(pipeline = {
        "{ $match: { 'categoryId': ?0 } }",
        "{ $group: { _id: '$categoryId', avgPrice: { $avg: '$price' } } }"
    })
    AggregationResult getAveragePriceByCategory(String categoryId);
    
    // Text search
    @Query("{ $text: { $search: ?0 } }")
    List<ProductDocument> searchByText(String text);
}
```

**Embedded documents vs references**
```java
// Embedded - melhor para dados que sempre são acessados juntos
@Document
public class Order {
    @Id
    private String id;
    
    private List<OrderItem> items; // Embedded
    
    private Address shippingAddress; // Embedded
}

// Reference - melhor para dados grandes ou compartilhados
@Document
public class Order {
    @Id
    private String id;
    
    @DBRef
    private Customer customer; // Reference
    
    @DBRef
    private List<Product> products; // Reference
}
```

### Tarde (2 horas)

#### 4. Redis - Cache e Estratégias (1.5h)

**Configuração**
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD}
      timeout: 60000
      jedis:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
```

**Cache com annotations**
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("products", 
                config.entryTtl(Duration.ofHours(1)))
            .withCacheConfiguration("categories", 
                config.entryTtl(Duration.ofDays(1)))
            .build();
    }
}

@Service
public class ProductService {
    
    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        // Cache miss: busca no banco
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    @CachePut(value = "products", key = "#result.id")
    public Product update(Long id, UpdateProductCommand command) {
        Product product = findById(id);
        product.update(command);
        return productRepository.save(product);
    }
    
    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void clearCache() {
        // Limpa todo o cache de produtos
    }
}
```

**Redis Template para casos avançados**
```java
@Service
public class SessionService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public void saveUserSession(String userId, UserSession session) {
        String key = "session:" + userId;
        redisTemplate.opsForValue().set(key, session, Duration.ofHours(2));
    }
    
    public Optional<UserSession> getUserSession(String userId) {
        String key = "session:" + userId;
        UserSession session = (UserSession) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(session);
    }
    
    public void incrementViewCount(String productId) {
        String key = "views:" + productId;
        redisTemplate.opsForValue().increment(key);
    }
    
    public void addToRecentlyViewed(String userId, String productId) {
        String key = "recent:" + userId;
        redisTemplate.opsForList().leftPush(key, productId);
        redisTemplate.opsForList().trim(key, 0, 9); // Mantém últimos 10
    }
    
    public Set<String> getFeaturedProducts() {
        String key = "featured:products";
        return redisTemplate.opsForSet().members(key);
    }
}
```

**Estratégias de cache**
```java
// 1. Cache-Aside (Lazy Loading)
public Product getProduct(Long id) {
    String key = "product:" + id;
    Product cached = (Product) redisTemplate.opsForValue().get(key);
    
    if (cached == null) {
        cached = productRepository.findById(id).orElseThrow();
        redisTemplate.opsForValue().set(key, cached, Duration.ofMinutes(30));
    }
    
    return cached;
}

// 2. Write-Through
public Product saveProduct(Product product) {
    Product saved = productRepository.save(product);
    String key = "product:" + saved.getId();
    redisTemplate.opsForValue().set(key, saved, Duration.ofMinutes(30));
    return saved;
}

// 3. Write-Behind (Async)
@Async
public void updateProductAsync(Product product) {
    String key = "product:" + product.getId();
    redisTemplate.opsForValue().set(key, product);
    // Salvar no banco posteriormente (batch)
}

// 4. Refresh-Ahead
@Scheduled(fixedRate = 300000) // 5 minutos
public void refreshPopularProducts() {
    List<Product> popular = productRepository.findTopSelling(100);
    popular.forEach(p -> {
        String key = "product:" + p.getId();
        redisTemplate.opsForValue().set(key, p, Duration.ofMinutes(30));
    });
}
```

#### 5. Performance e Otimização (30min)

**Métricas e monitoramento**
```java
@Component
public class DatabaseMetrics {
    
    private final MeterRegistry meterRegistry;
    
    @Around("execution(* com.example.repository.*.*(..))")
    public Object measureRepositoryCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Object result = joinPoint.proceed();
            sample.stop(Timer.builder("database.query")
                .tag("repository", joinPoint.getTarget().getClass().getSimpleName())
                .tag("method", joinPoint.getSignature().getName())
                .tag("status", "success")
                .register(meterRegistry));
            return result;
        } catch (Exception e) {
            sample.stop(Timer.builder("database.query")
                .tag("status", "error")
                .register(meterRegistry));
            throw e;
        }
    }
}
```

## 💻 Exercícios Práticos

### Exercício 1: Otimização de Queries (1h)
Identifique e corrija problemas de performance:
- Problema N+1
- Queries sem índices
- Falta de paginação
- Fetch desnecessário

### Exercício 2: MongoDB (1h)
Crie um catálogo de produtos no MongoDB:
- Modelo de documento
- Queries complexas
- Aggregations
- Text search

### Exercício 3: Redis Cache (1h)
Implemente cache em múltiplas camadas:
- Cache de produtos mais visitados
- Cache de categorias
- Cache de sessão de usuário
- Invalidação inteligente

## 📚 Material de Estudo

### Leitura Obrigatória
- [JPA Performance](https://vladmihalcea.com/tutorials/hibernate/)
- [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [Spring Data Redis](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)

### Leitura Complementar
- [High-Performance Java Persistence](https://vladmihalcea.com/books/high-performance-java-persistence/)
- [Redis in Action](https://www.manning.com/books/redis-in-action)

## 🎯 Objetivos de Aprendizagem

Ao final deste dia, você deve ser capaz de:

- ✅ Otimizar queries JPA
- ✅ Trabalhar com MongoDB
- ✅ Implementar cache com Redis
- ✅ Aplicar estratégias de cache apropriadas
- ✅ Monitorar performance de banco de dados

## 🏠 Tarefa de Casa

1. **Otimizar aplicação existente**
2. **Estudar**: Sharding, Replication
3. **Preparação Dia 8**: Git workflows, Docker basics

## 📝 Notas do Instrutor

```
Demonstrar:
- Hibernate statistics
- Query plans (EXPLAIN)
- Cache hit ratio
- Comparação SQL vs NoSQL
```

## 🔗 Links Úteis

- [Hibernate](https://hibernate.org/)
- [MongoDB University](https://university.mongodb.com/)
- [Redis University](https://university.redis.com/)
