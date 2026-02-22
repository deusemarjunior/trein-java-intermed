package com.example.products.dto;

import java.math.BigDecimal;

/**
 * DTO para criação de produto.
 * 
 * Usa Record do Java 21 com validação manual no construtor compacto.
 * Em Spring Boot, usaríamos @NotBlank, @NotNull, @DecimalMin, etc.
 */
public record CreateProductRequest(
        String name,
        String description,
        BigDecimal price,
        String category
) {
    // Construtor compacto com validação
    public CreateProductRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (name.length() < 3 || name.length() > 100) {
            throw new IllegalArgumentException("Name must be between 3 and 100 characters");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Description must be less than 500 characters");
        }
        if (category != null && category.length() > 50) {
            throw new IllegalArgumentException("Category must be less than 50 characters");
        }
    }
}
