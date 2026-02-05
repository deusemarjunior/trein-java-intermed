package com.example.solid.dip.after;

import lombok.Data;

/**
 * ✅ Modelo de domínio: Order
 */
@Data
public class Order {
    private Long id;
    private String description;
    private Double total;
}
