package com.example.patterns.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("VIP")
public class VipDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.20));
    }
    
    @Override
    public String getDescription() {
        return "VIP - 20% desconto";
    }
}
