# Slide 6: Pattern Matching & Switch Expressions

**Horário:** 10:25 - 10:45

---

## Pattern Matching for instanceof

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
```

---

## Switch Expressions (Java 14+)

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
```

---

## Switch com Blocos

```java
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

---

## ✨ Pattern Matching for Switch (Java 21+)

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

---

## 🎬 DEMO COMPLETO

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
