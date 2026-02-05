package com.example.solid.ocp.after;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * ✅ Estratégia concreta: Desconto VIP
 */
@Component("VIP")
public class VipDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.20)); // 20% desconto
    }
    
    @Override
    public String getDescription() {
        return "VIP - 20% de desconto";
    }
}
