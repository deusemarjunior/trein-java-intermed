package com.example.solid.ocp.after;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

/**
 * ✅ APLICANDO OCP
 * 
 * Agora este serviço está FECHADO para modificação:
 * - Não precisa mudar para adicionar novos tipos de desconto
 * 
 * E ABERTO para extensão:
 * - Basta criar uma nova classe que implemente DiscountStrategy
 * - Registrar com @Component e o nome apropriado
 */
@Service
@RequiredArgsConstructor
public class DiscountService {
    
    private final ApplicationContext context;
    
    public BigDecimal calculateDiscount(BigDecimal price, String customerType) {
        try {
            // Obtém a estratégia do contexto do Spring
            DiscountStrategy strategy = context.getBean(customerType, DiscountStrategy.class);
            return strategy.calculate(price);
        } catch (Exception e) {
            // Se não encontrar a estratégia, retorna zero
            System.out.println("Estratégia não encontrada para: " + customerType);
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
