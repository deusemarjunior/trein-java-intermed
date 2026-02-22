package com.example.stream;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Demonstração completa de Stream API e Optional
 */
public class StreamApiDemo {
    
    public static void main(String[] args) {
        System.out.println("=== DEMONSTRAÇÃO DE STREAM API ===\n");
        
        List<Product> products = createSampleProducts();
        
        // 1. Imperativo vs Funcional
        demonstrateImperativeVsFunctional(products);
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 2. Operações comuns de Stream
        demonstrateCommonOperations(products);
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 3. Operações de agregação
        demonstrateAggregations(products);
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 4. Agrupamento e particionamento
        demonstrateGrouping(products);
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // 5. Optional - Lidando com null
        demonstrateOptional(products);
        
        System.out.println("\n✅ Demonstração completa!");
        System.out.println("\nVantagens da Stream API:");
        System.out.println("   • Código mais declarativo");
        System.out.println("   • Pipeline de operações");
        System.out.println("   • Lazy evaluation");
        System.out.println("   • Suporte a paralelismo");
    }
    
    private static List<Product> createSampleProducts() {
        return List.of(
            new Product(1L, "Laptop", BigDecimal.valueOf(3500), "Electronics"),
            new Product(2L, "Mouse", BigDecimal.valueOf(50), "Electronics"),
            new Product(3L, "Keyboard", BigDecimal.valueOf(200), "Electronics"),
            new Product(4L, "Monitor", BigDecimal.valueOf(1200), "Electronics"),
            new Product(5L, "Desk", BigDecimal.valueOf(800), "Furniture"),
            new Product(6L, "Chair", BigDecimal.valueOf(600), "Furniture"),
            new Product(7L, "Lamp", BigDecimal.valueOf(150), "Furniture"),
            new Product(8L, "Notebook", BigDecimal.valueOf(20), "Stationery"),
            new Product(9L, "Pen", BigDecimal.valueOf(5), "Stationery")
        );
    }
    
    /**
     * 1. Comparação: Imperativo vs Funcional
     */
    private static void demonstrateImperativeVsFunctional(List<Product> products) {
        System.out.println("1. IMPERATIVO VS FUNCIONAL\n");
        
        // Objetivo: Produtos eletrônicos caros (> R$ 1000), nomes em maiúsculo, ordenados
        
        // ❌ IMPERATIVO (Java antigo)
        System.out.println("❌ IMPERATIVO:");
        List<String> resultOld = new ArrayList<>();
        for (Product p : products) {
            if (p.category().equals("Electronics") && 
                p.price().compareTo(BigDecimal.valueOf(1000)) > 0) {
                resultOld.add(p.name().toUpperCase());
            }
        }
        Collections.sort(resultOld);
        System.out.println("   " + resultOld);
        
        // ✅ FUNCIONAL (Stream API)
        System.out.println("\n✅ FUNCIONAL (Stream API):");
        List<String> resultNew = products.stream()
            .filter(p -> p.category().equals("Electronics"))
            .filter(p -> p.price().compareTo(BigDecimal.valueOf(1000)) > 0)
            .map(Product::name)
            .map(String::toUpperCase)
            .sorted()
            .toList();
        System.out.println("   " + resultNew);
    }
    
    /**
     * 2. Operações comuns de Stream
     */
    private static void demonstrateCommonOperations(List<Product> products) {
        System.out.println("2. OPERAÇÕES COMUNS\n");
        
        // FILTER - Filtrar elementos
        System.out.println("🔍 FILTER - Eletrônicos:");
        List<Product> electronics = products.stream()
            .filter(p -> p.category().equals("Electronics"))
            .toList();
        electronics.forEach(p -> System.out.println("   • " + p.name()));
        
        // MAP - Transformar elementos
        System.out.println("\n🔄 MAP - Apenas nomes:");
        List<String> names = products.stream()
            .map(Product::name)
            .toList();
        System.out.println("   " + names);
        
        // SORTED - Ordenar
        System.out.println("\n📊 SORTED - Por preço (crescente):");
        products.stream()
            .sorted(Comparator.comparing(Product::price))
            .limit(3)  // Apenas os 3 mais baratos
            .forEach(p -> System.out.println("   • " + p.name() + ": R$ " + p.price()));
        
        // DISTINCT - Remover duplicatas
        System.out.println("\n🎯 DISTINCT - Categorias únicas:");
        List<String> categories = products.stream()
            .map(Product::category)
            .distinct()
            .sorted()
            .toList();
        System.out.println("   " + categories);
        
        // LIMIT e SKIP
        System.out.println("\n⏭️ LIMIT & SKIP - Paginação (página 2, 3 itens por página):");
        products.stream()
            .skip(3)   // Pular primeiros 3
            .limit(3)  // Pegar próximos 3
            .forEach(p -> System.out.println("   • " + p.name()));
    }
    
    /**
     * 3. Operações de agregação
     */
    private static void demonstrateAggregations(List<Product> products) {
        System.out.println("3. AGREGAÇÕES\n");
        
        // COUNT
        long count = products.stream()
            .filter(p -> p.category().equals("Electronics"))
            .count();
        System.out.println("📊 COUNT - Total de eletrônicos: " + count);
        
        // SUM (usando reduce)
        BigDecimal total = products.stream()
            .map(Product::price)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("\n💰 SUM - Valor total de estoque: R$ " + total);
        
        // AVERAGE
        OptionalDouble average = products.stream()
            .mapToDouble(p -> p.price().doubleValue())
            .average();
        average.ifPresent(avg -> 
            System.out.println("\n📈 AVERAGE - Preço médio: R$ " + String.format("%.2f", avg))
        );
        
        // MIN e MAX
        Optional<Product> cheapest = products.stream()
            .min(Comparator.comparing(Product::price));
        cheapest.ifPresent(p -> 
            System.out.println("\n⬇️ MIN - Produto mais barato: " + p.name() + " (R$ " + p.price() + ")")
        );
        
        Optional<Product> mostExpensive = products.stream()
            .max(Comparator.comparing(Product::price));
        mostExpensive.ifPresent(p -> 
            System.out.println("\n⬆️ MAX - Produto mais caro: " + p.name() + " (R$ " + p.price() + ")")
        );
        
        // ANY/ALL/NONE MATCH
        boolean hasExpensive = products.stream()
            .anyMatch(Product::isExpensive);
        System.out.println("\n✅ ANY MATCH - Tem produto caro? " + hasExpensive);
        
        boolean allExpensive = products.stream()
            .allMatch(Product::isExpensive);
        System.out.println("✅ ALL MATCH - Todos são caros? " + allExpensive);
        
        boolean noneExpensive = products.stream()
            .noneMatch(Product::isExpensive);
        System.out.println("✅ NONE MATCH - Nenhum é caro? " + noneExpensive);
    }
    
    /**
     * 4. Agrupamento e particionamento
     */
    private static void demonstrateGrouping(List<Product> products) {
        System.out.println("4. AGRUPAMENTO E PARTICIONAMENTO\n");
        
        // GROUP BY - Agrupar por categoria
        System.out.println("📦 GROUPING BY - Por categoria:");
        Map<String, List<Product>> byCategory = products.stream()
            .collect(Collectors.groupingBy(Product::category));
        
        byCategory.forEach((category, items) -> {
            System.out.println("   " + category + " (" + items.size() + " itens):");
            items.forEach(p -> System.out.println("      • " + p.name()));
        });
        
        // PARTITIONING - Dividir em 2 grupos (true/false)
        System.out.println("\n💎 PARTITIONING BY - Caros vs Baratos:");
        Map<Boolean, List<Product>> byPrice = products.stream()
            .collect(Collectors.partitioningBy(Product::isExpensive));
        
        System.out.println("   Caros (> R$ 1000): " + byPrice.get(true).size() + " itens");
        byPrice.get(true).forEach(p -> System.out.println("      • " + p.name()));
        
        System.out.println("   Baratos (<= R$ 1000): " + byPrice.get(false).size() + " itens");
        
        // COUNTING - Contar por categoria
        System.out.println("\n🔢 COUNTING - Quantidade por categoria:");
        Map<String, Long> countByCategory = products.stream()
            .collect(Collectors.groupingBy(Product::category, Collectors.counting()));
        countByCategory.forEach((cat, count) -> 
            System.out.println("   " + cat + ": " + count)
        );
    }
    
    /**
     * 5. Optional - Lidando com null
     */
    private static void demonstrateOptional(List<Product> products) {
        System.out.println("5. OPTIONAL - LIDANDO COM NULL\n");
        
        // Buscar produto
        System.out.println("🔎 Buscando produto por ID:\n");
        
        Optional<Product> found = findProductById(products, 1L);
        found.ifPresent(p -> 
            System.out.println("   ✅ Encontrado: " + p.name())
        );
        
        Optional<Product> notFound = findProductById(products, 999L);
        System.out.println("   ❌ ID 999: " + notFound.orElse(null));
        
        // Diferentes formas de usar Optional
        System.out.println("\n🎯 Formas de usar Optional:\n");
        
        // 1. ifPresent
        found.ifPresent(p -> 
            System.out.println("   1. ifPresent: " + p.name())
        );
        
        // 2. orElse (valor padrão)
        Product product = notFound.orElse(
            new Product(0L, "Default Product", BigDecimal.ZERO, "Unknown")
        );
        System.out.println("   2. orElse: " + product.name());
        
        // 3. orElseGet (lazy - só executa se vazio)
        product = notFound.orElseGet(() -> 
            new Product(0L, "Lazy Default", BigDecimal.ZERO, "Unknown")
        );
        System.out.println("   3. orElseGet: " + product.name());
        
        // 4. orElseThrow (lançar exceção)
        try {
            product = notFound.orElseThrow(() -> 
                new RuntimeException("Product not found!")
            );
        } catch (RuntimeException e) {
            System.out.println("   4. orElseThrow: " + e.getMessage());
        }
        
        // 5. map e filter com Optional
        String name = found
            .filter(p -> p.price().compareTo(BigDecimal.valueOf(1000)) > 0)
            .map(Product::name)
            .map(String::toUpperCase)
            .orElse("Not expensive");
        System.out.println("   5. map + filter: " + name);
    }
    
    private static Optional<Product> findProductById(List<Product> products, Long id) {
        return products.stream()
            .filter(p -> p.id().equals(id))
            .findFirst();
    }
}
