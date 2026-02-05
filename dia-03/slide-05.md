# Slide 5: Open/Closed Principle (OCP)

---

## 📖 Definição

> **"Entidades de software devem estar abertas para extensão, mas fechadas para modificação"**
> 
> *— Bertrand Meyer*

```mermaid
graph LR
    A[Código Existente<br/>FECHADO] -->|não modifica| B[✅ Estável]
    C[Nova Funcionalidade<br/>ABERTO] -->|estende| A
    C -->|adiciona| D[✅ Novo Comportamento]
    
    style A fill:#4CAF50,stroke:#2E7D32,color:#fff
    style C fill:#2196F3,stroke:#1565C0,color:#fff
```

---

## ❌ Violação do OCP

```java
@Service
public class DiscountService {
    
    public BigDecimal calculateDiscount(Order order, String customerType) {
        BigDecimal discount = BigDecimal.ZERO;
        
        if ("VIP".equals(customerType)) {
            discount = order.getTotal().multiply(BigDecimal.valueOf(0.20));
        } else if ("REGULAR".equals(customerType)) {
            discount = order.getTotal().multiply(BigDecimal.valueOf(0.10));
        } else if ("PREMIUM".equals(customerType)) {
            discount = order.getTotal().multiply(BigDecimal.valueOf(0.30));
        }
        // ⚠️ Para adicionar novo tipo, preciso MODIFICAR este código!
        
        return discount;
    }
}
```

**Problema:** Cada novo tipo de cliente requer modificação da classe! 🔧

---

## 🔄 Evolução do Código

```mermaid
sequenceDiagram
    participant Dev1 as Dev 1
    participant Code as DiscountService
    participant Dev2 as Dev 2
    
    Note over Code: v1.0 - VIP e REGULAR
    Dev1->>Code: ❌ Adiciona PREMIUM
    Note over Code: v1.1 - Modifica if/else
    Dev2->>Code: ❌ Adiciona GOLD
    Note over Code: v1.2 - Modifica if/else
    Note over Code: ⚠️ Código cresce<br/>Risco de bugs<br/>Testes quebram
```

---

## ✅ Aplicando OCP com Strategy Pattern

```mermaid
classDiagram
    class DiscountStrategy {
        <<interface>>
        +calculate(BigDecimal) BigDecimal
    }
    
    class VipDiscount {
        +calculate(BigDecimal) BigDecimal
    }
    
    class RegularDiscount {
        +calculate(BigDecimal) BigDecimal
    }
    
    class PremiumDiscount {
        +calculate(BigDecimal) BigDecimal
    }
    
    class GoldDiscount {
        +calculate(BigDecimal) BigDecimal
    }
    
    class DiscountService {
        -strategies: Map
        +calculateDiscount(Order, String) BigDecimal
    }
    
    DiscountStrategy <|.. VipDiscount
    DiscountStrategy <|.. RegularDiscount
    DiscountStrategy <|.. PremiumDiscount
    DiscountStrategy <|.. GoldDiscount
    DiscountService --> DiscountStrategy
    
    style DiscountStrategy fill:#4CAF50,stroke:#2E7D32,color:#fff
    style DiscountService fill:#2196F3,stroke:#1565C0,color:#fff
```

---

## ✅ Código Refatorado

```java
// Interface - Contrato fechado
public interface DiscountStrategy {
    BigDecimal calculate(BigDecimal price);
}

// Implementações - Novas estratégias sem modificar código existente
@Component("VIP")
public class VipDiscount implements DiscountStrategy {
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.20));
    }
}

@Component("REGULAR")
public class RegularDiscount implements DiscountStrategy {
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.10));
    }
}

@Component("PREMIUM")
public class PremiumDiscount implements DiscountStrategy {
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.30));
    }
}

// ✅ NOVA ESTRATÉGIA - Sem modificar código existente!
@Component("GOLD")
public class GoldDiscount implements DiscountStrategy {
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.25));
    }
}

// Service - Fechado para modificação, usa as estratégias
@Service
@RequiredArgsConstructor
public class DiscountService {
    private final Map<String, DiscountStrategy> strategies;
    
    public BigDecimal calculateDiscount(Order order, String customerType) {
        DiscountStrategy strategy = strategies.get(customerType);
        return strategy != null ? strategy.calculate(order.getTotal()) : BigDecimal.ZERO;
    }
}
```

---

## 📊 Comparação: Antes vs Depois

| Aspecto | ❌ Sem OCP | ✅ Com OCP |
|---------|-----------|-----------|
| Adicionar novo tipo | Modifica código existente | Cria nova classe |
| Risco de bugs | Alto (código existente) | Baixo (código isolado) |
| Testes existentes | Precisam ser reexecutados | Permanecem válidos |
| Conflitos no Git | Frequentes | Raros |
| Complexidade | Cresce linear | Cresce modular |

---

## 🎯 Técnicas para OCP

```mermaid
mindmap
  root((OCP))
    Abstrações
      Interfaces
      Classes Abstratas
      Contratos
    Patterns
      Strategy
      Template Method
      Decorator
      Factory
    Polimorfismo
      Herança
      Implementação
      Composição
    Configuração
      Injeção de Dependência
      Spring Beans
      Properties
```

---

## 🔍 Quando Aplicar?

```mermaid
flowchart TD
    A[Nova Funcionalidade] --> B{Modifica código<br/>existente?}
    B -->|Sim| C{É provável ter<br/>mais variações?}
    B -->|Não| Z[✅ OK - Continue]
    C -->|Sim| D[⚠️ Aplicar OCP<br/>Criar abstração]
    C -->|Não| E{Código é<br/>crítico?}
    E -->|Sim| D
    E -->|Não| F[⚠️ Considere aplicar OCP<br/>ou documentar débito técnico]
    
    style D fill:#4CAF50,stroke:#2E7D32,color:#fff
    style Z fill:#4CAF50,stroke:#2E7D32,color:#fff
    style F fill:#FF9800,stroke:#F57C00,color:#fff
```

---

## 💡 Dica do Instrutor

```
⚠️ Cuidados:
- Não crie abstrações prematuras
- OCP tem custo: mais classes, mais complexidade
- Use quando houver evidência de variação futura
- "Fool me once, shame on you; fool me twice, shame on me"
  → Na primeira vez, pode ser if/else
  → Na segunda vez, refatore para OCP
```
