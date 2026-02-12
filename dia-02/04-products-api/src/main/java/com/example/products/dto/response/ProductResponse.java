package com.example.products.dto.response;

import com.example.products.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resposta usando Record
 * Converte entidade em formato de API
 */
public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String category,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /**
     * Factory method para converter Product -> ProductResponse
     */
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}
