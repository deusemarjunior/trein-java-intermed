package com.example.cleanarchitecture.adapter.in.web.dto;

import com.example.cleanarchitecture.validation.ValidSku;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO de entrada — o que o cliente envia na requisição.
 * Validações com Bean Validation + Custom Validator.
 */
public record ProductRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,

        @NotBlank(message = "SKU is required")
        @ValidSku
        String sku,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {
}
