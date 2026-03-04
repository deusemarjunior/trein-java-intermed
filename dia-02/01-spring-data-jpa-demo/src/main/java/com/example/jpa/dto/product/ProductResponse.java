package com.example.jpa.dto.product;

import com.example.jpa.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    Boolean active,
    String imageUrl,
    String categoryName,
    Long categoryId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getActive(),
            product.getImageUrl(),
            product.getCategory() != null ? product.getCategory().getName() : null,
            product.getCategory() != null ? product.getCategory().getId() : null,
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}
