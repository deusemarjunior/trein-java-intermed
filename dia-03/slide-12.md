# Slide 12: Refactoring — Técnicas e Ferramentas

**Horário:** 13:00 - 13:30

---

## � O que é Refactoring?

> "Refactoring é a disciplina de **reestruturar** código existente, mudando sua **estrutura interna** sem alterar seu **comportamento externo**."
> — **Martin Fowler**, *Refactoring* (2018, 2ª edição)

Não é reescrever. Não é adicionar funcionalidade. É **melhorar a qualidade** do que já existe.

### O que refactoring NÃO é

```mermaid
graph LR
    subgraph "✅ É Refactoring"
        R1["Renomear variável"]
        R2["Extrair método"]
        R3["Mover classe para outro pacote"]
        R4["Substituir if/else por Strategy"]
        R5["Remover código morto"]
    end

    subgraph "❌ NÃO é Refactoring"
        N1["Adicionar feature nova"]
        N2["Corrigir bug"]
        N3["Reescrever do zero"]
        N4["Otimizar performance"]
        N5["Migrar de framework"]
    end

    style R1 fill:#2ecc71,color:#fff
    style R2 fill:#2ecc71,color:#fff
    style R3 fill:#2ecc71,color:#fff
    style R4 fill:#2ecc71,color:#fff
    style R5 fill:#2ecc71,color:#fff
    style N1 fill:#e74c3c,color:#fff
    style N2 fill:#e74c3c,color:#fff
    style N3 fill:#e74c3c,color:#fff
    style N4 fill:#e74c3c,color:#fff
    style N5 fill:#e74c3c,color:#fff
```

---

## Catálogo de Técnicas de Refatoração

> Martin Fowler cataloga **mais de 60 técnicas** de refatoração. Veremos as mais usadas no dia a dia.

```mermaid
---
config:
  theme: default
  themeVariables:
    fontSize: 18px
  flowchart:
    nodeSpacing: 30
    rankSpacing: 60
    padding: 15
---
graph LR
    REF["🔧 Técnicas de<br/>Refactoring"]

    REF --> COMP["📦 Composição"]
    COMP --> EM["Extract Method<br/>⭐ mais usada!"]
    COMP --> EV["Extract Variable"]
    COMP --> IM["Inline Method"]

    REF --> MOV["🚚 Movimentação"]
    MOV --> MC["Move Method/Class"]
    MOV --> EC["Extract Class"]
    MOV --> HE["Hide Delegate"]

    REF --> SIMP["✂️ Simplificação"]
    SIMP --> RN["Rename<br/>⭐ segunda mais usada!"]
    SIMP --> RCO["Replace Conditional<br/>with Polymorphism"]
    SIMP --> RMN["Replace Magic Number<br/>with Constant"]

    REF --> ORG["🗂️ Organização<br/>de Dados"]
    ORG --> EPO["Encapsulate Field"]
    ORG --> RPO["Replace Primitive<br/>with Object"]

    style EM fill:#2ecc71,color:#fff,stroke-width:3px
    style RN fill:#2ecc71,color:#fff,stroke-width:3px
    style REF fill:#3498db,color:#fff,stroke-width:3px
    style COMP fill:#2980b9,color:#fff
    style MOV fill:#2980b9,color:#fff
    style SIMP fill:#2980b9,color:#fff
    style ORG fill:#2980b9,color:#fff
```

---

## Técnicas em Detalhe

### 1. Extract Method (mais usada!)

> **Quando usar:** Método longo, bloco de código com comentário explicando o que faz, código repetido.

```java
// ❌ ANTES: bloco de código com comentário explicando
public Order processOrder(OrderRequest request) {
    // Valida estoque de todos os itens
    for (var item : request.items()) {
        Product p = productRepo.findById(item.productId()).orElseThrow();
        if (p.getStock() < item.quantity()) {
            throw new InsufficientStockException(p.getName());
        }
    }
    // Calcula total do pedido
    BigDecimal total = BigDecimal.ZERO;
    for (var item : request.items()) {
        Product p = productRepo.findById(item.productId()).orElseThrow();
        total = total.add(p.getPrice().multiply(BigDecimal.valueOf(item.quantity())));
    }
    // ... mais 80 linhas
}

// ✅ DEPOIS: métodos com nomes descritivos (o comentário vira o nome!)
public Order processOrder(OrderRequest request) {
    validateStock(request.items());
    BigDecimal total = calculateTotal(request.items());
    // ... código limpo e legível
}

private void validateStock(List<OrderItemRequest> items) {
    for (var item : items) {
        Product p = productRepo.findById(item.productId()).orElseThrow();
        if (p.getStock() < item.quantity()) {
            throw new InsufficientStockException(p.getName());
        }
    }
}

private BigDecimal calculateTotal(List<OrderItemRequest> items) {
    return items.stream()
        .map(item -> {
            Product p = productRepo.findById(item.productId()).orElseThrow();
            return p.getPrice().multiply(BigDecimal.valueOf(item.quantity()));
        })
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

> **IntelliJ:** Selecione o bloco → `Ctrl+Alt+M` → digite o nome do método → Enter

---

### 2. Rename (segunda mais usada!)

> **Quando usar:** Nome não revela intenção, abreviações, nomes genéricos (`data`, `temp`, `result`).

```java
// ❌ ANTES
int d; // o que é "d"?
public void proc(List<String> l) { ... }
String aux = calculateSomething();
boolean f = checkCondition();

// ✅ DEPOIS
int daysSinceCreation;
public void processEmails(List<String> pendingEmails) { ... }
String formattedAddress = calculateFormattedAddress();
boolean isEligibleForDiscount = checkDiscountEligibility();
```

> **IntelliJ:** Cursor no nome → `Shift+F6` → novo nome → Enter  
> Renomeia em **TODOS** os lugares que usam (incluindo outros arquivos, testes, Javadoc).

---

### 3. Extract Class (separar responsabilidades)

```mermaid
graph LR
    subgraph "❌ ANTES: God Class"
        GC["OrderService<br/>───────────<br/>validateStock()<br/>validatePayment()<br/>validateAddress()<br/>calcSubtotal()<br/>calcShipping()<br/>calcDiscount()<br/>calcTax()<br/>calcTotal()<br/>sendEmail()<br/>sendSMS()<br/>sendPush()<br/>───────────<br/>30 métodos, 500+ linhas"]
    end

    subgraph "✅ DEPOIS: SRP"
        OS["OrderService<br/>───────────<br/>processOrder()<br/>(orquestração)"]
        OV["OrderValidation<br/>───────────<br/>validateStock()<br/>validatePayment()<br/>validateAddress()"]
        OC["OrderCalculation<br/>───────────<br/>calcSubtotal()<br/>calcShipping()<br/>calcDiscount()<br/>calcTax()<br/>calcTotal()"]
        ON["OrderNotification<br/>───────────<br/>sendEmail()<br/>sendSMS()<br/>sendPush()"]
    end

    GC -->|"Extract Class"| OS
    GC -->|"Extract Class"| OV
    GC -->|"Extract Class"| OC
    GC -->|"Extract Class"| ON

    OS -->|"usa"| OV
    OS -->|"usa"| OC
    OS -->|"usa"| ON

    style GC fill:#e74c3c,color:#fff
    style OS fill:#2ecc71,color:#fff
    style OV fill:#3498db,color:#fff
    style OC fill:#e67e22,color:#fff
    style ON fill:#9b59b6,color:#fff
```

---

### 4. Replace Conditional with Polymorphism (Strategy Pattern)

> **Quando usar:** Cadeia de `if/else` ou `switch` que tende a crescer com novas opções.

```java
// ❌ ANTES: cadeia de if/else que cresce a cada região nova
public BigDecimal calculateShipping(String region, BigDecimal weight) {
    if (region.equals("SUDESTE")) {
        return weight.multiply(new BigDecimal("5.00"));
    } else if (region.equals("SUL")) {
        return weight.multiply(new BigDecimal("7.00"));
    } else if (region.equals("NORDESTE")) {
        return weight.multiply(new BigDecimal("10.00"));
    } else if (region.equals("NORTE")) {
        return weight.multiply(new BigDecimal("12.00"));
    } else if (region.equals("CENTRO-OESTE")) {
        return weight.multiply(new BigDecimal("8.00"));
    } else {
        return weight.multiply(new BigDecimal("15.00"));
    }
}
```

```java
// ✅ DEPOIS: Strategy Pattern — adicionar região = criar classe, sem tocar no existente

// 1. Interface Strategy
public interface ShippingCalculator {
    BigDecimal calculate(BigDecimal weight);
    String getRegion();
}

// 2. Implementações
public class SudesteShipping implements ShippingCalculator {
    private static final BigDecimal RATE = new BigDecimal("5.00");
    public BigDecimal calculate(BigDecimal weight) { return weight.multiply(RATE); }
    public String getRegion() { return "SUDESTE"; }
}

// 3. Registry com Spring (ou Map)
@Component
public class ShippingCalculatorRegistry {
    private final Map<String, ShippingCalculator> calculators;

    public ShippingCalculatorRegistry(List<ShippingCalculator> list) {
        this.calculators = list.stream()
            .collect(Collectors.toMap(ShippingCalculator::getRegion, c -> c));
    }

    public ShippingCalculator getFor(String region) {
        return Optional.ofNullable(calculators.get(region))
            .orElseThrow(() -> new UnsupportedRegionException(region));
    }
}
```

```mermaid
graph TD
    subgraph "Open/Closed Principle"
        REG["ShippingCalculatorRegistry"]
        IF["ShippingCalculator<br/>«interface»"]
        SE["SudesteShipping"]
        SL["SulShipping"]
        NE["NordesteShipping"]
        NT["NorteShipping"]
        NEW["NovaRegiao?<br/>Só criar classe!"]

        REG --> IF
        IF --> SE
        IF --> SL
        IF --> NE
        IF --> NT
        IF -.-> NEW
    end

    style IF fill:#3498db,color:#fff
    style NEW fill:#f39c12,color:#fff,stroke-dasharray: 5 5
```

---

### 5. Replace Magic Number with Named Constant

```java
// ❌ ANTES
if (order.getTotal().compareTo(new BigDecimal("100")) > 0) {
    discount = order.getTotal().multiply(new BigDecimal("0.1"));
}
if (items.size() > 5) { throw new TooManyItemsException(); }
if (salary.compareTo(new BigDecimal("1412.00")) < 0) { ... }

// ✅ DEPOIS
private static final BigDecimal MINIMUM_ORDER_FOR_DISCOUNT = new BigDecimal("100.00");
private static final BigDecimal DEFAULT_DISCOUNT_RATE = new BigDecimal("0.10");
private static final int MAX_ITEMS_PER_ORDER = 5;
private static final BigDecimal MINIMUM_WAGE = new BigDecimal("1412.00");

if (order.getTotal().compareTo(MINIMUM_ORDER_FOR_DISCOUNT) > 0) {
    discount = order.getTotal().multiply(DEFAULT_DISCOUNT_RATE);
}
if (items.size() > MAX_ITEMS_PER_ORDER) { throw new TooManyItemsException(); }
if (salary.compareTo(MINIMUM_WAGE) < 0) { ... }
```

> **IntelliJ:** Selecione o número → `Ctrl+Alt+C` → nome da constante

---

## ⌨️ Atalhos do IntelliJ — Refactoring

| Atalho | Ação | Quando usar |
|--------|------|-------------|
| `Ctrl+Alt+M` | **Extract Method** | Bloco de código → método |
| `Shift+F6` | **Rename** | Nome ruim → nome bom |
| `Ctrl+Alt+N` | **Inline** | Desfaz extract (variável, método) |
| `Ctrl+Alt+V` | **Extract Variable** | Expressão complexa → variável nomeada |
| `Ctrl+Alt+C` | **Extract Constant** | Magic number → constante |
| `Ctrl+Alt+F` | **Extract Field** | Variável local → campo da classe |
| `Ctrl+Alt+P` | **Extract Parameter** | Valor hardcoded → parâmetro do método |
| `F6` | **Move** | Mover para outra classe/pacote |
| `Ctrl+Shift+Alt+T` | **Refactor This** | Menu com TODAS as opções |

> **Dica:** `Ctrl+Shift+Alt+T` abre um menu com TODAS as refatorações disponíveis no contexto.

---

## 🔴🟢 Ciclo Seguro de Refatoração

```mermaid
flowchart TD
    A["1. 🟢 mvn test → GREEN<br/>Todos os testes passam"] --> B["2. 🔧 Refatorar UMA coisa<br/>(Extract Method, Rename, etc.)"]
    B --> C["3. 🟢 mvn test → GREEN<br/>Todos os testes AINDA passam?"]
    C -->|"✅ Sim"| D["4. ✅ Commit!<br/>git commit -m 'refactor: ...'"]
    D --> A
    C -->|"❌ Não"| E["5. 🔙 Ctrl+Z<br/>Desfazer e tentar<br/>passos menores"]
    E --> A

    style A fill:#2ecc71,color:#fff
    style B fill:#f39c12,color:#fff
    style C fill:#2ecc71,color:#fff
    style D fill:#3498db,color:#fff
    style E fill:#e74c3c,color:#fff
```

> **Regra de ouro:** NUNCA refatore com testes quebrados. Se algo quebrar, desfaça (`Ctrl+Z`) e tente novamente com passos **menores**.

### Commits Semânticos para Refatoração

```bash
# Padrão Conventional Commits
git commit -m "refactor: extract validation logic to OrderValidationService"
git commit -m "refactor: rename proc() to processOrder()"
git commit -m "refactor: replace shipping if/else with Strategy pattern"
git commit -m "refactor: extract magic numbers to named constants"
```

---

## 📊 Resumo: Smell → Técnica

| Code Smell | Técnica de Refactoring | Atalho IntelliJ |
|-----------|------------------------|-----------------|
| **Long Method** | Extract Method | `Ctrl+Alt+M` |
| **Bad Name** | Rename | `Shift+F6` |
| **God Class** | Extract Class | `F6` (Move) |
| **Magic Number** | Extract Constant | `Ctrl+Alt+C` |
| **Duplicate Code** | Extract Method + reutilizar | `Ctrl+Alt+M` |
| **Feature Envy** | Move Method | `F6` |
| **Primitive Obsession** | Replace Primitive with Object | Manual |
| **if/else chain** | Replace Conditional with Polymorphism | Manual |

---

## 💡 Dica do Instrutor

Demonstrar Extract Method e Rename **AO VIVO** na IDE usando código do `03-bad-practices-lab`. Pedir para os alunos praticarem junto. Mostrar `Ctrl+Shift+Alt+T` como "menu mágico" de refactoring.
