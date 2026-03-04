package com.example.testingdemo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        Integer quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
