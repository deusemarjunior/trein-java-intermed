# Slide 7: Stream API & Optional

**Horário:** 11:00 - 11:30

---

## Stream API - Processamento Funcional

### 🎬 DEMO: Do Imperativo ao Funcional

```java
import java.util.*;
import java.math.BigDecimal;
import java.util.stream.*;

public class StreamApiDemo {
    
    record Product(Long id, String name, BigDecimal price, String category) {}
    
    public static void main(String[] args) {
        List<Product> products = List.of(
            new Product(1L, "Laptop", BigDecimal.valueOf(3500), "Electronics"),
            new Product(2L, "Mouse", BigDecimal.valueOf(50), "Electronics"),
            new Product(3L, "Desk", BigDecimal.valueOf(800), "Furniture"),
            new Product(4L, "Chair", BigDecimal.valueOf(600), "Furniture"),
            new Product(5L, "Monitor", BigDecimal.valueOf(1200), "Electronics")
        );
        
        // ❌ IMPERATIVO (Java antigo)
        List<String> expensiveElectronics = new ArrayList<>();
        for (Product p : products) {
            if (p.category().equals("Electronics") && 
                p.price().compareTo(BigDecimal.valueOf(1000)) > 0) {
                expensiveElectronics.add(p.name().toUpperCase());
            }
        }
        Collections.sort(expensiveElectronics);
        
        // ✅ FUNCIONAL (Stream API)
        List<String> result = products.stream()
            .filter(p -> p.category().equals("Electronics"))
            .filter(p -> p.price().compareTo(BigDecimal.valueOf(1000)) > 0)
            .map(Product::name)
            .map(String::toUpperCase)
            .sorted()
            .toList();  // Java 16+ (antes era .collect(Collectors.toList()))
        
        System.out.println(result);
        // Output: [LAPTOP, MONITOR]
    }
}
```

---

## Operações Comuns

```java
// 🔍 FILTRAR
List<Product> electronics = products.stream()
    .filter(p -> p.category().equals("Electronics"))
    .toList();

// 🔄 TRANSFORMAR (map)
List<String> names = products.stream()
    .map(Product::name)
    .toList();

// 📊 AGRUPAR
Map<String, List<Product>> byCategory = products.stream()
    .collect(Collectors.groupingBy(Product::category));

// 💰 SOMAR
BigDecimal total = products.stream()
    .map(Product::price)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

// 🔎 BUSCAR
Optional<Product> mostExpensive = products.stream()
    .max(Comparator.comparing(Product::price));

// ✅ VERIFICAR
boolean hasExpensive = products.stream()
    .anyMatch(p -> p.price().compareTo(BigDecimal.valueOf(1000)) > 0);
```

---

## Optional - Lidando com null

```java
// ❌ EVITE ISSO
public static Product findByIdOldWay(Long id) {
    Product product = repository.findById(id);
    if (product == null) {
        throw new NotFoundException("Product not found");
    }
    return product;
}

// ✅ USE OPTIONAL
public static Optional<Product> findById(Long id) {
    return repository.findById(id);
}
```

---

## Usando Optional

```java
// 1️⃣ ifPresent
findById(id).ifPresent(product -> {
    System.out.println("Found: " + product.name());
});

// 2️⃣ orElse - valor padrão
Product product = findById(id)
    .orElse(new Product(0L, "Default", BigDecimal.ZERO, "None"));

// 3️⃣ orElseGet - lazy evaluation
Product product2 = findById(id)
    .orElseGet(() -> createDefaultProduct());

// 4️⃣ orElseThrow - lançar exceção
Product product3 = findById(id)
    .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));

// 5️⃣ map - transformar
String name = findById(id)
    .map(Product::name)
    .orElse("Unknown");

// 6️⃣ filter - filtrar
Optional<Product> expensive = findById(id)
    .filter(p -> p.price().compareTo(BigDecimal.valueOf(1000)) > 0);
```

---

## ❌ ANTI-PATTERNS - NÃO FAÇA!

```java
Optional<Product> opt = findById(123L);

// ❌ Não use .get() sem verificar
Product p1 = opt.get();  // Pode lançar NoSuchElementException!

// ❌ Não use isPresent() + get()
if (opt.isPresent()) {
    Product p2 = opt.get();
}
// ✅ Use ifPresent ou orElse

// ❌ Não retorne Optional.of(null)
return Optional.of(null);  // Lança NullPointerException!
// ✅ Use Optional.ofNullable(value)

// ❌ Não use Optional como campo de classe
class BadExample {
    private Optional<String> name;  // ❌
}
```
