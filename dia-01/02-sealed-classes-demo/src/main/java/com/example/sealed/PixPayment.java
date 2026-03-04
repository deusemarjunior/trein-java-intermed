package com.example.sealed;

import java.math.BigDecimal;

/**
 * Classe final para pagamento via PIX
 */
public final class PixPayment extends Payment {
    
    private final String pixKey;
    
    public PixPayment(BigDecimal amount, String description, String pixKey) {
        super(amount, description);
        this.pixKey = pixKey;
    }
    
    @Override
    public void process() {
        System.out.println("📱 Processing PIX payment...");
        System.out.println("   Amount: R$ " + amount);
        System.out.println("   PIX Key: " + pixKey);
        System.out.println("   Fee: R$ 0.00 (PIX é grátis!)");
    }
    
    public String getPixKey() {
        return pixKey;
    }
}
