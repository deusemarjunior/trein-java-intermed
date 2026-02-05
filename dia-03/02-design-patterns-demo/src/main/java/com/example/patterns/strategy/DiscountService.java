package com.example.patterns.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final ApplicationContext context;
    
    public BigDecimal calculateDiscount(BigDecimal price, String customerType) {
        try {
            DiscountStrategy strategy = context.getBean(customerType, DiscountStrategy.class);
            return strategy.calculate(price);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
    
    public BigDecimal applyDiscount(BigDecimal price, String customerType) {
        BigDecimal discount = calculateDiscount(price, customerType);
        return price.subtract(discount);
    }
    
    public String getDiscountDescription(String customerType) {
        try {
            DiscountStrategy strategy = context.getBean(customerType, DiscountStrategy.class);
            return strategy.getDescription();
        } catch (Exception e) {
            return "Sem desconto";
        }
    }
}
