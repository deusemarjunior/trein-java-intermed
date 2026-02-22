package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para criação de pedidos.
 */
public record OrderRequest(
        @NotBlank(message = "Nome do cliente é obrigatório")
        String customerName,

        @NotBlank(message = "Email do cliente é obrigatório")
        @Email(message = "Email inválido")
        String customerEmail,

        @NotNull(message = "Categoria é obrigatória")
        Long categoryId,

        @NotEmpty(message = "Pedido deve ter pelo menos um item")
        @Valid
        List<ItemRequest> items
) {
    public record ItemRequest(
            @NotBlank(message = "Nome do produto é obrigatório")
            String productName,

            @NotNull @Min(1)
            Integer quantity,

            @NotNull @DecimalMin("0.01")
            BigDecimal unitPrice
    ) {}
}
