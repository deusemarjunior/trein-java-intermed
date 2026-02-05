package com.example.pattern;

import java.math.BigDecimal;
import java.util.List;

/**
 * Demonstração completa de Pattern Matching e Switch Expressions
 */
public class PatternMatchingDemo {
    
    public static void main(String[] args) {
        System.out.println("=== DEMONSTRAÇÃO DE PATTERN MATCHING ===\n");
        
        // 1. Pattern Matching for instanceof
        demonstrateInstanceof();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 2. Switch Expressions (Java 14+)
        demonstrateSwitchExpressions();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 3. Switch com blocos e yield
        demonstrateSwitchBlocks();
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 4. Pattern Matching for Switch (Java 21+)
        demonstratePatternSwitch();
        
        System.out.println("\n✅ Demonstração completa!");
        System.out.println("\nVantagens:");
        System.out.println("   • Menos código boilerplate");
        System.out.println("   • Mais seguro (sem cast duplicado)");
        System.out.println("   • Switch como expressão");
        System.out.println("   • Pattern matching poderoso");
    }
    
    /**
     * 1. Pattern Matching for instanceof (Java 16+)
     */
    private static void demonstrateInstanceof() {
        System.out.println("1. PATTERN MATCHING FOR INSTANCEOF\n");
        
        Object obj1 = "Hello World";
        Object obj2 = 42;
        Object obj3 = BigDecimal.valueOf(99.99);
        
        // ❌ ANTES - Cast duplicado
        System.out.println("❌ ANTES (Java 8-15):");
        if (obj1 instanceof String) {
            String str = (String) obj1;  // Cast manual!
            System.out.println("   String length: " + str.length());
        }
        
        // ✅ AGORA - Pattern matching
        System.out.println("\n✅ AGORA (Java 16+):");
        if (obj1 instanceof String str) {  // Declara variável direto!
            System.out.println("   String length: " + str.length());
        }
        
        if (obj2 instanceof Integer num) {
            System.out.println("   Integer value: " + num);
        }
        
        if (obj3 instanceof BigDecimal price) {
            System.out.println("   Price: R$ " + price);
        }
        
        // Com negação
        System.out.println("\n✅ Com negação:");
        if (!(obj1 instanceof Integer num)) {
            System.out.println("   Não é um Integer!");
        }
        
        // Com condição adicional
        System.out.println("\n✅ Com condição (&&):");
        if (obj1 instanceof String str && str.length() > 5) {
            System.out.println("   String longa: " + str);
        }
    }
    
    /**
     * 2. Switch Expressions (Java 14+)
     */
    private static void demonstrateSwitchExpressions() {
        System.out.println("2. SWITCH EXPRESSIONS\n");
        
        OrderStatus status = OrderStatus.PROCESSING;
        
        // ❌ ANTES - Statement (não retorna valor)
        System.out.println("❌ ANTES (switch statement):");
        String messageOld;
        switch (status) {
            case PENDING:
                messageOld = "Order is pending";
                break;
            case PROCESSING:
                messageOld = "Order is being processed";
                break;
            case COMPLETED:
                messageOld = "Order completed";
                break;
            case CANCELLED:
                messageOld = "Order was cancelled";
                break;
            case REFUNDED:
                messageOld = "Order was refunded";
                break;
            default:
                messageOld = "Unknown status";
        }
        System.out.println("   " + messageOld);
        
        // ✅ AGORA - Expression (retorna valor!)
        System.out.println("\n✅ AGORA (switch expression):");
        String messageNew = switch (status) {
            case PENDING -> "Order is pending";
            case PROCESSING -> "Order is being processed";
            case COMPLETED -> "Order completed";
            case CANCELLED -> "Order was cancelled";
            case REFUNDED -> "Order was refunded";
        };  // Sem default necessário se enum completo!
        System.out.println("   " + messageNew);
        
        // Múltiplos casos
        System.out.println("\n✅ Múltiplos casos em uma linha:");
        String category = switch (status) {
            case PENDING, PROCESSING -> "Active";
            case COMPLETED, REFUNDED -> "Finished";
            case CANCELLED -> "Inactive";
        };
        System.out.println("   Category: " + category);
    }
    
    /**
     * 3. Switch com blocos e yield
     */
    private static void demonstrateSwitchBlocks() {
        System.out.println("3. SWITCH COM BLOCOS E YIELD\n");
        
        OrderStatus status = OrderStatus.COMPLETED;
        
        String message = switch (status) {
            case PENDING -> {
                System.out.println("   [LOG] Order is pending...");
                yield "⏳ Aguardando processamento";
            }
            case PROCESSING -> {
                System.out.println("   [LOG] Order is being processed...");
                yield "🔄 Processando seu pedido";
            }
            case COMPLETED -> {
                System.out.println("   [LOG] Order completed successfully!");
                yield "✅ Pedido concluído com sucesso!";
            }
            case CANCELLED, REFUNDED -> {
                System.out.println("   [LOG] Order was terminated.");
                yield "❌ Pedido cancelado/reembolsado";
            }
        };
        
        System.out.println("   Result: " + message);
    }
    
    /**
     * 4. Pattern Matching for Switch (Java 21+)
     */
    private static void demonstratePatternSwitch() {
        System.out.println("4. PATTERN MATCHING FOR SWITCH (Java 21+)\n");
        
        Object[] objects = {
            null,
            "Hello World",
            42,
            -10,
            BigDecimal.valueOf(1500.50),
            List.of("A", "B", "C"),
            new int[]{1, 2, 3}
        };
        
        for (Object obj : objects) {
            String description = getDescription(obj);
            System.out.println("   " + description);
        }
    }
    
    /**
     * Pattern matching com guards (condições)
     */
    private static String getDescription(Object obj) {
        return switch (obj) {
            case null -> "❌ Object is null";
            case String s when s.isEmpty() -> "📝 Empty string";
            case String s -> "📝 String of length " + s.length() + ": '" + s + "'";
            case Integer i when i > 0 -> "➕ Positive integer: " + i;
            case Integer i when i < 0 -> "➖ Negative integer: " + i;
            case Integer i -> "0️⃣ Zero";
            case BigDecimal bd when bd.compareTo(BigDecimal.valueOf(1000)) > 0 ->
                "💰 Expensive: R$ " + bd;
            case BigDecimal bd -> "💵 Price: R$ " + bd;
            case List<?> list when list.isEmpty() -> "📋 Empty list";
            case List<?> list -> "📋 List with " + list.size() + " elements: " + list;
            case int[] arr -> "🔢 Array with " + arr.length + " elements";
            default -> "❓ Unknown type: " + obj.getClass().getSimpleName();
        };
    }
}
