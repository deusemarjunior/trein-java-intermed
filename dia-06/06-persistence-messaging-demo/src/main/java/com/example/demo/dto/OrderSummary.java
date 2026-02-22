package com.example.demo.dto;

import java.math.BigDecimal;

/**
 * Projeção DTO usando Java Record.
 * Evita o problema N+1 buscando apenas os campos necessários
 * diretamente na query JPQL com SELECT NEW.
 */
public record OrderSummary(
        Long id,
        String customerName,
        String categoryName,
        BigDecimal total,
        String status
) {}
