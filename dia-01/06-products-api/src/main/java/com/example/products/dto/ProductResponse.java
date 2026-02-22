package com.example.products.dto;

import com.example.products.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resposta para produto.
 * 
 * Usa Record do Java 21 - imutável e conciso.
 * Factory method from() converte Product → ProductResponse.
 */
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        LocalDateTime createdAt
) {
    /**
     * Converte uma entidade Product para ProductResponse.
     */
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
