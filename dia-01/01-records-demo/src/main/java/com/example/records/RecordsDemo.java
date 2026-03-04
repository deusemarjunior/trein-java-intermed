package com.example.records;

import java.math.BigDecimal;

/**
 * Demonstração prática de Java Records
 */
public class RecordsDemo {
    
    public static void main(String[] args) {
        System.out.println("=== DEMONSTRAÇÃO DE RECORDS ===\n");
        
        // 1. Criar produto
        Product laptop = new Product(1L, "Laptop Gaming", BigDecimal.valueOf(3500));
        System.out.println("1. Produto criado:");
        System.out.println("   " + laptop);
        System.out.println();
        
        // 2. Usar métodos gerados automaticamente
        System.out.println("2. Acessando campos (getters automáticos):");
        System.out.println("   ID: " + laptop.id());
        System.out.println("   Nome: " + laptop.name());
        System.out.println("   Preço: R$ " + laptop.price());
        System.out.println();
        
        // 3. Comparação automática (equals/hashCode)
        Product laptop2 = new Product(1L, "Laptop Gaming", BigDecimal.valueOf(3500));
        System.out.println("3. Comparação (equals automático):");
        System.out.println("   laptop.equals(laptop2): " + laptop.equals(laptop2));
        System.out.println();
        
        // 4. Método customizado
        System.out.println("4. Método customizado isExpensive():");
        System.out.println("   Laptop é caro? " + laptop.isExpensive());
        Product mouse = new Product(2L, "Mouse", BigDecimal.valueOf(50));
        System.out.println("   Mouse é caro? " + mouse.isExpensive());
        System.out.println();
        
        // 5. Imutabilidade - criar nova instância com desconto
        System.out.println("5. Aplicar desconto (cria novo objeto - imutável):");
        BigDecimal discount = BigDecimal.valueOf(0.10); // 10%
        Product laptopComDesconto = laptop.applyDiscount(discount);
        System.out.println("   Original: " + laptop);
        System.out.println("   Com 10% desconto: " + laptopComDesconto);
        System.out.println();
        
        // 6. Factory method
        System.out.println("6. Factory method:");
        Product novoProduto = Product.create("Monitor", BigDecimal.valueOf(1200));
        System.out.println("   " + novoProduto);
        System.out.println();
        
        // 7. Validação (compact constructor)
        System.out.println("7. Testando validação:");
        try {
            Product invalido = new Product(3L, "", BigDecimal.valueOf(100));
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Nome vazio rejeitado: " + e.getMessage());
        }
        
        try {
            Product invalido = new Product(4L, "Produto", BigDecimal.valueOf(-50));
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Preço negativo rejeitado: " + e.getMessage());
        }
        
        System.out.println("\n✅ Demonstração completa!");
        System.out.println("\nVantagens dos Records:");
        System.out.println("   • Menos código (1 linha vs ~50 linhas)");
        System.out.println("   • Imutável por padrão");
        System.out.println("   • toString/equals/hashCode automáticos");
        System.out.println("   • Perfeito para DTOs e Value Objects");
    }
}
