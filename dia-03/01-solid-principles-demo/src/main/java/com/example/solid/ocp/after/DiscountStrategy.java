package com.example.solid.ocp.after;

import java.math.BigDecimal;

/**
 * ✅ APLICANDO OCP
 * Interface para estratégias de desconto
 */
public interface DiscountStrategy {
    BigDecimal calculate(BigDecimal price);
    String getDescription();
}
