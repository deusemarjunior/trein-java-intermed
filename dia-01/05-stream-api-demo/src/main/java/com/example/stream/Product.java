package com.example.stream;

import java.math.BigDecimal;

/**
 * Record para representar um produto
 */
public record Product(Long id, String name, BigDecimal price, String category) {
    
    public boolean isExpensive() {
        return price.compareTo(BigDecimal.valueOf(1000)) > 0;
    }
}
