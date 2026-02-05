package com.example.ddd.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.math.BigDecimal;

/**
 * VALUE OBJECT: Money
 * - Imutável
 * - Sem identidade
 * - Comparado por valor
 */
@Getter
@EqualsAndHashCode
public class Money {
    private final BigDecimal amount;
    private final String currency;
    
    public Money(String amount, String currency) {
        this.amount = new BigDecimal(amount);
        this.currency = currency;
    }
    
    public Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
    
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }
    
    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Moedas diferentes");
        }
    }
    
    @Override
    public String toString() {
        return currency + " " + amount;
    }
}
