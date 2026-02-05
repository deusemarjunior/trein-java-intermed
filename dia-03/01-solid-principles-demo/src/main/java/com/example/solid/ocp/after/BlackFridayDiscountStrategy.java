package com.example.solid.ocp.after;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * ✅ Estratégia concreta: Desconto Black Friday
 */
@Component("BLACK_FRIDAY")
public class BlackFridayDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.50)); // 50% desconto
    }
    
    @Override
    public String getDescription() {
        return "Black Friday - 50% de desconto";
    }
}
