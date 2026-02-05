package com.example.solid.ocp.after;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * ✅ Estratégia concreta: Desconto Regular
 */
@Component("REGULAR")
public class RegularDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.10)); // 10% desconto
    }
    
    @Override
    public String getDescription() {
        return "Regular - 10% de desconto";
    }
}
