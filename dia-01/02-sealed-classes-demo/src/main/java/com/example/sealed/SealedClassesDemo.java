package com.example.sealed;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Demonstração de Sealed Classes e Pattern Matching
 */
public class SealedClassesDemo {
    
    public static void main(String[] args) {
        System.out.println("=== DEMONSTRAÇÃO DE SEALED CLASSES ===\n");
        
        // Criar diferentes tipos de pagamento
        Payment creditCard = new CreditCardPayment(
            BigDecimal.valueOf(1500), 
            "Compra Laptop",
            "1234567890123456",
            3
        );
        
        Payment pix = new PixPayment(
            BigDecimal.valueOf(250),
            "Pagamento Freelance",
            "email@example.com"
        );
        
        Payment boleto = new BoletoPayment(
            BigDecimal.valueOf(800),
            "Mensalidade Curso",
            "23793.38128 60007.827136 95000.063308 8 95160000080000",
            LocalDate.now().plusDays(3)
        );
        
        // Processar pagamentos
        System.out.println("1. Processando pagamentos:\n");
        creditCard.process();
        System.out.println();
        pix.process();
        System.out.println();
        boleto.process();
        System.out.println();
        
        // Pattern matching com instanceof (Java 16+)
        System.out.println("2. Pattern Matching com instanceof:\n");
        printPaymentDetails(creditCard);
        printPaymentDetails(pix);
        printPaymentDetails(boleto);
        System.out.println();
        
        // Calcular taxa total
        System.out.println("3. Calculando taxas:\n");
        Payment[] payments = {creditCard, pix, boleto};
        for (Payment payment : payments) {
            String fee = getProcessingFee(payment);
            System.out.println("   " + payment.getDescription() + ": " + fee);
        }
        
        System.out.println("\n✅ Demonstração completa!");
        System.out.println("\nVantagens de Sealed Classes:");
        System.out.println("   • Hierarquia controlada - segurança");
        System.out.println("   • Compilador força tratamento de todos os casos");
        System.out.println("   • Pattern matching mais poderoso");
        System.out.println("   • Ideal para modelar estados/tipos finitos");
    }
    
    /**
     * Pattern matching for instanceof (Java 16+)
     */
    private static void printPaymentDetails(Payment payment) {
        if (payment instanceof CreditCardPayment cc) {
            System.out.println("💳 Cartão: " + cc.getCardNumber() + 
                             " (" + cc.getInstallments() + "x)");
        } else if (payment instanceof PixPayment pix) {
            System.out.println("📱 PIX: " + pix.getPixKey());
        } else if (payment instanceof BoletoPayment boleto) {
            System.out.println("📄 Boleto vence em: " + boleto.getDueDate());
        }
    }
    
    /**
     * Pattern matching simplificado
     */
    private static String getProcessingFee(Payment payment) {
        if (payment instanceof CreditCardPayment cc) {
            return "Fee: R$ " + cc.calculateFee();
        } else if (payment instanceof PixPayment) {
            return "Fee: R$ 0.00 (PIX é grátis!)";
        } else if (payment instanceof BoletoPayment boleto) {
            return "Fee: R$ " + boleto.getBankFee();
        }
        return "Unknown payment type";
    }
}
