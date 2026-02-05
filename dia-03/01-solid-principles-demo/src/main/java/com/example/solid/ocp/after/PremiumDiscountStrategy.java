package com.example.solid.ocp.after;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * ✅ Estratégia concreta: Desconto Premium
 */
@Component("PREMIUM")
public class PremiumDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.30)); // 30% desconto
    }
    
    @Override
    public String getDescription() {
        return "Premium - 30% de desconto";
    }
}
