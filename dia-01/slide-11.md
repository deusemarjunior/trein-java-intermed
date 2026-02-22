# Slide 11: Modelo, DAO e DTOs

**Horário:** 13:50 - 14:15

---

## Passo 1: Modelo (Product.java)

```java
package com.example.products.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor vazio
    public Product() {}

    // Construtor para criação (sem id)
    public Product(String name, String description, 
                   BigDecimal price, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

**Observe:** Classe Java pura, sem `@Entity`, sem `@Table`, sem JPA!

---

## Passo 2: DTOs com Records

```java
// CreateProductRequest.java
package com.example.products.dto;

import java.math.BigDecimal;

public record CreateProductRequest(
    String name,
    String description,
    BigDecimal price,
    String category
) {
    // Validação manual no construtor
    public CreateProductRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
    }
}
```

```java
// ProductResponse.java
package com.example.products.dto;

import com.example.products.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String category,
    LocalDateTime createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory(),
            product.getCreatedAt()
        );
    }
}
```

---

## Passo 3: DAO - Data Access Object (JDBC)

```java
package com.example.products.dao;

import com.example.products.config.DatabaseConfig;
import com.example.products.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {

    // ═══════════════════════════════════════
    //  CREATE
    // ═══════════════════════════════════════
    public Product save(Product product) {
        String sql = """
            INSERT INTO products (name, description, price, category, created_at, updated_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, 
                 Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setString(4, product.getCategory());
            ps.executeUpdate();

            // Recuperar ID gerado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getLong(1));
                }
            }
            return findById(product.getId()).orElse(product);
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving product", e);
        }
    }

    // ═══════════════════════════════════════
    //  READ
    // ═══════════════════════════════════════
    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY id";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products", e);
        }
        return products;
    }

    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product", e);
        }
        return Optional.empty();
    }

    // ═══════════════════════════════════════
    //  UPDATE
    // ═══════════════════════════════════════
    public Optional<Product> update(Long id, Product product) {
        String sql = """
            UPDATE products 
            SET name = ?, description = ?, price = ?, category = ?, 
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setString(4, product.getCategory());
            ps.setLong(5, id);
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                return findById(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product", e);
        }
        return Optional.empty();
    }

    // ═══════════════════════════════════════
    //  DELETE
    // ═══════════════════════════════════════
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product", e);
        }
    }

    // ═══════════════════════════════════════
    //  HELPER: Mapear ResultSet → Product
    // ═══════════════════════════════════════
    private Product mapRow(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setCategory(rs.getString("category"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return product;
    }
}
```

### 🔑 Conceitos importantes

| Conceito | Descrição |
|----------|-----------|
| `Connection` | Conexão com o banco |
| `PreparedStatement` | SQL com `?` (previne SQL Injection!) |
| `ResultSet` | Cursor sobre os resultados |
| `try-with-resources` | Fecha conexões automaticamente |
| `mapRow()` | Converte linha do DB → Objeto Java |

