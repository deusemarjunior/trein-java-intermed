package com.example.sealed;

import java.math.BigDecimal;

/**
 * Sealed class - Hierarquia controlada
 * Apenas as classes listadas em 'permits' podem estender Payment
 */
public abstract sealed class Payment 
    permits CreditCardPayment, PixPayment, BoletoPayment {
    
    protected final BigDecimal amount;
    protected final String description;
    
    protected Payment(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }
    
    public abstract void process();
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
}
