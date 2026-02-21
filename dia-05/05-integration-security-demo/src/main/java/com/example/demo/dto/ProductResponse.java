package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de resposta do produto")
public record ProductResponse(
        @Schema(description = "ID do produto", example = "1")
        Long id,

        @Schema(description = "Nome do produto", example = "Notebook Dell")
        String name,

        @Schema(description = "SKU do produto", example = "NOT-0001")
        String sku,

        @Schema(description = "Preço do produto", example = "4500.00")
        BigDecimal price,

        @Schema(description = "Descrição do produto", example = "Notebook Dell Inspiron 15")
        String description,

        @Schema(description = "Data de criação")
        LocalDateTime createdAt
) {}
