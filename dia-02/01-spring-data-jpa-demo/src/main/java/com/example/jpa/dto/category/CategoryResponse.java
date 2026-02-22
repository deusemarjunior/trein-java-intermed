package com.example.jpa.dto.category;

import com.example.jpa.model.Category;
import java.time.LocalDateTime;

public record CategoryResponse(
    Long id,
    String name,
    String description,
    Boolean active,
    Integer productCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getActive(),
            category.getProducts() != null ? category.getProducts().size() : 0,
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
}
