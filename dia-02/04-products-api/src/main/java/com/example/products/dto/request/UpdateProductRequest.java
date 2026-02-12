package com.example.products.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO para atualização de produto
 */
public record UpdateProductRequest(
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    String description,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    BigDecimal price,
    
    @Size(max = 50, message = "Category must be less than 50 characters")
    String category
    
) {}
