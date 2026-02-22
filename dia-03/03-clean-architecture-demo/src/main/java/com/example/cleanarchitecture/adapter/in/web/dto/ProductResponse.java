package com.example.cleanarchitecture.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de saída — o que o cliente recebe na resposta.
 * Nunca expõe campos internos da entidade (ex: updatedAt interno).
 */
public record ProductResponse(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        String description,
        LocalDateTime createdAt
) {
}
