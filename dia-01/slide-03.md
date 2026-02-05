# Slide 3: Records - A Revolução dos DTOs

**Horário:** 09:20 - 09:50

---

## Problema Tradicional

```java
// ❌ ANTES (Java 8-13): ~50 linhas para um DTO simples!
public class Product {
    private final Long id;
    private final String name;
    private final BigDecimal price;
    
    public Product(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    
    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    
    @Override
    public boolean equals(Object o) {
        // ... 10+ linhas
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }
    
    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + 
               "', price=" + price + '}';
    }
}
```

---

## ✨ Solução com Records (Java 14+)

```java
// ✅ AGORA (Java 17+): 1 linha!
public record Product(Long id, String name, BigDecimal price) {}

// Grátis: constructor, getters, equals, hashCode, toString
```

---

## 🎬 DEMO AO VIVO

```java
package com.example.demo;

import java.math.BigDecimal;

public record Product(Long id, String name, BigDecimal price) {
    
    // ✅ Compact constructor - validação
    public Product {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
    }
    
    // ✅ Métodos customizados
    public boolean isExpensive() {
        return price.compareTo(BigDecimal.valueOf(1000)) > 0;
    }
    
    public Product applyDiscount(BigDecimal percentage) {
        BigDecimal factor = BigDecimal.ONE.subtract(percentage);
        return new Product(id, name, price.multiply(factor));
    }
    
    // ✅ Métodos estáticos factory
    public static Product create(String name, BigDecimal price) {
        return new Product(null, name, price);
    }
}
```

---

## Teste Rápido

```java
class Main {
    public static void main(String[] args) {
        // Criar produto
        Product laptop = new Product(1L, "Laptop", BigDecimal.valueOf(3500));
        System.out.println(laptop);
        // Output: Product[id=1, name=Laptop, price=3500]
        
        // Usar métodos
        System.out.println("Is expensive? " + laptop.isExpensive());
        // Output: Is expensive? true
        
        // Aplicar desconto (imutabilidade!)
        Product discounted = laptop.applyDiscount(BigDecimal.valueOf(0.1));
        System.out.println("Original: " + laptop.price());
        System.out.println("Discounted: " + discounted.price());
        // Output: Original: 3500
        // Output: Discounted: 3150
        
        // Equals e hashCode funcionam!
        Product laptop2 = new Product(1L, "Laptop", BigDecimal.valueOf(3500));
        System.out.println("Equals? " + laptop.equals(laptop2));
        // Output: Equals? true
    }
}
```

---

## 📝 Pontos-chave

1. Records são **imutáveis** por padrão
2. Getters não têm prefixo `get` → `product.name()` não `product.getName()`
3. Compact constructor é executado ANTES da atribuição
4. Ideal para DTOs, Value Objects, Responses

---

## 🤔 Perguntas para discussão

- Por que imutabilidade é importante?
- Quando NÃO usar Records? (Entidades JPA!)
