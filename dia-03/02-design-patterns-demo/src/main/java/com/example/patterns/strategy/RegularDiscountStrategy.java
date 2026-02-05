package com.example.patterns.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("REGULAR")
public class RegularDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.10));
    }
    
    @Override
    public String getDescription() {
        return "Regular - 10% desconto";
    }
}
