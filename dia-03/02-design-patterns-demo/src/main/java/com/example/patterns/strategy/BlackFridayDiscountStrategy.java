package com.example.patterns.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("BLACK_FRIDAY")
public class BlackFridayDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.50));
    }
    
    @Override
    public String getDescription() {
        return "Black Friday - 50% desconto";
    }
}
