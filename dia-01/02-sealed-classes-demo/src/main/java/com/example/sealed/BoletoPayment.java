package com.example.sealed;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Classe final para pagamento via Boleto
 */
public final class BoletoPayment extends Payment {
    
    private final String barcode;
    private final LocalDate dueDate;
    
    public BoletoPayment(BigDecimal amount, String description, 
                        String barcode, LocalDate dueDate) {
        super(amount, description);
        this.barcode = barcode;
        this.dueDate = dueDate;
    }
    
    @Override
    public void process() {
        System.out.println("📄 Processing boleto payment...");
        System.out.println("   Amount: R$ " + amount);
        System.out.println("   Barcode: " + barcode);
        System.out.println("   Due date: " + dueDate);
        System.out.println("   Bank fee: R$ " + getBankFee());
    }
    
    public BigDecimal getBankFee() {
        // Taxa fixa de R$ 3.50 para boleto
        return BigDecimal.valueOf(3.50);
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
}
