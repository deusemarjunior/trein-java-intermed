# Slide 13: Fundamentos de Persistência & JPA

**Horário:** 10:30 - 10:50

---

## 💾 O que é Persistência?

### Problema: Dados em Memória são Voláteis

```mermaid
flowchart LR
    A[Aplicação<br/>inicia] --> B[Dados em<br/>memória RAM]
    B --> C[Aplicação<br/>processa]
    C --> D[Aplicação<br/>encerra]
    D --> E[💥 Dados<br/>perdidos!]
    
    style E fill:#FF6B6B
```

### Solução: Persistir em Banco de Dados

```mermaid
flowchart LR
    A[Aplicação] --> B[(Banco de<br/>Dados)]
    B --> C[Dados<br/>permanentes]
    
    A -.->|1. Salva| B
    B -.->|2. Persiste<br/>em disco| C
    A -.->|3. Pode ler<br/>a qualquer momento| B
    
    style C fill:#90EE90
```

---

## 🗃️ Modelo Relacional vs Orientação a Objetos

### Impedância Objeto-Relacional

```mermaid
flowchart TD
    subgraph JAVA["MUNDO JAVA — Orientação a Objetos"]
        direction TB
        J1["class Product"]
        J2["Long id"]
        J3["String name"]
        J4["BigDecimal price"]
        J5["Category category ← Objeto relacionado"]
        J6["List&lt;Review&gt; reviews ← Coleção"]
        J1 --- J2 & J3 & J4 & J5 & J6
    end

    JAVA <-->|"ORM (JPA / Hibernate)"| SQL

    subgraph SQL["MUNDO SQL — Modelo Relacional"]
        direction TB
        S1["TABLE products"]
        S2["id BIGSERIAL PRIMARY KEY"]
        S3["name VARCHAR(100)"]
        S4["price DECIMAL(10,2)"]
        S5["category_id BIGINT ← Foreign Key"]
        S1 --- S2 & S3 & S4 & S5
        S6["TABLE reviews (...)"]
    end

    style JAVA fill:#E8F4FD,stroke:#2196F3
    style SQL fill:#FFF3E0,stroke:#FF9800
```

---

## 🔧 ORM: Object-Relational Mapping

### O que é JPA?

```mermaid
flowchart TD
    A["JPA<br/>(Jakarta Persistence API)"] --> B["Especificação (interface)"]
    A --> C["Implementações"]

    B --> B1["Define anotações<br/>@Entity, @Id, etc"]
    B --> B2["Define EntityManager<br/>(API)"]
    B --> B3["Define comportamentos padrão"]

    C --> C1["Hibernate ✅<br/>(mais popular)"]
    C --> C2["EclipseLink"]
    C --> C3["OpenJPA"]

    style A fill:#FFD700
    style C1 fill:#90EE90
```

**Spring Data JPA** = JPA + Repositories + Convenções Spring

---

## 📦 Arquitetura JPA/Hibernate

```mermaid
flowchart TB
    A[Aplicação Java] --> B[Spring Data JPA]
    B --> C[JPA API]
    C --> D[Hibernate<br/>implementação]
    D --> E[JDBC]
    E --> F[(Database)]
    
    B -.-> G[Repositories<br/>Automáticos]
    C -.-> H[Entity Manager]
    D -.-> I[Session<br/>1st Level Cache<br/>Dirty Checking]
    E -.-> J[Connection Pool<br/>HikariCP]
    
    style B fill:#87CEEB
    style D fill:#90EE90
    style F fill:#DDA0DD
```

---

## ⚙️ Configuração: application.yml

```yaml
spring:
  # Datasource - conexão com banco
  datasource:
    url: jdbc:postgresql://localhost:5432/java_training
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
    
    # Connection Pool (HikariCP)
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
  
  # JPA Configuration
  jpa:
    # Hibernate specific
    hibernate:
      ddl-auto: update  # create, update, validate, none
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    
    # Mostrar SQL no console
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
    
    # Dialeto do banco
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

---

## 🎯 hibernate.ddl-auto: Quando Usar?

| Valor | Comportamento | Quando usar |
|-------|---------------|-------------|
| `none` | Não faz nada | Produção (sempre!) |
| `validate` | Valida schema contra entities | Produção, CI/CD |
| `update` | Adiciona colunas/tabelas faltantes | Dev (com cuidado!) |
| `create` | DROP + CREATE tudo ao iniciar | Testes automatizados |
| `create-drop` | DROP ao encerrar aplicação | Testes, demos |

**⚠️ ATENÇÃO:**
```java
// ❌ NUNCA em produção:
spring.jpa.hibernate.ddl-auto=create  // APAGA TUDO! 💥

// ✅ Em produção:
spring.jpa.hibernate.ddl-auto=none  ou validate
// E use migrations (Flyway/Liquibase)
```

---

## 🎬 DEMO: Primeira Entity

```java
// src/main/java/com/example/model/Product.java
package com.example.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity  // ← Marca como entidade JPA
@Table(name = "products")  // ← Nome da tabela (opcional)
public class Product {
    
    @Id  // ← Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← Auto-increment
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(length = 50)
    private String category;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Construtores
    public Product() {}  // ← JPA precisa de construtor vazio!
    
    public Product(String name, BigDecimal price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }
    
    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters e Setters (JPA precisa!)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    // ... demais getters/setters
}
```

---

## 🔍 Anotações JPA Essenciais

### Entity & Table

| Anotação | Descrição | Exemplo |
|----------|-----------|---------|
| `@Entity` | Marca classe como entidade | `@Entity class Product` |
| `@Table` | Customiza nome da tabela | `@Table(name="products")` |
| `@Id` | Define primary key | `@Id private Long id` |
| `@GeneratedValue` | Auto-increment | `@GeneratedValue(strategy=IDENTITY)` |

### Column Mapping

| Anotação | Descrição | Exemplo |
|----------|-----------|---------|
| `@Column` | Customiza coluna | `@Column(name="product_name")` |
| `nullable` | NOT NULL | `@Column(nullable=false)` |
| `unique` | UNIQUE constraint | `@Column(unique=true)` |
| `length` | VARCHAR tamanho | `@Column(length=100)` |
| `precision/scale` | DECIMAL | `@Column(precision=10, scale=2)` |
| `columnDefinition` | SQL customizado | `@Column(columnDefinition="TEXT")` |

---

## 🕐 Lifecycle Callbacks

```java
@Entity
public class Product {
    
    @PrePersist  // Antes de INSERT
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        log.info("Creating new product: {}", name);
    }
    
    @PostPersist  // Depois de INSERT
    protected void afterCreate() {
        log.info("Product created with ID: {}", id);
    }
    
    @PreUpdate  // Antes de UPDATE
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        log.info("Updating product: {}", id);
    }
    
    @PostUpdate  // Depois de UPDATE
    protected void afterUpdate() {
        log.info("Product updated: {}", id);
    }
    
    @PreRemove  // Antes de DELETE
    protected void onDelete() {
        log.info("Deleting product: {}", id);
    }
    
    @PostRemove  // Depois de DELETE
    protected void afterDelete() {
        log.info("Product deleted: {}", id);
    }
    
    @PostLoad  // Depois de SELECT
    protected void afterLoad() {
        log.debug("Product loaded: {}", id);
    }
}
```

---

## 🔄 GeneratedValue Strategies

```java
// 1. IDENTITY - Auto-increment do banco (PostgreSQL SERIAL, MySQL AUTO_INCREMENT)
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
// SQL: id BIGSERIAL PRIMARY KEY

// 2. SEQUENCE - Sequence do banco (PostgreSQL, Oracle)
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
@SequenceGenerator(name = "product_seq", sequenceName = "product_sequence", allocationSize = 1)
private Long id;
// SQL: CREATE SEQUENCE product_sequence

// 3. TABLE - Tabela separada para IDs (qualquer banco)
@Id
@GeneratedValue(strategy = GenerationType.TABLE, generator = "product_gen")
@TableGenerator(name = "product_gen", table = "id_generator")
private Long id;
// SQL: CREATE TABLE id_generator (...)

// 4. AUTO - Hibernate decide (não recomendado)
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
```

**Recomendação:** Use `IDENTITY` para PostgreSQL/MySQL moderno.
