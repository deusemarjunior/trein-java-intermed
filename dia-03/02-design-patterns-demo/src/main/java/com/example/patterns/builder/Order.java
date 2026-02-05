package com.example.patterns.builder;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class Order {
    private Long id;
    private String description;
    private BigDecimal total;
    private String customerName;
    private String status;
}
