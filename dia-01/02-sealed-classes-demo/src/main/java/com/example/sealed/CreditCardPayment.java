package com.example.sealed;

import java.math.BigDecimal;

/**
 * Classe final que estende Payment
 */
public final class CreditCardPayment extends Payment {
    
    private final String cardNumber;
    private final int installments;
    
    public CreditCardPayment(BigDecimal amount, String description, 
                            String cardNumber, int installments) {
        super(amount, description);
        this.cardNumber = maskCardNumber(cardNumber);
        this.installments = installments;
    }
    
    @Override
    public void process() {
        System.out.println("💳 Processing credit card payment...");
        System.out.println("   Amount: R$ " + amount);
        System.out.println("   Card: " + cardNumber);
        System.out.println("   Installments: " + installments + "x");
        System.out.println("   Fee: R$ " + calculateFee());
    }
    
    public BigDecimal calculateFee() {
        // Taxa de 2% para cartão
        return amount.multiply(BigDecimal.valueOf(0.02));
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public int getInstallments() {
        return installments;
    }
}
