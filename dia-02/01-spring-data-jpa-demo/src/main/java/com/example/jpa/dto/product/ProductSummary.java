package com.example.jpa.dto.product;

import java.math.BigDecimal;

// Projeção - retornar apenas alguns campos
public record ProductSummary(
    Long id,
    String name,
    BigDecimal price
) {}
