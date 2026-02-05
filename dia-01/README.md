# Dia 1 - Fundamentos Java Moderno e Spring Boot

**Duração**: 5 horas  
**Objetivo**: Compreender os recursos modernos do Java e iniciar com Spring Boot

---

## 🎯 Agenda do Dia

| Horário | Duração | Tópico | Tipo |
|---------|---------|--------|------|
| 09:00 - 09:15 | 15min | Apresentação e Setup | Prática |
| 09:15 - 10:45 | 1h30 | Java Moderno (17/21) | Teórico + Demo |
| 10:45 - 11:00 | 15min | ☕ Coffee Break | - |
| 11:00 - 12:00 | 1h | Lombok vs Records | Teórico + Demo |
| 12:00 - 13:00 | 1h | 🍽️ Almoço | - |
| 13:00 - 14:30 | 1h30 | Spring Boot Fundamentals | Teórico + Demo |
| 14:30 - 16:00 | 1h30 | Primeira API REST | Hands-on |
| 16:00 - 16:30 | 30min | Review e Q&A | Discussão |

---

## 📦 Material Necessário (Checklist Instrutor)

### Software (verificar antes da aula)
- [ ] JDK 17 ou 21 instalado
- [ ] Maven 3.8+ ou Gradle 8+
- [ ] IDE configurada (IntelliJ IDEA / VS Code)
- [ ] Postman ou Insomnia
- [ ] Git instalado
- [ ] Docker (opcional, para dia 2+)

### Links para compartilhar
- [ ] [Spring Initializr](https://start.spring.io/)
- [ ] [GitHub repo do curso](https://github.com/seu-repo)
- [ ] [Slides da apresentação](link)
- [ ] [Exercícios](link)

### Arquivos preparados
- [ ] Projeto base Spring Boot
- [ ] Exemplos de código prontos
- [ ] Scripts de demonstração
- [ ] Dataset para exemplos

---

## 📋 Conteúdo Programático

### 🌅 Manhã (3 horas)

---

## SLIDE 1: Abertura e Boas-vindas (09:00 - 09:15)

### Apresentação do Instrutor
```
👨‍🏫 [Seu Nome]
📧 [email@example.com]
💼 [Experiência com Java/Spring]

Regras da sala:
✓ Perguntas são bem-vindas a qualquer momento
✓ Câmeras ligadas (se possível)
✓ Hands-on: código junto comigo
✓ Pausa de 15min a cada 1h30
```

### Setup Rápido
```bash
# Verificar instalações
java -version  # Esperado: openjdk 17 ou 21
mvn -version   # Esperado: Maven 3.8+

# Clonar repositório do curso
git clone https://github.com/seu-repo/java-intermediate
cd java-intermediate/dia-01
```

**💡 Dica do Instrutor**: Peça para todos compartilharem no chat a versão do Java que estão usando.

---

## SLIDE 2: Por que Java Moderno? (09:15 - 09:20)

### Evolução do Java

```
Java 8  (2014) ━━━━━━━━━━━━━━━━┓
                               ┃ 6 anos de gap!
Java 11 (2018 - LTS) ━━━━━━━━━┛
Java 17 (2021 - LTS) ━━━━━━━━━┓
                               ┃ Ciclo de 3 anos
Java 21 (2024 - LTS) ━━━━━━━━━┛
Java 25 (2027 - próximo LTS)
```

### Por que atualizar?
✅ Menos código boilerplate (Records, Pattern Matching)  
✅ Melhor performance (GC improvements)  
✅ Recursos de produtividade (Text Blocks, Switch Expressions)  
✅ Segurança e suporte  
✅ Mercado de trabalho exige

**🎯 Pergunta para a turma**: Quem já trabalhou com Java 8? E com Java 17+?

---

## SLIDE 3: Records - A Revolução dos DTOs (09:20 - 09:50)

### Problema Tradicional

```java
// ❌ ANTES (Java 8-13): ~50 linhas para um DTO simples!
public class Product {
    private final Long id;
    private final String name;
    private final BigDecimal price;
    
    public Product(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    
    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
               Objects.equals(name, product.name) &&
               Objects.equals(price, product.price);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }
    
    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + 
               "', price=" + price + '}';
    }
}
```

### ✨ Solução com Records (Java 14+)

```java
// ✅ AGORA (Java 17+): 1 linha!
public record Product(Long id, String name, BigDecimal price) {}

// Grátis: constructor, getters, equals, hashCode, toString
```

**🎬 DEMO AO VIVO** (5 minutos)

```java
// Criar arquivo: src/main/java/com/example/demo/RecordsDemo.java
package com.example.demo;

import java.math.BigDecimal;

public record Product(Long id, String name, BigDecimal price) {
    
    // ✅ Compact constructor - validação
    public Product {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
    }
    
    // ✅ Métodos customizados
    public boolean isExpensive() {
        return price.compareTo(BigDecimal.valueOf(1000)) > 0;
    }
    
    public Product applyDiscount(BigDecimal percentage) {
        BigDecimal factor = BigDecimal.ONE.subtract(percentage);
        return new Product(id, name, price.multiply(factor));
    }
    
    // ✅ Métodos estáticos factory
    public static Product create(String name, BigDecimal price) {
        return new Product(null, name, price);
    }
}

// Teste rápido
class Main {
    public static void main(String[] args) {
        // Criar produto
        Product laptop = new Product(1L, "Laptop", BigDecimal.valueOf(3500));
        System.out.println(laptop);
        // Output: Product[id=1, name=Laptop, price=3500]
        
        // Usar métodos
        System.out.println("Is expensive? " + laptop.isExpensive());
        // Output: Is expensive? true
        
        // Aplicar desconto (imutabilidade!)
        Product discounted = laptop.applyDiscount(BigDecimal.valueOf(0.1));
        System.out.println("Original: " + laptop.price());
        System.out.println("Discounted: " + discounted.price());
        // Output: Original: 3500
        // Output: Discounted: 3150
        
        // Equals e hashCode funcionam!
        Product laptop2 = new Product(1L, "Laptop", BigDecimal.valueOf(3500));
        System.out.println("Equals? " + laptop.equals(laptop2));
        // Output: Equals? true
    }
}
```

**📝 Pontos-chave para enfatizar:**
1. Records são **imutáveis** por padrão
2. Getters não têm prefixo `get` → `product.name()` não `product.getName()`
3. Compact constructor é executado ANTES da atribuição
4. Ideal para DTOs, Value Objects, Responses

**🤔 Perguntas para discussão:**
- Por que imutabilidade é importante?
- Quando NÃO usar Records? (Entidades JPA!)

---

## SLIDE 4: Sealed Classes (09:50 - 10:10)

### O Problema

```java
// ❌ Hierarquia aberta - qualquer um pode estender!
public abstract class Payment {
    abstract void process();
}

// Em outro arquivo, alguém pode fazer:
public class BitcoinPayment extends Payment { ... } // 😱
```

### ✅ Solução: Sealed Classes

```java
// Controle total sobre a hierarquia
public sealed class Payment 
    permits CreditCardPayment, PixPayment, BoletoPayment {
    
    abstract void process();
}

public final class CreditCardPayment extends Payment {
    @Override
    void process() {
        System.out.println("Processing credit card...");
    }
}

public final class PixPayment extends Payment {
    @Override
    void process() {
        System.out.println("Processing PIX...");
    }
}

public final class BoletoPayment extends Payment {
    @Override
    void process() {
        System.out.println("Processing boleto...");
    }
}
```

**🎬 DEMO: Pattern Matching com Sealed Classes**

```java
public class PaymentProcessor {
    
    public static String getProcessingFee(Payment payment) {
        // ✨ Pattern matching for instanceof (Java 16+)
        if (payment instanceof CreditCardPayment cc) {
            return "Fee: " + cc.calculateFee();
        } else if (payment instanceof PixPayment pix) {
            return "Fee: 0 (PIX is free!)";
        } else if (payment instanceof BoletoPayment boleto) {
            return "Fee: " + boleto.getBankFee();
        }
        
        throw new IllegalArgumentException("Unknown payment type");
    }
    
    // ✨ Pattern matching for switch (Java 21+)
    public static String getProcessingFeeModern(Payment payment) {
        return switch (payment) {
            case CreditCardPayment cc -> "Fee: " + cc.calculateFee();
            case PixPayment pix -> "Fee: 0 (PIX is free!)";
            case BoletoPayment boleto -> "Fee: " + boleto.getBankFee();
            // Não precisa default! Compiler sabe que cobriu todos os casos
        };
    }
}
```

**💡 Casos de uso:**
- Modelagem de domínio com tipos fixos
- State machines
- Command patterns
- Payment methods, Order statuses, etc.

---

## SLIDE 5: Text Blocks (10:10 - 10:25)

### Antes era assim... 😢

```java
String json = "{\n" +
              "  \"name\": \"Laptop\",\n" +
              "  \"price\": 3500,\n" +
              "  \"inStock\": true\n" +
              "}";

String sql = "SELECT p.id, p.name, p.price \n" +
             "FROM products p \n" +
             "WHERE p.category = 'electronics' \n" +
             "  AND p.price > 1000 \n" +
             "ORDER BY p.price DESC";

String html = "<html>\n" +
              "  <body>\n" +
              "    <h1>Welcome</h1>\n" +
              "  </body>\n" +
              "</html>";
```

### ✨ Agora com Text Blocks (Java 15+)

```java
String json = """
    {
      "name": "Laptop",
      "price": 3500,
      "inStock": true
    }
    """;

String sql = """
    SELECT p.id, p.name, p.price
    FROM products p
    WHERE p.category = 'electronics'
      AND p.price > 1000
    ORDER BY p.price DESC
    """;

String html = """
    <html>
      <body>
        <h1>Welcome</h1>
      </body>
    </html>
    """;
```

**🎬 DEMO: Interpolação e Formatação**

```java
public class TextBlocksDemo {
    
    public static void main(String[] args) {
        String name = "Laptop Gaming";
        BigDecimal price = BigDecimal.valueOf(4500);
        
        // ✅ Com formatação
        String json = """
            {
              "product": "%s",
              "price": %.2f,
              "currency": "BRL"
            }
            """.formatted(name, price);
        
        System.out.println(json);
        
        // ✅ SQL com parâmetros
        Long categoryId = 5L;
        String sql = """
            SELECT p.*
            FROM products p
            WHERE p.category_id = %d
              AND p.active = true
            """.formatted(categoryId);
        
        System.out.println(sql);
    }
}
```

**⚠️ Cuidado:**
- Identação importa! É preservada
- Útil para testes, queries, JSON, XML, HTML
- Não substitui templates complexos (use Thymeleaf, etc)

---

## SLIDE 6: Pattern Matching & Switch Expressions (10:25 - 10:45)

### Pattern Matching for instanceof

```java
// ❌ ANTES
Object obj = getProduct();
if (obj instanceof Product) {
    Product product = (Product) obj;  // Cast duplicado!
    System.out.println(product.name());
}

// ✅ AGORA (Java 16+)
Object obj = getProduct();
if (obj instanceof Product product) {  // Declara variável direto!
    System.out.println(product.name());
}

// ✅ Com negação
if (!(obj instanceof Product product)) {
    throw new IllegalArgumentException("Not a product");
}
// 'product' disponível aqui se passou do if
```

### Switch Expressions (Java 14+)

```java
// ❌ ANTES - Statement
String message;
switch (status) {
    case PENDING:
        message = "Order is pending";
        break;
    case PROCESSING:
        message = "Order is being processed";
        break;
    case COMPLETED:
        message = "Order completed";
        break;
    case CANCELLED:
        message = "Order was cancelled";
        break;
    default:
        message = "Unknown status";
}

// ✅ AGORA - Expression (retorna valor)
String message = switch (status) {
    case PENDING -> "Order is pending";
    case PROCESSING -> "Order is being processed";
    case COMPLETED -> "Order completed";
    case CANCELLED -> "Order was cancelled";
};  // Sem default se enum cobrir todos os casos!

// ✅ Com blocos
String message = switch (status) {
    case PENDING -> {
        log.info("Order pending");
        yield "Order is pending";
    }
    case PROCESSING -> {
        log.info("Order processing");
        yield "Order is being processed";
    }
    default -> "Unknown";
};
```

### ✨ Pattern Matching for Switch (Java 21+)

```java
public static String getDescription(Object obj) {
    return switch (obj) {
        case null -> "Object is null";
        case String s -> "String of length " + s.length();
        case Integer i && i > 0 -> "Positive integer: " + i;
        case Integer i -> "Non-positive integer: " + i;
        case Product p && p.price().compareTo(BigDecimal.valueOf(1000)) > 0 ->
            "Expensive product: " + p.name();
        case Product p -> "Product: " + p.name();
        case List<?> list -> "List with " + list.size() + " elements";
        default -> "Unknown type: " + obj.getClass().getName();
    };
}
```

**🎬 DEMO COMPLETO**

```java
public class PatternMatchingDemo {
    
    public static void processPayment(Payment payment, BigDecimal amount) {
        String result = switch (payment) {
            case CreditCardPayment cc when amount.compareTo(BigDecimal.valueOf(5000)) > 0 ->
                "Large credit card payment - requires approval";
            
            case CreditCardPayment cc ->
                "Processing credit card: " + cc.getCardNumber();
            
            case PixPayment pix when amount.compareTo(BigDecimal.ZERO) <= 0 ->
                throw new IllegalArgumentException("Invalid amount");
            
            case PixPayment pix ->
                "Processing PIX to key: " + pix.getPixKey();
            
            case BoletoPayment boleto ->
                "Generating boleto with due date: " + boleto.getDueDate();
        };
        
        System.out.println(result);
    }
}
```

---

## ☕ COFFEE BREAK (10:45 - 11:00)

**Instrutor:** Use este tempo para:
- Verificar se todos estão acompanhando
- Resolver dúvidas individuais
- Testar ambiente de quem teve problemas

---

## ☕ COFFEE BREAK (10:45 - 11:00)

**Instrutor:** Use este tempo para:
- Verificar se todos estão acompanhando
- Resolver dúvidas individuais
- Testar ambiente de quem teve problemas

---

## SLIDE 7: Stream API & Optional (11:00 - 11:30)

### Stream API - Processamento Funcional

**🎬 DEMO: Do Imperativo ao Funcional**

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

### Operações Comuns

```java
public class StreamOperations {
    
    static List<Product> products = getProducts();
    
    // 🔍 FILTRAR
    public static List<Product> getElectronics() {
        return products.stream()
            .filter(p -> p.category().equals("Electronics"))
            .toList();
    }
    
    // 🔄 TRANSFORMAR (map)
    public static List<String> getProductNames() {
        return products.stream()
            .map(Product::name)
            .toList();
    }
    
    // 📊 AGRUPAR
    public static Map<String, List<Product>> groupByCategory() {
        return products.stream()
            .collect(Collectors.groupingBy(Product::category));
    }
    
    // 💰 SOMAR
    public static BigDecimal getTotalValue() {
        return products.stream()
            .map(Product::price)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // 📈 ESTATÍSTICAS
    public static DoubleSummaryStatistics getPriceStatistics() {
        return products.stream()
            .mapToDouble(p -> p.price().doubleValue())
            .summaryStatistics();
        // min, max, average, sum, count
    }
    
    // 🔎 BUSCAR
    public static Optional<Product> findMostExpensive() {
        return products.stream()
            .max(Comparator.comparing(Product::price));
    }
    
    // ✅ VERIFICAR
    public static boolean hasExpensiveProducts() {
        return products.stream()
            .anyMatch(p -> p.price().compareTo(BigDecimal.valueOf(1000)) > 0);
    }
    
    // 📦 COLETAR EM MAPA
    public static Map<Long, String> getIdToNameMap() {
        return products.stream()
            .collect(Collectors.toMap(
                Product::id,
                Product::name
            ));
    }
    
    // 🎯 PARTICIONANDO
    public static Map<Boolean, List<Product>> partitionByPrice() {
        return products.stream()
            .collect(Collectors.partitioningBy(
                p -> p.price().compareTo(BigDecimal.valueOf(500)) > 0
            ));
        // {true=[expensive products], false=[cheap products]}
    }
}
```

### Optional - Lidando com null de forma elegante

```java
public class OptionalDemo {
    
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
    
    // 💡 USANDO OPTIONAL
    public static void examples() {
        Long id = 123L;
        
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
        
        // 7️⃣ flatMap - evitar Optional<Optional<T>>
        Optional<String> category = findById(id)
            .flatMap(p -> findCategoryById(p.categoryId()))
            .map(Category::name);
        
        // 8️⃣ or - fallback para outro Optional (Java 9+)
        Product result = findById(id)
            .or(() -> findByName("Laptop"))
            .orElse(null);
    }
    
    // ❌ ANTI-PATTERNS - NÃO FAÇA ISSO!
    public static void antiPatterns() {
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
        // ✅ Use apenas em retornos de métodos
    }
}
```

**🎯 Exercício Rápido (10 min)**

```java
// Dado esta lista, use Stream API para:
List<Product> products = getProducts();

// 1. Encontrar o produto mais caro de cada categoria
// 2. Calcular a média de preço por categoria
// 3. Listar nomes de produtos com preço > 500, em uppercase, ordenados
// 4. Verificar se existe algum produto na categoria "Books"
// 5. Criar um Map<String, BigDecimal> com categoria -> total de preços

// SOLUÇÃO:
// 1.
Map<String, Optional<Product>> mostExpensiveByCategory = products.stream()
    .collect(Collectors.groupingBy(
        Product::category,
        Collectors.maxBy(Comparator.comparing(Product::price))
    ));

// 2.
Map<String, Double> avgPriceByCategory = products.stream()
    .collect(Collectors.groupingBy(
        Product::category,
        Collectors.averagingDouble(p -> p.price().doubleValue())
    ));

// 3.
List<String> expensiveNames = products.stream()
    .filter(p -> p.price().compareTo(BigDecimal.valueOf(500)) > 0)
    .map(Product::name)
    .map(String::toUpperCase)
    .sorted()
    .toList();

// 4.
boolean hasBooks = products.stream()
    .anyMatch(p -> p.category().equals("Books"));

// 5.
Map<String, BigDecimal> totalByCategory = products.stream()
    .collect(Collectors.groupingBy(
        Product::category,
        Collectors.reducing(
            BigDecimal.ZERO,
            Product::price,
            BigDecimal::add
        )
    ));
```

---

## SLIDE 8: Lombok vs Records - O Grande Debate (11:30 - 12:00)

### ⚠️ DISCLAIMER IMPORTANTE

```
╔═══════════════════════════════════════════════════════════╗
║  Com Java 17+, Records resolvem 80% dos casos de uso     ║
║  que antes exigiam Lombok!                                ║
║                                                           ║
║  Recomendação 2026: PREFIRA RECORDS                       ║
╚═══════════════════════════════════════════════════════════╝
```

### Comparação Visual

```java
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// CENÁRIO 1: DTO Simples
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// 🅰️ LOMBOK
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
}

// 🅱️ RECORD ✅ VENCEDOR!
public record ProductDTO(Long id, String name, BigDecimal price) {}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// CENÁRIO 2: Entidade JPA
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// 🅰️ LOMBOK ✅ VENCEDOR!
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private BigDecimal price;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}

// 🅱️ RECORD ❌ NÃO FUNCIONA!
// Records são imutáveis, JPA precisa de setters

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// CENÁRIO 3: Builder Pattern
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// 🅰️ LOMBOK ✅ VENCEDOR!
@Builder
@Data
public class CreateOrderRequest {
    private Long customerId;
    private List<OrderItem> items;
    private Address shippingAddress;
    private PaymentMethod paymentMethod;
}

// Uso:
CreateOrderRequest request = CreateOrderRequest.builder()
    .customerId(123L)
    .items(items)
    .shippingAddress(address)
    .paymentMethod(PaymentMethod.CREDIT_CARD)
    .build();

// 🅱️ RECORD - Possível, mas verboso
public record CreateOrderRequest(
    Long customerId,
    List<OrderItem> items,
    Address shippingAddress,
    PaymentMethod paymentMethod
) {
    // Precisa criar builder manualmente...
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// CENÁRIO 4: Logging
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// 🅰️ LOMBOK ✅ VENCEDOR!
@Slf4j
@Service
public class ProductService {
    public void doSomething() {
        log.info("Processing...");
        log.error("Error!", exception);
    }
}

// 🅱️ RECORD/JAVA PURO - Verboso
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    
    public void doSomething() {
        log.info("Processing...");
    }
}
```

### 📊 Tabela de Decisão

| Caso de Uso | Lombok | Record | Vencedor | Por quê? |
|-------------|:------:|:------:|:--------:|----------|
| DTO Request/Response | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | **RECORD** | Mais simples, padrão Java |
| Entidade JPA | ⭐⭐⭐⭐⭐ | ❌ | **LOMBOK** | JPA precisa mutabilidade |
| Value Object | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | **RECORD** | Imutabilidade é desejada |
| Builder Pattern | ⭐⭐⭐⭐⭐ | ⭐⭐ | **LOMBOK** | @Builder é muito conveniente |
| Logging | ⭐⭐⭐⭐⭐ | ⭐ | **LOMBOK** | @Slf4j economiza linha |
| Classes Simples | ⭐⭐⭐ | ⭐⭐⭐⭐ | **RECORD** | Menos dependências |

### 🎯 Guia de Decisão (Fluxograma)

```
Preciso criar uma classe Java...
         │
         ├─ É imutável (sem setters)? ──────────────> SIM ──> USE RECORD ✅
         │                                                      
         ├─ É entidade JPA/Hibernate? ──────────────> SIM ──> USE LOMBOK (@Getter/@Setter) ✅
         │                                                      
         ├─ Precisa de Builder complexo? ───────────> SIM ──> USE LOMBOK (@Builder) ✅
         │                                                      
         ├─ Precisa de logging? ────────────────────> SIM ──> USE LOMBOK (@Slf4j) ✅
         │                                                      
         └─ Caso contrário ─────────────────────────────────> AVALIE: Record ou Java puro
```

### 🎬 DEMO: Converter Lombok → Record

```java
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// ANTES: ProductResponse com Lombok
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private LocalDateTime createdAt;
}

// Uso:
ProductResponse response = ProductResponse.builder()
    .id(1L)
    .name("Laptop")
    .price(BigDecimal.valueOf(3500))
    .category("Electronics")
    .createdAt(LocalDateTime.now())
    .build();

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// DEPOIS: ProductResponse com Record ✅
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
public record ProductResponse(
    Long id,
    String name,
    BigDecimal price,
    String category,
    LocalDateTime createdAt
) {
    // Factory method para criar facilmente
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.id(),
            product.name(),
            product.price(),
            product.category(),
            LocalDateTime.now()
        );
    }
}

// Uso:
ProductResponse response = new ProductResponse(
    1L,
    "Laptop",
    BigDecimal.valueOf(3500),
    "Electronics",
    LocalDateTime.now()
);

// Ou com factory:
ProductResponse response = ProductResponse.from(product);
```

### ⚠️ Problemas do Lombok

```java
// 1️⃣ "MÁGICA" - não é óbvio o que está sendo gerado
@Data  // O que isso gera exatamente? 🤔
public class User {
    private String password;  // Ops! toString vai expor a senha!
}

// 2️⃣ DEPENDÊNCIA DA IDE
// Colegas sem plugin Lombok instalado veem erros em todo lugar

// 3️⃣ DEBUGGING DIFÍCIL
// Breakpoints em getters/setters gerados não funcionam direito

// 4️⃣ CONFLITOS
@Data
@Entity
public class Product {  // @Data com JPA pode causar problemas
    @OneToMany
    private List<Review> reviews;  // toString infinito! 💥
}
```

### ✅ Recomendação Final 2026

```java
// ✅ FAÇA ISSO
// DTOs e Responses
public record CreateProductRequest(String name, BigDecimal price) {}
public record ProductResponse(Long id, String name, BigDecimal price) {}

// Entidades JPA
@Entity
@Getter @Setter
@NoArgsConstructor
public class ProductEntity { ... }

// Services com logging
@Slf4j
@Service
public class ProductService { ... }

// ❌ EVITE ISSO
@Data  // Muito genérico! Seja específico
@AllArgsConstructor  // Record faz isso melhor
@ToString  // Record faz isso melhor
@EqualsAndHashCode  // Record faz isso melhor
```

---

## 🍽️ ALMOÇO (12:00 - 13:00)

**Para o instrutor:**
- Deixar exercícios opcionais disponíveis para quem quiser praticar
- Estar disponível no chat para dúvidas
- Preparar ambiente Spring Boot para a tarde

---
---

## 🍽️ ALMOÇO (12:00 - 13:00)

**Para o instrutor:**
- Deixar exercícios opcionais disponíveis para quem quiser praticar
- Estar disponível no chat para dúvidas
- Preparar ambiente Spring Boot para a tarde

---

### 🌆 Tarde (2 horas)

---

## SLIDE 9: Spring Framework vs Spring Boot (13:00 - 13:15)

### A Evolução

```
Spring Framework (2004)
   ↓
Configuração XML complexa 😫
   ↓
Spring 3.0 - Java Config
   ↓
Ainda precisa configurar TUDO manualmente
   ↓
Spring Boot (2014) 🎉
   ↓
"Convenção sobre Configuração"
Zero XML, minimal config
```

### Comparação: Antes vs Depois

```java
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// SPRING FRAMEWORK (SEM BOOT) - ~50 linhas de config
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
<!-- web.xml -->
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
</servlet>

<!-- applicationContext.xml -->
<beans>
    <context:component-scan base-package="com.example"/>
    <mvc:annotation-driven/>
    
    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/db"/>
        <property name="username" value="root"/>
        <property name="password" value="password"/>
    </bean>
    
    <bean id="sessionFactory" class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="packagesToScan" value="com.example.model"/>
    </bean>
    <!-- + muitas outras configurações... -->
</beans>

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// SPRING BOOT - 0 linhas de XML! ✨
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
```

### 🎯 Conceitos Fundamentais

#### 1. Inversão de Controle (IoC)

```java
// ❌ SEM IoC - Controle manual
public class OrderService {
    private ProductRepository repository = new ProductRepositoryImpl();
    private PaymentGateway gateway = new PaymentGatewayImpl();
    
    // Acoplamento forte! Difícil de testar!
}

// ✅ COM IoC - Spring gerencia as dependências
@Service
public class OrderService {
    private final ProductRepository repository;
    private final PaymentGateway gateway;
    
    // Spring injeta automaticamente
    public OrderService(ProductRepository repository, PaymentGateway gateway) {
        this.repository = repository;
        this.gateway = gateway;
    }
}
```

#### 2. Injeção de Dependências (DI)

```java
// 3 formas de injetar dependências:

// 1️⃣ CONSTRUCTOR INJECTION ✅ RECOMENDADO!
@Service
public class ProductService {
    private final ProductRepository repository;
    
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
}

// 2️⃣ SETTER INJECTION (raramente usado)
@Service
public class ProductService {
    private ProductRepository repository;
    
    @Autowired
    public void setRepository(ProductRepository repository) {
        this.repository = repository;
    }
}

// 3️⃣ FIELD INJECTION ❌ EVITE! (dificulta testes)
@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
}
```

#### 3. Auto-configuração Mágica ✨

```java
// Apenas adicionando dependência no pom.xml:
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

// Spring Boot automaticamente configura:
// ✅ DataSource
// ✅ EntityManager
// ✅ TransactionManager
// ✅ JPA Repositories
// ✅ Hibernate

// Você só precisa usar!
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {}
```

### 🏗️ Spring Boot Starters

```xml
<!-- Starter Web: REST APIs -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- Inclui: Tomcat, Jackson, Spring MVC, validation -->

<!-- Starter Data JPA: Banco de dados -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- Inclui: Hibernate, JPA, JDBC, Transaction -->

<!-- Starter Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<!-- Inclui: Bean Validation, Hibernate Validator -->

<!-- Starter Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<!-- Inclui: JUnit, Mockito, AssertJ, Spring Test -->
```

---

## SLIDE 10: Criando Primeiro Projeto (13:15 - 13:30)

### 🎬 DEMO AO VIVO: Spring Initializr

**1. Acesse:** https://start.spring.io/

**2. Configure:**
```
Project: Maven
Language: Java
Spring Boot: 3.2.x (última stable)
Packaging: Jar
Java: 17 ou 21

Group: com.example
Artifact: products-api
Name: products-api
Description: Products REST API
Package name: com.example.products
```

**3. Dependências:**
- Spring Web
- Spring Data JPA
- H2 Database (para começar)
- Lombok (opcional)
- Validation
- Spring Boot DevTools

**4. Generate → Download → Extrair → Abrir na IDE**

### Estrutura Gerada

```
products-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/products/
│   │   │       └── ProductsApiApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/example/products/
│               └── ProductsApiApplicationTests.java
├── pom.xml
└── README.md
```

### Arquivo Principal

```java
package com.example.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // ← Mágica acontece aqui!
public class ProductsApiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductsApiApplication.class, args);
    }
}
```

**O que @SpringBootApplication faz?**
```java
@SpringBootApplication = 
    @Configuration +           // Classe de configuração
    @EnableAutoConfiguration + // Auto-config mágica
    @ComponentScan            // Escaneia @Component, @Service, etc
```

### Configuração (application.yml)

```yaml
# src/main/resources/application.yml
spring:
  application:
    name: products-api
  
  # H2 Database (para desenvolvimento)
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  
  h2:
    console:
      enabled: true  # http://localhost:8080/h2-console
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop  # Cria tabelas ao iniciar
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080

logging:
  level:
    com.example.products: DEBUG
    org.springframework.web: INFO
```

### Rodando a aplicação

```bash
# Opção 1: Maven
./mvnw spring-boot:run

# Opção 2: Java (após build)
./mvnw clean package
java -jar target/products-api-0.0.1-SNAPSHOT.jar

# Opção 3: IDE
# Run ProductsApiApplication.java
```

**Output esperado:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

2026-02-03 13:25:01.234  INFO --- [main] c.e.p.ProductsApiApplication : Starting ProductsApiApplication
2026-02-03 13:25:02.456  INFO --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http)
2026-02-03 13:25:02.789  INFO --- [main] c.e.p.ProductsApiApplication : Started ProductsApiApplication in 2.1 seconds
```

---

## SLIDE 11: Estrutura do Projeto (13:30 - 13:45)

### 📁 Organização Recomendada

```
src/main/java/com/example/products/
├── ProductsApiApplication.java
│
├── controller/          # REST Controllers
│   └── ProductController.java
│
├── service/            # Lógica de negócio
│   └── ProductService.java
│
├── repository/         # Acesso a dados
│   └── ProductRepository.java
│
├── model/              # Entidades JPA
│   └── Product.java
│
├── dto/                # Data Transfer Objects
│   ├── request/
│   │   └── CreateProductRequest.java
│   └── response/
│       └── ProductResponse.java
│
├── exception/          # Exceções customizadas
│   ├── ProductNotFoundException.java
│   └── GlobalExceptionHandler.java
│
└── config/             # Configurações
    └── AppConfig.java
```

### Anotações Fundamentais

```java
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// COMPONENTES SPRING (Bean = objeto gerenciado pelo Spring)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Component          // Componente genérico
@Service            // Lógica de negócio
@Repository         // Acesso a dados
@Controller         // MVC Controller (retorna views)
@RestController     // REST Controller (retorna JSON)
@Configuration      // Classe de configuração

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// REST ENDPOINTS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@RequestMapping     // Base path
@GetMapping         // HTTP GET
@PostMapping        // HTTP POST
@PutMapping         // HTTP PUT
@DeleteMapping      // HTTP DELETE
@PatchMapping       // HTTP PATCH

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// REQUEST HANDLING
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@PathVariable       // Captura variável da URL: /products/{id}
@RequestParam       // Query parameter: /products?name=laptop
@RequestBody        // Corpo da requisição (JSON)
@RequestHeader      // Header HTTP

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// VALIDAÇÃO
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Valid              // Valida objeto
@NotNull            // Campo não pode ser null
@NotBlank           // String não pode ser vazia/blank
@Size               // Tamanho min/max
@Min / @Max         // Valor mínimo/máximo
@Email              // Valida formato de email
@Pattern            // Regex pattern

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// JPA
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Entity             // Entidade JPA
@Table              // Nome da tabela
@Id                 // Primary key
@GeneratedValue     // Auto-increment
@Column             // Customiza coluna
@ManyToOne / @OneToMany / @ManyToMany  // Relacionamentos
```

---

## SLIDE 12: Primeira API REST - Hands-on! (13:45 - 14:30)

### 🎯 Objetivo
Criar API completa para gerenciar produtos (CRUD)

### Passo 1: Criar a Entidade

```java
// src/main/java/com/example/products/model/Product.java
package com.example.products.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(length = 50)
    private String category;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Construtores
    public Product() {}
    
    public Product(String name, String description, BigDecimal price, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

### Passo 2: Criar o Repository

```java
// src/main/java/com/example/products/repository/ProductRepository.java
package com.example.products.repository;

import com.example.products.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Spring Data JPA cria implementação automaticamente! 🎉
    
    // Métodos derivados do nome (query methods)
    List<Product> findByCategory(String category);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    boolean existsByName(String name);
}
```

### Passo 3: Criar DTOs

```java
// src/main/java/com/example/products/dto/request/CreateProductRequest.java
package com.example.products.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    String description,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    BigDecimal price,
    
    @Size(max = 50, message = "Category must be less than 50 characters")
    String category
    
) {}

// src/main/java/com/example/products/dto/response/ProductResponse.java
package com.example.products.dto.response;

import com.example.products.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String category,
    LocalDateTime createdAt
) {
    // Factory method
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory(),
            product.getCreatedAt()
        );
    }
}
```

### Passo 4: Criar o Service

```java
// src/main/java/com/example/products/service/ProductService.java
package com.example.products.service;

import com.example.products.dto.request.CreateProductRequest;
import com.example.products.dto.response.ProductResponse;
import com.example.products.model.Product;
import com.example.products.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    
    private final ProductRepository repository;
    
    // Constructor injection
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return repository.findAll()
            .stream()
            .map(ProductResponse::from)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        return ProductResponse.from(product);
    }
    
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        // Validar se já existe
        if (repository.existsByName(request.name())) {
            throw new RuntimeException("Product with name '" + request.name() + "' already exists");
        }
        
        // Criar entidade
        Product product = new Product(
            request.name(),
            request.description(),
            request.price(),
            request.category()
        );
        
        // Salvar
        Product saved = repository.save(product);
        
        return ProductResponse.from(saved);
    }
    
    @Transactional
    public ProductResponse update(Long id, CreateProductRequest request) {
        Product product = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Atualizar campos
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        
        // Save não é necessário (managed entity), mas é boa prática
        Product updated = repository.save(product);
        
        return ProductResponse.from(updated);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        repository.deleteById(id);
    }
}
```

### Passo 5: Criar o Controller

```java
// src/main/java/com/example/products/controller/ProductController.java
package com.example.products.controller;

import com.example.products.dto.request.CreateProductRequest;
import com.example.products.dto.response.ProductResponse;
import com.example.products.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService service;
    
    public ProductController(ProductService service) {
        this.service = service;
    }
    
    // GET /api/products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        List<ProductResponse> products = service.findAll();
        return ResponseEntity.ok(products);
    }
    
    // GET /api/products/123
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        ProductResponse product = service.findById(id);
        return ResponseEntity.ok(product);
    }
    
    // POST /api/products
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // PUT /api/products/123
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request) {
        
        ProductResponse updated = service.update(id, request);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE /api/products/123
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## SLIDE 13: Testando a API (14:30 - 15:00)

### 🎬 DEMO: Testando com Postman

#### 1️⃣ Iniciar aplicação
```bash
./mvnw spring-boot:run
```

#### 2️⃣ Criar produto (POST)
```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Laptop Gaming",
  "description": "High-end gaming laptop with RTX 4080",
  "price": 7500.00,
  "category": "Electronics"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Laptop Gaming",
  "description": "High-end gaming laptop with RTX 4080",
  "price": 7500.00,
  "category": "Electronics",
  "createdAt": "2026-02-03T14:35:22.123"
}
```

#### 3️⃣ Listar todos (GET)
```http
GET http://localhost:8080/api/products
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Laptop Gaming",
    "description": "High-end gaming laptop with RTX 4080",
    "price": 7500.00,
    "category": "Electronics",
    "createdAt": "2026-02-03T14:35:22.123"
  }
]
```

#### 4️⃣ Buscar por ID (GET)
```http
GET http://localhost:8080/api/products/1
```

#### 5️⃣ Atualizar (PUT)
```http
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
  "name": "Laptop Gaming Pro",
  "description": "Ultimate gaming laptop with RTX 4090",
  "price": 9500.00,
  "category": "Electronics"
}
```

#### 6️⃣ Deletar (DELETE)
```http
DELETE http://localhost:8080/api/products/1
```

**Response (204 No Content)**

### 🐞 Debugging - Ver logs no console

```bash
# SQL executado
Hibernate: insert into products (category,created_at,description,name,price,updated_at) values (?,?,?,?,?,?)

# Request recebido
2026-02-03 14:35:22.456 DEBUG --- [nio-8080-exec-1] c.e.p.controller.ProductController : Creating product: Laptop Gaming
```

### 🔍 H2 Console - Ver banco de dados

1. Acessar: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:testdb`
3. User: `sa`
4. Password: (deixar vazio)

```sql
-- Ver todos os produtos
SELECT * FROM PRODUCTS;

-- Inserir via SQL
INSERT INTO PRODUCTS (NAME, DESCRIPTION, PRICE, CATEGORY, CREATED_AT, UPDATED_AT)
VALUES ('Mouse', 'Gaming mouse', 250.00, 'Electronics', NOW(), NOW());
```

---

## SLIDE 14: Profiles - Dev vs Prod (15:00 - 15:15)

### Múltiplos Ambientes

```
application.yml           # Configurações comuns
application-dev.yml       # Desenvolvimento
application-test.yml      # Testes
application-prod.yml      # Produção
```

```yaml
# application.yml (comum)
spring:
  application:
    name: products-api

---
# application-dev.yml (desenvolvimento)
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  h2:
    console:
      enabled: true
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop

logging:
  level:
    root: DEBUG

---
# application-prod.yml (produção)
spring:
  datasource:
    url: jdbc:postgresql://prod-server:5432/products_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate  # NUNCA use create-drop em prod!

logging:
  level:
    root: INFO
```

### Ativar profile

```bash
# Opção 1: Linha de comando
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Opção 2: application.yml
spring:
  profiles:
    active: dev

# Opção 3: Variável de ambiente
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run

# Opção 4: IDE (IntelliJ)
Run → Edit Configurations → Active Profiles: dev
```

### Beans específicos por profile

```java
@Configuration
public class AppConfig {
    
    @Bean
    @Profile("dev")
    public CommandLineRunner loadData(ProductRepository repo) {
        return args -> {
            // Carregar dados de teste apenas em dev
            repo.save(new Product("Laptop", "Test", BigDecimal.valueOf(1000), "Electronics"));
            repo.save(new Product("Mouse", "Test", BigDecimal.valueOf(50), "Electronics"));
            System.out.println("✅ Test data loaded!");
        };
    }
    
    @Bean
    @Profile("prod")
    public SomeService prodService() {
        return new ProductionService();
    }
}
```

---

## SLIDE 15: Spring Boot DevTools (15:15 - 15:30)

### Hot Reload Automático! 🔥

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

**O que ele faz:**
✅ Restart automático ao salvar arquivo  
✅ LiveReload no browser  
✅ Configurações otimizadas para dev  
✅ Cache desabilitado  

**🎬 DEMO:**
1. Adicionar DevTools
2. Mudar um controller
3. Salvar (Ctrl+S)
4. Aplicação reinicia automaticamente (~2s)

### Configurar no IntelliJ

```
Settings → Build, Execution, Deployment → Compiler
✅ Build project automatically

Settings → Advanced Settings
✅ Allow auto-make to start even if developed application is currently running
```

---

## SLIDE 16: Review e Q&A (15:30 - 16:00)

### ✅ O que aprendemos hoje

```
✓ Java Moderno (17/21)
  ✓ Records (DTOs imutáveis)
  ✓ Sealed Classes (hierarquias controladas)
  ✓ Text Blocks (strings multilinha)
  ✓ Pattern Matching (instanceof e switch)
  ✓ Stream API (programação funcional)
  ✓ Optional (lidar com null)

✓ Lombok vs Records
  ✓ Records são preferíveis para DTOs
  ✓ Lombok ainda útil para entidades JPA
  ✓ @Slf4j conveniente para logging

✓ Spring Boot
  ✓ IoC e DI (Inversion of Control, Dependency Injection)
  ✓ Auto-configuração
  ✓ Starters
  ✓ Profiles (dev, test, prod)

✓ Primeira API REST
  ✓ Controller (endpoints)
  ✓ Service (lógica de negócio)
  ✓ Repository (acesso a dados)
  ✓ Entity (modelo JPA)
  ✓ DTOs (Request/Response)
  ✓ Validação (@Valid)
```

### 🤔 Perguntas Comuns

**Q: Quando usar Records vs Classes?**  
A: Records para DTOs imutáveis. Classes para entidades JPA ou quando precisa mutabilidade.

**Q: @Autowired é obrigatório?**  
A: Não! Constructor injection não precisa (recomendado). Field/Setter injection precisam.

**Q: DDL-auto create-drop é seguro?**  
A: NUNCA em produção! Só dev/test. Use `validate` em prod.

**Q: Como debugar aplicação Spring?**  
A: Logs, breakpoints, Spring Boot Actuator (dia 9).

**Q: Preciso saber XML?**  
A: Não mais! Spring Boot usa annotations e YAML.

### 📝 Checklist de Aprendizado

```
[ ] Sei criar Records com validação
[ ] Entendo diferença entre Spring e Spring Boot
[ ] Sei o que é IoC e DI
[ ] Consigo criar projeto no Spring Initializr
[ ] Entendo estrutura de pastas do projeto
[ ] Sei criar Entity, Repository, Service, Controller
[ ] Entendo anotações básicas (@RestController, @Service, etc)
[ ] Consigo testar API com Postman
[ ] Sei configurar profiles
```

---

---

## 💻 Exercícios Práticos

### 🎯 Exercício 1: Java Moderno (30min)

**Objetivo:** Praticar Records, Stream API e Optional
Crie uma aplicação console que demonstre:
- Records para representar Produto (id, nome, preço, categoria)
- Stream API para filtrar e transformar lista de produtos
- Optional para buscar produto por ID

### Exercício 2: Primeira API (1h)
**Tarefa:** Criar API REST completa para gerenciar **Tarefas** (Todo List)

#### Requisitos:

**1. Criar novo projeto Spring Boot**
- Dependencies: Web, Validation, DevTools, H2, JPA

**2. Endpoints necessários:**
```http
GET    /api/tasks          # Lista todas as tarefas
GET    /api/tasks/{id}     # Busca tarefa por ID
POST   /api/tasks          # Cria nova tarefa
PUT    /api/tasks/{id}     # Atualiza tarefa
DELETE /api/tasks/{id}     # Remove tarefa
```

**3. Modelo de dados:**
```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    
    private boolean completed;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Getters, Setters, @PrePersist
}
```

**4. DTOs:**
```java
// Request
public record CreateTaskRequest(
    @NotBlank String title,
    String description
) {}

// Response
public record TaskResponse(
    Long id,
---

## 🏠 Tarefa de Casa

### 📚 Para Praticar (Obrigatório)

1. **Completar Exercício 2** (se não terminou)
   - Todos os endpoints funcionando
   - Testes com Postman documentados

2. **Estender a API de Tarefas**:
   ```java
   // Adicionar enum Priority
   public enum Priority { LOW, MEDIUM, HIGH }
   
   // Adicionar em Task
   @Enumerated(EnumType.STRING)
   private Priority priority;
   
   // Novo endpoint
   GET /api/tasks/search?status=completed&priority=HIGH
   ```

3. **Experimentar com Records**:
   - Criar 3 Records diferentes para seu domínio favorito
   - Implementar métodos customizados
   - Usar compact constructor com validação

### 📖 Para Ler (Complementar)

1. **Artigos**:
   - [ ] [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/) - Seções 1-3
   - [ ] [Effective Java - Item 16: In public classes, use accessor methods, not public fields](https://www.oreilly.com/library/view/effective-java/9780134686097/)
   - [ ] [Modern Java in Action - Chapter 3: Lambda Expressions](https://www.manning.com/books/modern-java-in-action)

2. **Vídeos** (YouTube):
   - [ ] Spring Boot Tutorial for Beginners (Amigoscode)
   - [ ] Java Records Explained
   - [ ] Dependency Injection Explained

### 🔧 Para Preparar (Dia 2)

1. **Banco de Dados**:
   ```bash
   # Opção 1: Instalar PostgreSQL
   # Windows: https://www.postgresql.org/download/windows/
   # Mac: brew install postgresql
   
   # Opção 2: Docker
   docker run --name postgres-dev -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15
   
   # Testar conexão
   psql -h localhost -U postgres
   ```

2. **Revisar SQL Básico**:
   ```sql
   -- CRUD
   SELECT * FROM products WHERE category = 'Electronics';
   INSERT INTO products (name, price) VALUES ('Mouse', 50.00);
   UPDATE products SET price = 45.00 WHERE id = 1;
   DELETE FROM products WHERE id = 1;
   
   -- Joins
   SELECT p.*, c.name as category_name
   FROM products p
   JOIN categories c ON p.category_id = c.id;
   
   -- Aggregations
   SELECT category, COUNT(*), AVG(price)
   FROM products
   GROUP BY category;
   ```

3. **Conceitos para estudar**:
   - [ ] O que são Relationships (OneToMany, ManyToOne, ManyToMany)
   - [ ] Diferença entre EAGER e LAZY loading
   - [ ] O que é um DTO e por que usar
   - [ ] Como funciona validação com Bean Validation

---

## 📝 Notas do Instrutor (Slides Extras
│   └── TaskController.java
├── service/
│   └── TaskService.java
├── repository/
│   └── TaskRepository.java
├── model/
│   └── Task.java
└── dto/
    ├── CreateTaskRequest.java
    └── TaskResponse.java
```

**6. Validações:**
- Title não pode ser vazio
- Title deve ter entre 3 e 100 caracteres
- Retornar 404 se task não existir
- Retornar 400 se validação falhar

**7. Testar com Postman:**
```bash
# Criar tarefa
POST http://localhost:8080/api/tasks
{
  "title": "Estudar Java 17",
  "description": "Aprender Records e Pattern Matching"
}

# Listar todas
GET http://localhost:8080/api/tasks

# Buscar por ID
GET http://localhost:8080/api/tasks/1

# Atualizar
PUT http://localhost:8080/api/tasks/1
{
  "title": "Estudar Java 21",
  "description": "Aprender Virtual Threads também"
}

# Deletar
DELETE http://localhost:8080/api/tasks/1
```

**Critérios de Avaliação:**
- ✅ Todos os endpoints funcionando
- ✅ DTOs usando Records
- ✅ Validação implementada
- ✅ Repository usando Spring Data JPA
- ✅ Service com lógica de negócio
- ✅ Controller com tratamento de erros
- ✅ Testado com Postman (screenshots/prints)

**🎁 Bônus (opcional):**
- Adicionar campo `priority` (LOW, MEDIUM, HIGH)
- Endpoint `GET /api/tasks/completed` para listar apenas completas
- Endpoint `PATCH /api/tasks/{id}/complete` para marcar como completa
- Exception handling global (@ControllerAdvice)

## 📚 Material de Estudo

### Leitura Obrigatória
- [What's New in Java 17](https://www.oracle.com/java/technologies/javase/17-relnote-issues.html)
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)

### Leitura Complementar
- [Java Records Tutorial](https://www.baeldung.com/java-record-keyword)
- [Spring Boot Annotations](https://www.baeldung.com/spring-boot-annotations)
- [Lombok vs Records](https://www.baeldung.com/java-record-vs-lombok)

### Vídeos
- [Java 17 Features Overview](https://www.youtube.com/results?search_query=java+17+features)
- [Spring Boot Tutorial for Beginners](https://www.youtube.com/results?search_query=spring+boot+tutorial)

## 🎯 Objetivos de Aprendizagem

Ao final deste dia, você deve ser capaz de:

- ✅ Utilizar recursos modernos do Java 17/21
- ✅ Explicar os conceitos de IoC e DI
- ✅ Criar um projeto Spring Boot do zero
- ✅ Desenvolver endpoints REST básicos
- ✅ Testar APIs com ferramentas de requisição HTTP

## 🏠 Tarefa de Casa

1. **Estender a API de Tarefas**:
   - Adicionar campo de prioridade (BAIXA, MÉDIA, ALTA)
   - Endpoint para filtrar por status (completas/incompletas)
   - Endpoint para buscar por palavra-chave no título

2. **Pesquisa**:
   - Ler sobre as anotações @Component, @Service, @Repository
   - Entender o ciclo de vida de beans no Spring

3. **Preparação para Dia 2**:
   - Instalar PostgreSQL ou ter Docker pronto
   - Revisar SQL básico (SELECT, INSERT, UPDATE, DELETE)
� Apêndice: Lombok - Ainda Precisamos?

### O que é Lombok?

Lombok é uma biblioteca Java que automaticamente gera código boilerplate através de anotações:

```java
// COM Lombok
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
}

// SEM Lombok (antes do Java 14)
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    
    // Constructors
    public Product() {}
    
    public Product(Long id, String name, BigDecimal price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    
    // equals, hashCode, toString...
    // +50 linhas de código
}
```

### ⚠️ Disclaimer: Java Moderno Reduziu a Necessidade do Lombok

Com Java 14+ (Records) e outras melhorias, **a necessidade do Lombok diminuiu significativamente**:

#### Comparação: Lombok vs Records

```java
// LOMBOK (ainda útil para classes mutáveis)
@Data
@Builder
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
}

// JAVA 17+ RECORDS (melhor para DTOs imutáveis)
public record ProductDTO(
    Long id,
    String name,
    BigDecimal price
) {
    // Automático: constructor, getters, equals, hashCode, toString
}

// RECORDS com validação
public record ProductDTO(
    Long id,
    String name,
    BigDecimal price
) {
    // Compact constructor para validação
    public ProductDTO {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}

// RECORDS com métodos customizados
public record ProductDTO(
    Long id,
    String name,
    BigDecimal price
) {
    public boolean isExpensive() {
        return price.compareTo(BigDecimal.valueOf(1000)) > 0;
    }
    
    public ProductDTO applyDiscount(BigDecimal percentage) {
        BigDecimal discountedPrice = price.multiply(
            BigDecimal.ONE.subtract(percentage)
        );
        return new ProductDTO(id, name, discountedPrice);
    }
}
```

### Quando USAR Lombok (ainda faz sentido)

✅ **Entidades JPA mutáveis**
```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private BigDecimal price;
    
    @ManyToOne
    private Category category;
}
// Records não funcionam bem com JPA por serem imutáveis
```

✅ **Builders complexos**
```java
@Builder
@Data
public class OrderRequest {
    private Long customerId;
    private List<OrderItem> items;
    private Address shippingAddress;
    private Address billingAddress;
    private PaymentMethod paymentMethod;
    private String discountCode;
    private String notes;
}

// Uso
OrderRequest order = OrderRequest.builder()
    .customerId(123L)
    .items(List.of(item1, item2))
    .shippingAddress(address)
    .paymentMethod(PaymentMethod.CREDIT_CARD)
    .build();
```

✅ **Logging**
```java
@Slf4j // Gera: private static final Logger log = LoggerFactory.getLogger(...)
@Service
public class ProductService {
    public void doSomething() {
        log.info("Processing...");
        log.error("Error occurred", exception);
    }
}
```

### Quando NÃO USAR Lombok (prefira Java moderno)

❌ **DTOs (use Records)**
```java
// NÃO FAÇA ISSO
@Data
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
}

// FAÇA ISSO
public record ProductResponse(
    Long id,
    String name,
    BigDecimal price
) {}
```

❌ **Value Objects (use Records)**
```java
// NÃO FAÇA ISSO
@Value // Lombok imutável
public class Money {
    BigDecimal amount;
    Currency currency;
}

// FAÇA ISSO
public record Money(BigDecimal amount, Currency currency) {
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(amount.add(other.amount), currency);
    }
}
```

❌ **Classes simples (use Records ou código explícito)**
```java
// Código explícito é melhor que "mágica" para casos simples
public class Configuration {
    private final String host;
    private final int port;
    
    public Configuration(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public String getHost() { return host; }
    public int getPort() { return port; }
}
```

### Problemas do Lombok

1. **"Mágica" em tempo de compilação**: Pode dificultar debug
2. **Dependência da IDE**: Requer plugin instalado
3. **Não é padrão Java**: Nem todos os desenvolvedores conhecem
4. **Conflitos com outras bibliotecas**: Às vezes causa problemas
5. **Menos controle**: Não é óbvio o que está sendo gerado

### Resumo: Guia de Decisão

```
┌─────────────────────────────────────────────────────────┐
│         Preciso de uma classe Java...                   │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ├─ É imutável? ──────> SIM ──> USE RECORDS
                      │                      
                      ├─ É entidade JPA? ──> SIM ──> USE LOMBOK (@Getter/@Setter)
                      │                      
                      ├─ Precisa Builder? ─> SIM ──> USE LOMBOK (@Builder)
                      │                      
                      ├─ Precisa logging? ─> SIM ──> USE LOMBOK (@Slf4j)
                      │                      
                      └─ Outros casos ─────> AVALIE ──> Java puro pode ser melhor
```

### Conclusão

🎯 **Recomendação para 2026**:
- **Prefira Records** para DTOs e Value Objects (Java 17+)
- **Use Lombok seletivamente** para entidades JPA e builders
- **Evite @Data** - seja mais específico (@Getter, @Setter, etc)
- **Considere o custo**: adicionar dependência só se realmente necessário

**No restante deste treinamento, daremos preferência a Records e código Java moderno, usando Lombok apenas quando realmente agregar valor.**

## 📝 Notas do Instrutor

```
Pontos de atenção:
- Enfatizar diferença entre Spring e Spring Boot
- Mostrar como auto-configuração funciona
- Explicar quando usar Records vs Classes tradicionais
- IMPORTANTE: Demonstrar lado a lado Records vs Lombok
- Discutir imutabilidade e suas vantagens
- Demonstrar debugging de aplicação Spring Boot
- Mostrar DevTools para hot reload
- Alertar sobre uso excessivo de Lombok em projetos modernoso Spring Boot
- Mostrar DevTools para hot reload
```

## 🔗 Links Úteis

- [Spring Initializr](https://start.spring.io/)
- [Postman](https://www.postman.com/)
- [Insomnia](https://insomnia.rest/)
- [JDK 17 Download](https://adoptium.net/)
