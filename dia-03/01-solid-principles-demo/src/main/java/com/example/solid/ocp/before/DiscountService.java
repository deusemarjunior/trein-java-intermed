package com.example.solid.ocp.before;

import java.math.BigDecimal;

/**
 * ❌ VIOLAÇÃO DO OCP (Open/Closed Principle)
 * 
 * Problema: Para adicionar um novo tipo de desconto,
 * precisamos MODIFICAR esta classe (adicionar mais um if/else).
 * 
 * Isso viola o princípio: deve estar ABERTO para extensão,
 * mas FECHADO para modificação.
 */
public class DiscountService {
    
    public BigDecimal calculateDiscount(BigDecimal price, String customerType) {
        BigDecimal discount = BigDecimal.ZERO;
        
        if ("VIP".equals(customerType)) {
            discount = price.multiply(BigDecimal.valueOf(0.20)); // 20% desconto
        } else if ("REGULAR".equals(customerType)) {
            discount = price.multiply(BigDecimal.valueOf(0.10)); // 10% desconto
        } else if ("PREMIUM".equals(customerType)) {
            discount = price.multiply(BigDecimal.valueOf(0.30)); // 30% desconto
        } else if ("BLACK_FRIDAY".equals(customerType)) {
            discount = price.multiply(BigDecimal.valueOf(0.50)); // 50% desconto
        }
        // ⚠️ Para adicionar GOLD, PLATINUM, etc.,
        // precisamos MODIFICAR este código!
        
        return discount;
    }
    
    public BigDecimal applyDiscount(BigDecimal price, String customerType) {
        BigDecimal discount = calculateDiscount(price, customerType);
        return price.subtract(discount);
    }
}
