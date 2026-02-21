package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Dados para criação/atualização de produto")
public record ProductRequest(
        @Schema(description = "Nome do produto", example = "Notebook Dell")
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @Schema(description = "SKU único do produto", example = "NOT-0001")
        @NotBlank(message = "SKU é obrigatório")
        String sku,

        @Schema(description = "Preço do produto", example = "4500.00")
        @Positive(message = "Preço deve ser positivo")
        BigDecimal price,

        @Schema(description = "Descrição do produto", example = "Notebook Dell Inspiron 15")
        String description
) {}
