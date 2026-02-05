package com.example.patterns.strategy;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate(BigDecimal price);
    String getDescription();
}
