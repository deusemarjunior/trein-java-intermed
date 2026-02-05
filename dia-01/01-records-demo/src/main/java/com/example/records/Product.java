package com.example.records;

import java.math.BigDecimal;

/**
 * Record moderno do Java 17+ - Substitui DTOs tradicionais
 * Automaticamente gera: constructor, getters, equals, hashCode, toString
 */
public record Product(Long id, String name, BigDecimal price) {
    
    /**
     * Compact constructor - Validação automática
     * Executado antes da atribuição dos campos
     */
    public Product {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
    }
    
    /**
     * Métodos customizados podem ser adicionados
     */
    public boolean isExpensive() {
        return price.compareTo(BigDecimal.valueOf(1000)) > 0;
    }
    
    /**
     * Criando nova instância com desconto (Records são imutáveis!)
     */
    public Product applyDiscount(BigDecimal percentage) {
        BigDecimal factor = BigDecimal.ONE.subtract(percentage);
        return new Product(id, name, price.multiply(factor));
    }
    
    /**
     * Factory method estático
     */
    public static Product create(String name, BigDecimal price) {
        return new Product(null, name, price);
    }
}
