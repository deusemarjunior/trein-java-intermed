package com.example.patterns.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("PREMIUM")
public class PremiumDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.30));
    }
    
    @Override
    public String getDescription() {
        return "Premium - 30% desconto";
    }
}
