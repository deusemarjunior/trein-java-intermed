package com.example.jpa.dto.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    String name,
    
    @Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    String description,
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Preço inválido")
    BigDecimal price,
    
    @NotNull(message = "Estoque é obrigatório")
    @Min(value = 0, message = "Estoque não pode ser negativo")
    Integer stock,
    
    @NotNull(message = "Categoria é obrigatória")
    Long categoryId,
    
    @Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    String imageUrl
) {}
