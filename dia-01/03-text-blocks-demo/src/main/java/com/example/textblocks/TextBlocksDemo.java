package com.example.textblocks;

import java.math.BigDecimal;

/**
 * Demonstração de Text Blocks do Java 15+
 * Substituem concatenações complexas de strings multilinha
 */
public class TextBlocksDemo {
    
    public static void main(String[] args) {
        System.out.println("=== DEMONSTRAÇÃO DE TEXT BLOCKS ===\n");
        
        // 1. JSON - Antes vs Agora
        demonstrateJson();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 2. SQL - Antes vs Agora
        demonstrateSql();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 3. HTML - Antes vs Agora
        demonstrateHtml();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 4. Formatação com String.format()
        demonstrateFormatting();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 5. Uso prático - Email template
        demonstrateEmailTemplate();
        
        System.out.println("\n✅ Demonstração completa!");
        System.out.println("\nVantagens de Text Blocks:");
        System.out.println("   • Mais legível - sem escapes");
        System.out.println("   • Sem concatenação com +");
        System.out.println("   • Preserva formatação");
        System.out.println("   • Ideal para JSON, SQL, HTML, XML");
    }
    
    private static void demonstrateJson() {
        System.out.println("1. JSON - COMPARAÇÃO\n");
        
        // ❌ ANTES - Concatenação horrível
        String jsonOld = "{\n" +
                        "  \"name\": \"Laptop\",\n" +
                        "  \"price\": 3500,\n" +
                        "  \"inStock\": true\n" +
                        "}";
        
        // ✅ AGORA - Text Block limpo
        String jsonNew = """
            {
              "name": "Laptop",
              "price": 3500,
              "inStock": true
            }
            """;
        
        System.out.println("❌ ANTES (concatenação):");
        System.out.println(jsonOld);
        System.out.println("\n✅ AGORA (text block):");
        System.out.println(jsonNew);
    }
    
    private static void demonstrateSql() {
        System.out.println("2. SQL - COMPARAÇÃO\n");
        
        // ❌ ANTES
        String sqlOld = "SELECT p.id, p.name, p.price \n" +
                       "FROM products p \n" +
                       "WHERE p.category = 'electronics' \n" +
                       "  AND p.price > 1000 \n" +
                       "ORDER BY p.price DESC";
        
        // ✅ AGORA
        String sqlNew = """
            SELECT p.id, p.name, p.price
            FROM products p
            WHERE p.category = 'electronics'
              AND p.price > 1000
            ORDER BY p.price DESC
            """;
        
        System.out.println("❌ ANTES:");
        System.out.println(sqlOld);
        System.out.println("\n✅ AGORA:");
        System.out.println(sqlNew);
    }
    
    private static void demonstrateHtml() {
        System.out.println("3. HTML - COMPARAÇÃO\n");
        
        // ❌ ANTES
        String htmlOld = "<html>\n" +
                        "  <body>\n" +
                        "    <h1>Welcome to our Store</h1>\n" +
                        "    <p>Check our products!</p>\n" +
                        "  </body>\n" +
                        "</html>";
        
        // ✅ AGORA
        String htmlNew = """
            <html>
              <body>
                <h1>Welcome to our Store</h1>
                <p>Check our products!</p>
              </body>
            </html>
            """;
        
        System.out.println("❌ ANTES:");
        System.out.println(htmlOld);
        System.out.println("\n✅ AGORA:");
        System.out.println(htmlNew);
    }
    
    private static void demonstrateFormatting() {
        System.out.println("4. FORMATAÇÃO COM VARIÁVEIS\n");
        
        String productName = "Laptop Gaming";
        BigDecimal price = BigDecimal.valueOf(4500.99);
        boolean inStock = true;
        
        // Usando String.format() com text block
        String json = """
            {
              "product": "%s",
              "price": %.2f,
              "currency": "BRL",
              "inStock": %b
            }
            """.formatted(productName, price, inStock);
        
        System.out.println("JSON com variáveis:");
        System.out.println(json);
        
        // Ou usando formatted() (Java 15+)
        String message = """
            Produto: %s
            Preço: R$ %.2f
            %s
            """.formatted(
                productName, 
                price,
                inStock ? "✅ Em estoque" : "❌ Indisponível"
            );
        
        System.out.println("Mensagem formatada:");
        System.out.println(message);
    }
    
    private static void demonstrateEmailTemplate() {
        System.out.println("5. CASO PRÁTICO - EMAIL TEMPLATE\n");
        
        String customerName = "João Silva";
        String orderNumber = "ORD-2026-0001";
        String productName = "Laptop Gaming";
        BigDecimal total = BigDecimal.valueOf(4500.00);
        
        String email = """
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            📧 CONFIRMAÇÃO DE PEDIDO
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            
            Olá, %s!
            
            Seu pedido foi confirmado com sucesso! ✅
            
            📦 DETALHES DO PEDIDO
            ─────────────────────────────────────────
            Número do pedido: %s
            Produto: %s
            Valor total: R$ %.2f
            
            🚚 ENTREGA
            ─────────────────────────────────────────
            Previsão: 3-5 dias úteis
            Você receberá um código de rastreamento em breve.
            
            Obrigado por comprar conosco!
            
            Atenciosamente,
            Equipe de Vendas
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            """.formatted(customerName, orderNumber, productName, total);
        
        System.out.println(email);
    }
}
