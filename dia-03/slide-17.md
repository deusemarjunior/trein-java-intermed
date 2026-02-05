# Slide 17: Arquitetura em Camadas

**Horário:** 14:30 - 15:30

---

## 🏛️ Arquitetura Tradicional em 3 Camadas

```mermaid
graph TB
    subgraph "Presentation Layer"
        C[Controllers<br/>REST APIs<br/>Views]
    end
    
    subgraph "Business Layer"
        S[Services<br/>Business Logic<br/>Domain]
    end
    
    subgraph "Data Access Layer"
        R[Repositories<br/>DAOs<br/>Entities]
    end
    
    DB[(Database)]
    
    C -->|usa| S
    S -->|usa| R
    R -->|acessa| DB
    
    style C fill:#2196F3,stroke:#1565C0,color:#fff
    style S fill:#4CAF50,stroke:#2E7D32,color:#fff
    style R fill:#FF9800,stroke:#F57C00,color:#fff
```

---

## 📦 Estrutura de Pacotes (Layered)

```
src/main/java/com/example/
├── presentation/          # Camada de apresentação
│   ├── controller/
│   │   ├── OrderController.java
│   │   └── ProductController.java
│   └── dto/
│       ├── OrderDTO.java
│       └── ProductDTO.java
│
├── business/              # Camada de negócio
│   ├── service/
│   │   ├── OrderService.java
│   │   └── ProductService.java
│   └── validator/
│       └── OrderValidator.java
│
└── data/                  # Camada de dados
    ├── repository/
    │   ├── OrderRepository.java
    │   └── ProductRepository.java
    └── entity/
        ├── OrderEntity.java
        └── ProductEntity.java
```

---

## ✅ Vantagens

```mermaid
mindmap
  root((Layered))
    Simplicidade
      Fácil entender
      Padrão conhecido
      Onboarding rápido
    Separação
      Concerns separados
      Responsabilidades claras
    Desenvolvimento
      Equipes por camada
      Paralelização
```

---

## ❌ Problemas

```mermaid
graph TD
    A[Problemas da Arquitetura em Camadas] --> B[Acoplamento com Framework]
    A --> C[Lógica Espalhada]
    A --> D[Dificulta Testes]
    A --> E[Database-Centric]
    
    B --> B1[Spring em todo lugar<br/>JPA Entities no domínio]
    C --> C1[Validação em múltiplas camadas<br/>Business logic vazando]
    D --> D1[Precisa de banco<br/>Testes lentos]
    E --> E1[Design baseado em tabelas<br/>Não no domínio]
    
    style A fill:#f44336,stroke:#c62828,color:#fff
```

---

## 🔄 Fluxo de Dependências

### ❌ Problema: Dependências apontam para baixo

```mermaid
graph TD
    UI[Presentation<br/>Controllers] -->|depende| BL[Business<br/>Services]
    BL -->|depende| DAL[Data Access<br/>Repositories]
    DAL -->|depende| DB[(Database<br/>JPA/JDBC)]
    
    Note1[❌ Domínio depende<br/>de infraestrutura]
    Note2[❌ Viola Dependency<br/>Inversion Principle]
    
    style DAL fill:#f44336,stroke:#c62828,color:#fff
    style DB fill:#f44336,stroke:#c62828,color:#fff
```

---

## 🏗️ Arquitetura Hexagonal (Ports & Adapters)

```mermaid
graph TB
    subgraph "Adapters (Driving)"
        REST[REST API]
        WEB[Web UI]
        CLI[CLI]
    end
    
    subgraph "Core (Application + Domain)"
        APP[Application<br/>Use Cases]
        DOM[Domain<br/>Business Logic]
        
        subgraph "Ports"
            IN[Input Ports<br/>Interfaces]
            OUT[Output Ports<br/>Interfaces]
        end
    end
    
    subgraph "Adapters (Driven)"
        DB[Database]
        CACHE[Cache]
        EMAIL[Email]
        EXT[External API]
    end
    
    REST -->|usa| IN
    WEB -->|usa| IN
    CLI -->|usa| IN
    
    IN --> APP
    APP --> DOM
    APP -->|usa| OUT
    
    DB -.implementa.-> OUT
    CACHE -.implementa.-> OUT
    EMAIL -.implementa.-> OUT
    EXT -.implementa.-> OUT
    
    style DOM fill:#4CAF50,stroke:#2E7D32,color:#fff,stroke-width:4px
    style APP fill:#4CAF50,stroke:#2E7D32,color:#fff
    style IN fill:#2196F3,stroke:#1565C0,color:#fff
    style OUT fill:#2196F3,stroke:#1565C0,color:#fff
```

---

## 🎯 Conceitos da Arquitetura Hexagonal

```mermaid
mindmap
  root((Hexagonal))
    Core
      Domain isolado
      Sem dependências externas
      Testável
    Ports
      Input Driving
      Output Driven
      Interfaces
    Adapters
      Primary Driving
      Secondary Driven
      Implementações
    Inversão
      Core não depende de infra
      Infra depende de Core
```

---

## 📦 Estrutura de Pacotes (Hexagonal)

```
src/main/java/com/example/
├── domain/                         # 🟢 Core - Domain
│   ├── model/
│   │   ├── Order.java              # Entity
│   │   ├── Money.java              # Value Object
│   │   └── OrderStatus.java        # Enum
│   ├── service/
│   │   └── OrderPricingService.java  # Domain Service
│   └── exception/
│       └── OrderException.java
│
├── application/                    # 🟢 Core - Application
│   ├── port/
│   │   ├── in/                     # Input Ports (Driving)
│   │   │   ├── CreateOrderUseCase.java
│   │   │   └── FindOrderUseCase.java
│   │   └── out/                    # Output Ports (Driven)
│   │       ├── OrderRepository.java
│   │       ├── PaymentGateway.java
│   │       └── EmailService.java
│   └── service/
│       └── OrderApplicationService.java
│
└── infrastructure/                 # 🔵 Adapters
    ├── adapter/
    │   ├── in/                     # Primary Adapters (Driving)
    │   │   ├── rest/
    │   │   │   └── OrderController.java
    │   │   └── cli/
    │   │       └── OrderCLI.java
    │   └── out/                    # Secondary Adapters (Driven)
    │       ├── persistence/
    │       │   └── JpaOrderRepository.java
    │       ├── payment/
    │       │   └── StripePaymentGateway.java
    │       └── email/
    │           └── SmtpEmailService.java
    └── config/
        └── BeanConfiguration.java
```

---

## 🔌 Ports (Interfaces)

### Input Port (Driving)

```java
// application/port/in/CreateOrderUseCase.java
package com.example.application.port.in;

public interface CreateOrderUseCase {
    OrderResponse createOrder(CreateOrderCommand command);
}

// Command (DTO de entrada)
public record CreateOrderCommand(
    Long customerId,
    List<OrderItemRequest> items,
    Address shippingAddress
) {}
```

### Output Port (Driven)

```java
// application/port/out/OrderRepository.java
package com.example.application.port.out;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findByCustomer(Long customerId);
}

// application/port/out/PaymentGateway.java
public interface PaymentGateway {
    PaymentResult processPayment(Order order, PaymentDetails details);
}
```

---

## 🔧 Adapters (Implementações)

### Primary Adapter (REST)

```java
// infrastructure/adapter/in/rest/OrderController.java
package com.example.infrastructure.adapter.in.rest;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final CreateOrderUseCase createOrderUseCase;
    private final FindOrderUseCase findOrderUseCase;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request) {
        
        CreateOrderCommand command = new CreateOrderCommand(
            request.customerId(),
            request.items(),
            request.shippingAddress()
        );
        
        OrderResponse response = createOrderUseCase.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return findOrderUseCase.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
```

### Secondary Adapter (Database)

```java
// infrastructure/adapter/out/persistence/JpaOrderRepository.java
package com.example.infrastructure.adapter.out.persistence;

@Repository
@RequiredArgsConstructor
public class JpaOrderRepositoryAdapter implements OrderRepository {
    
    private final SpringDataOrderRepository springDataRepo;
    private final OrderMapper mapper;
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = springDataRepo.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Order> findById(Long id) {
        return springDataRepo.findById(id)
            .map(mapper::toDomain);
    }
}

// Spring Data JPA interface (interno ao adapter)
interface SpringDataOrderRepository extends JpaRepository<OrderEntity, Long> {}
```

---

## 🔄 Fluxo Completo

```mermaid
sequenceDiagram
    participant Client
    participant Controller as REST Controller<br/>(Primary Adapter)
    participant UseCase as CreateOrderUseCase<br/>(Input Port)
    participant Service as OrderApplicationService<br/>(Implementation)
    participant Domain as Order<br/>(Domain Model)
    participant Repo as OrderRepository<br/>(Output Port)
    participant DB as JpaOrderRepository<br/>(Secondary Adapter)
    
    Client->>Controller: POST /api/orders
    Controller->>Controller: Cria Command
    Controller->>UseCase: createOrder(command)
    UseCase->>Service: createOrder(command)
    Service->>Domain: Order.create()
    Domain-->>Service: order
    Service->>Repo: save(order)
    Repo->>DB: save(order)
    DB->>DB: Converte para Entity
    DB->>DB: Salva no banco
    DB-->>Repo: order salvo
    Repo-->>Service: order
    Service-->>UseCase: response
    UseCase-->>Controller: response
    Controller-->>Client: 201 Created
```

---

## 📊 Comparação: Layered vs Hexagonal

| Aspecto | Layered | Hexagonal |
|---------|---------|-----------|
| Dependências | Top → Bottom | Outside → Inside |
| Domínio | Acoplado com infra | Isolado |
| Testabilidade | Difícil (precisa DB) | Fácil (mocks) |
| Flexibilidade | Baixa | Alta |
| Complexidade | Baixa | Média |
| Manutenção | Difícil (código espalhado) | Fácil (isolado) |
| Framework | Acoplado | Desacoplado |

---

## 🎯 Benefícios da Hexagonal

```mermaid
mindmap
  root((Benefícios))
    Testabilidade
      Mock de ports
      Sem infraestrutura
      Testes rápidos
    Flexibilidade
      Trocar adapters
      Múltiplos adapters
      REST, GraphQL, gRPC
    Independência
      Sem acoplamento com framework
      Domain isolado
      Portabilidade
    Manutenibilidade
      Mudanças isoladas
      Código organizado
      Responsabilidades claras
```

---

## 💡 Quando Usar Cada Uma?

```mermaid
flowchart TD
    A[Escolher Arquitetura] --> B{Projeto simples<br/>CRUD básico?}
    B -->|Sim| C[✅ Layered Architecture]
    B -->|Não| D{Domínio complexo?}
    D -->|Não| C
    D -->|Sim| E{Precisa de múltiplos<br/>adapters?}
    E -->|Não| F{Testabilidade é<br/>crítica?}
    E -->|Sim| G[✅ Hexagonal Architecture]
    F -->|Sim| G
    F -->|Não| C
    
    C --> H[Mais simples<br/>Menos boilerplate<br/>Equipe júnior]
    G --> I[Mais flexível<br/>Testável<br/>Equipe experiente]
    
    style C fill:#4CAF50,stroke:#2E7D32,color:#fff
    style G fill:#2196F3,stroke:#1565C0,color:#fff
```

---

## 💡 Dica do Instrutor

```
⚠️ Layered Architecture:
✅ Use quando:
- Projeto simples (CRUD)
- Equipe iniciante
- Prazo curto
- Domínio trivial

❌ Evite quando:
- Domínio complexo
- Precisa de múltiplos front-ends/APIs
- Testabilidade é crucial
- Longo prazo

⚠️ Hexagonal Architecture:
✅ Use quando:
- Domínio rico e complexo
- Múltiplos adapters (REST, gRPC, CLI)
- Testabilidade importante
- Independência de framework
- Projeto de longo prazo

❌ Over-engineering para:
- CRUD simples
- Protótipos
- Projetos pequenos

🎯 Dica: Comece simples, evolua para hexagonal quando necessário!
```
