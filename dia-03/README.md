# Dia 3 - Arquitetura e Design

**Duração**: 5 horas  
**Objetivo**: Compreender e aplicar princípios SOLID, Design Patterns e conceitos de DDD

## 📋 Conteúdo Programático

### Manhã (3 horas)

#### 1. Princípios SOLID (1.5h)

**S - Single Responsibility Principle**
- Uma classe deve ter apenas uma razão para mudar
- Coesão e separação de responsabilidades
- Exemplos práticos em Spring Boot

**O - Open/Closed Principle**
- Aberto para extensão, fechado para modificação
- Uso de interfaces e abstrações
- Strategy Pattern como exemplo

**L - Liskov Substitution Principle**
- Substituibilidade de tipos
- Contratos e invariantes
- Cuidados com herança

**I - Interface Segregation Principle**
- Interfaces específicas vs interfaces genéricas
- Evitar "fat interfaces"
- Aplicação em repositories e services

**D - Dependency Inversion Principle**
- Depender de abstrações, não implementações
- Injeção de dependência no Spring
- Inversão de controle

#### 2. Design Patterns Essenciais (1.5h)

**Strategy Pattern**
```java
// Exemplo: Estratégias de cálculo de desconto
public interface DiscountStrategy {
    BigDecimal calculate(BigDecimal price);
}

@Component
public class BlackFridayDiscount implements DiscountStrategy {
    public BigDecimal calculate(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.5));
    }
}

@Service
public class PriceService {
    public BigDecimal calculatePrice(BigDecimal price, DiscountStrategy strategy) {
        return strategy.calculate(price);
    }
}
```

**Factory Pattern**
```java
// Exemplo: Factory de notificações
public interface NotificationFactory {
    Notification create(NotificationType type);
}

@Component
public class NotificationFactoryImpl implements NotificationFactory {
    public Notification create(NotificationType type) {
        return switch(type) {
            case EMAIL -> new EmailNotification();
            case SMS -> new SmsNotification();
            case PUSH -> new PushNotification();
        };
    }
}
```

**Builder Pattern**
```java
// Exemplo: Builder para objetos complexos
public class Order {
    private final Long id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final BigDecimal total;
    private final OrderStatus status;
    
    private Order(Builder builder) {
        this.id = builder.id;
        this.customer = builder.customer;
        this.items = builder.items;
        this.total = builder.total;
        this.status = builder.status;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        // campos e métodos
    }
}
```

**Singleton Pattern**
- Já implementado pelo Spring (@Component, @Service)
- Scopes: Singleton, Prototype, Request, Session

### Tarde (2 horas)

#### 3. Domain-Driven Design - Conceitos (1h)

**Ubiquitous Language**
- Linguagem compartilhada entre técnicos e negócio
- Nomenclatura consistente no código

**Building Blocks Táticos**
- **Entities**: Objetos com identidade (id)
- **Value Objects**: Objetos sem identidade, imutáveis
- **Aggregates**: Conjunto de entidades tratadas como unidade
- **Repositories**: Acesso a aggregates
- **Services**: Lógica que não pertence a entidades
- **Domain Events**: Eventos de negócio

**Exemplo prático**:
```java
// Entity
@Entity
public class Order {
    @Id
    private OrderId id;
    private Customer customer;
    private List<OrderItem> items;
    private Money totalAmount;
    
    public void addItem(Product product, Quantity quantity) {
        // lógica de domínio
    }
}

// Value Object
public record Money(BigDecimal amount, Currency currency) {
    public Money add(Money other) {
        // validar moeda
        return new Money(amount.add(other.amount), currency);
    }
}

// Repository (interface no domínio)
public interface OrderRepository {
    Order findById(OrderId id);
    void save(Order order);
}
```

#### 4. Arquitetura em Camadas (1h)

**Arquitetura tradicional em 3 camadas**
```
┌─────────────────────────┐
│   Presentation Layer    │  Controllers, DTOs
├─────────────────────────┤
│    Business Layer       │  Services, Domain
├─────────────────────────┤
│  Data Access Layer      │  Repositories, Entities
└─────────────────────────┘
```

**Problemas e limitações**
- Acoplamento com framework
- Dificuldade de testes
- Lógica de negócio espalhada

**Introdução à Arquitetura Hexagonal**
```
         ┌─────────────────┐
         │   Application   │
         │  (Use Cases)    │
         └────────┬────────┘
                  │
    ┌─────────────┼─────────────┐
    │                           │
┌───▼────┐                 ┌───▼────┐
│ Ports  │                 │ Ports  │
│(Input) │                 │(Output)│
└───┬────┘                 └───┬────┘
    │                          │
┌───▼────┐                 ┌───▼────┐
│Adapters│                 │Adapters│
│  REST  │                 │   DB   │
└────────┘                 └────────┘
```

**Vantagens**:
- Domínio isolado e testável
- Independência de framework
- Flexibilidade para mudar adapters

## 💻 Exercícios Práticos

### Exercício 1: Refatoração com SOLID (1h)

Dado um código que viola SOLID, refatore-o:

```java
// CÓDIGO PROBLEMÁTICO
@Service
public class OrderService {
    public void processOrder(Order order) {
        // Valida
        if (order.getItems().isEmpty()) throw new Exception();
        
        // Calcula desconto
        if (order.getCustomer().isVip()) {
            order.setTotal(order.getTotal() * 0.9);
        }
        
        // Salva no banco
        // Envia email
        // Envia SMS
        // Atualiza estoque
    }
}
```

**Tarefa**: Refatore aplicando:
- SRP: Separar responsabilidades
- OCP: Usar Strategy para descontos
- DIP: Injetar dependências

### Exercício 2: Design Patterns (1h)

Implemente um sistema de processamento de pagamentos usando:

1. **Factory Pattern** para criar processadores:
   - CreditCardProcessor
   - PixProcessor
   - BoletoProcessor

2. **Strategy Pattern** para validação:
   - Different validation rules per payment type

3. **Builder Pattern** para criar Payment object

```java
public interface PaymentProcessor {
    PaymentResult process(Payment payment);
}

public interface PaymentFactory {
    PaymentProcessor create(PaymentType type);
}
```

### Exercício 3: DDD Modeling (1h)

Modele o domínio de um **Sistema de Reservas de Hotel**:

**Requisitos**:
- Identifique Entities, Value Objects e Aggregates
- Defina o Aggregate Root
- Crie repository interfaces
- Implemente regras de negócio no domínio

**Conceitos para modelar**:
- Reservation (aggregate root)
- Room (entity)
- Guest (entity)
- DateRange (value object)
- Money (value object)
- ReservationStatus (enum)

## 📚 Material de Estudo

### Leitura Obrigatória
- [SOLID Principles](https://www.baeldung.com/solid-principles)
- [Design Patterns in Spring](https://www.baeldung.com/spring-framework-design-patterns)
- [DDD Reference](https://www.domainlanguage.com/ddd/reference/)

### Leitura Complementar
- "Clean Architecture" - Robert C. Martin
- "Domain-Driven Design" - Eric Evans
- [Refactoring Guru - Design Patterns](https://refactoring.guru/design-patterns)

### Vídeos
- [SOLID Principles Explained](https://www.youtube.com/results?search_query=solid+principles+java)
- [DDD in Practice](https://www.youtube.com/results?search_query=domain+driven+design)

## 🎯 Objetivos de Aprendizagem

Ao final deste dia, você deve ser capaz de:

- ✅ Aplicar os 5 princípios SOLID no código
- ✅ Identificar e implementar Design Patterns apropriados
- ✅ Distinguir Entities de Value Objects
- ✅ Modelar domínio seguindo DDD
- ✅ Entender diferenças entre arquiteturas em camadas

## 🏠 Tarefa de Casa

1. **Refatoração**:
   - Pegue a API de Tasks/Blog dos dias anteriores
   - Identifique violações de SOLID
   - Refatore aplicando os princípios

2. **Estudar**:
   - Ler sobre outros patterns: Adapter, Decorator, Observer
   - Pesquisar sobre Bounded Contexts em DDD
   - Entender Anemic Domain Model vs Rich Domain Model

3. **Preparação para Dia 4**:
   - Revisar conceitos de Ports & Adapters
   - Ler sobre Clean Architecture
   - Entender Use Cases

## 📝 Notas do Instrutor

```
Pontos de atenção:
- Usar exemplos do dia-a-dia para explicar SOLID
- Demonstrar código antes/depois da refatoração
- Não exagerar em patterns (YAGNI principle)
- Enfatizar que DDD é sobre modelagem, não tecnologia
- Mostrar quando NÃO usar certos patterns
- Discussão: quando usar Value Objects vs Entities
```

## 🔗 Links Úteis

- [Refactoring Guru](https://refactoring.guru/)
- [DDD Community](https://github.com/ddd-crew)
- [Martin Fowler's Blog](https://martinfowler.com/)
- [Clean Coders](https://cleancoders.com/)
