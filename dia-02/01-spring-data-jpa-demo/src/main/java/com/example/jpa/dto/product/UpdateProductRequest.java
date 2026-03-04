package com.example.jpa.dto.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateProductRequest(
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    String name,
    
    @Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    String description,
    
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Preço inválido")
    BigDecimal price,
    
    @Min(value = 0, message = "Estoque não pode ser negativo")
    Integer stock,
    
    Long categoryId,
    
    @Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    String imageUrl,
    
    Boolean active
) {}
