# Dia 4 - Arquitetura Hexagonal e Clean Architecture

**Duração**: 5 horas  
**Objetivo**: Implementar aplicações seguindo Arquitetura Hexagonal e Clean Architecture

## 📋 Conteúdo Programático

### Manhã (3 horas)

#### 1. Arquitetura Hexagonal (Ports & Adapters) (1.5h)

**Conceitos Fundamentais**
```
┌──────────────────────────────────────────────┐
│              DRIVING SIDE                    │
│  (Actors que dirigem a aplicação)            │
│                                              │
│  ┌────────┐    ┌────────┐    ┌────────┐     │
│  │  REST  │    │  CLI   │    │  gRPC  │     │
│  │Adapter │    │Adapter │    │Adapter │     │
│  └───┬────┘    └───┬────┘    └───┬────┘     │
│      │            │             │           │
│  ┌───▼────────────▼─────────────▼───┐       │
│  │        INPUT PORTS                │       │
│  │  (interfaces/use cases)           │       │
│  └───────────────┬───────────────────┘       │
│                  │                           │
│  ┌───────────────▼───────────────────┐       │
│  │      DOMAIN / CORE LOGIC          │       │
│  │  (Business Rules, Entities)       │       │
│  └───────────────┬───────────────────┘       │
│                  │                           │
│  ┌───────────────▼───────────────────┐       │
│  │        OUTPUT PORTS               │       │
│  │  (repository interfaces)          │       │
│  └───┬────────────┬─────────────┬────┘       │
│      │            │             │           │
│  ┌───▼────┐  ┌───▼────┐    ┌───▼────┐       │
│  │   JPA  │  │MongoDB │    │ Redis  │       │
│  │Adapter │  │Adapter │    │Adapter │       │
│  └────────┘  └────────┘    └────────┘       │
│                                              │
│              DRIVEN SIDE                     │
│  (Tecnologias dirigidas pela aplicação)      │
└──────────────────────────────────────────────┘
```

**Organização de Pacotes**
```
src/main/java/com/example/
├── domain/
│   ├── model/
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── Money.java
│   ├── port/
│   │   ├── in/
│   │   │   ├── CreateOrderUseCase.java
│   │   │   ├── GetOrderUseCase.java
│   │   │   └── CancelOrderUseCase.java
│   │   └── out/
│   │       ├── OrderRepository.java
│   │       ├── PaymentGateway.java
│   │       └── NotificationService.java
│   └── service/
│       └── OrderService.java (implementa use cases)
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── OrderController.java
│   │       └── dto/
│   └── out/
│       ├── persistence/
│       │   ├── OrderJpaRepository.java
│       │   ├── OrderEntity.java
│       │   └── OrderPersistenceAdapter.java
│       └── payment/
│           └── PaymentGatewayAdapter.java
└── config/
    └── BeanConfiguration.java
```

**Exemplo Prático**:
```java
// DOMAIN - Input Port (Use Case)
package domain.port.in;

public interface CreateOrderUseCase {
    OrderId createOrder(CreateOrderCommand command);
}

// DOMAIN - Output Port
package domain.port.out;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
}

// DOMAIN - Service (implementa use case)
package domain.service;

@Service
@Transactional
public class OrderService implements CreateOrderUseCase {
    
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    
    @Override
    public OrderId createOrder(CreateOrderCommand command) {
        // Lógica de domínio aqui
        Order order = Order.create(command);
        paymentGateway.authorize(order.getTotal());
        orderRepository.save(order);
        return order.getId();
    }
}

// ADAPTER IN - REST Controller
package adapter.in.web;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final CreateOrderUseCase createOrderUseCase;
    
    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        var command = new CreateOrderCommand(request);
        OrderId orderId = createOrderUseCase.createOrder(command);
        return ResponseEntity.ok(new OrderResponse(orderId));
    }
}

// ADAPTER OUT - Persistence
package adapter.out.persistence;

@Component
public class OrderPersistenceAdapter implements OrderRepository {
    
    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;
    
    @Override
    public void save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        jpaRepository.save(entity);
    }
}
```

#### 2. Clean Architecture (1.5h)

**Camadas e Dependências**
```
┌─────────────────────────────────────┐
│      Frameworks & Drivers           │  (Web, DB, UI)
├─────────────────────────────────────┤
│    Interface Adapters               │  (Controllers, Presenters)
├─────────────────────────────────────┤
│    Use Cases / Application          │  (Business Rules)
├─────────────────────────────────────┤
│    Entities / Domain                │  (Enterprise Rules)
└─────────────────────────────────────┘

Dependency Rule: Dependências apontam para dentro
```

**Principais diferenças com arquitetura tradicional**:
- Domínio não conhece infraestrutura
- Use Cases explícitos
- Testabilidade independente
- Inversão de dependências radical

### Tarde (2 horas)

#### 3. Implementação Prática (2h)

**Projeto: E-commerce Modular**

Vamos criar um módulo de **Catálogo de Produtos** com Arquitetura Hexagonal:

**Features**:
- Criar produto
- Buscar produtos
- Atualizar estoque
- Categorizar produtos

**Estrutura**:
```
catalog/
├── domain/
│   ├── model/
│   │   ├── Product.java
│   │   ├── ProductId.java (Value Object)
│   │   ├── Money.java (Value Object)
│   │   ├── Stock.java (Value Object)
│   │   └── Category.java
│   ├── port/
│   │   ├── in/
│   │   │   ├── CreateProductUseCase.java
│   │   │   ├── UpdateStockUseCase.java
│   │   │   └── SearchProductsUseCase.java
│   │   └── out/
│   │       ├── ProductRepository.java
│   │       └── CategoryRepository.java
│   ├── service/
│   │   └── ProductService.java
│   └── exception/
│       ├── ProductNotFoundException.java
│       └── InsufficientStockException.java
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── ProductController.java
│   │       └── dto/
│   └── out/
│       └── persistence/
│           ├── ProductJpaRepository.java
│           ├── ProductEntity.java
│           └── ProductPersistenceAdapter.java
└── config/
    └── CatalogConfiguration.java
```

## 💻 Exercícios Práticos

### Exercício 1: Converter aplicação existente (2h)

Pegue a API de Tasks dos dias anteriores e converta para Arquitetura Hexagonal:

**Passo 1**: Definir o domínio
```java
// domain/model/Task.java
public class Task {
    private final TaskId id;
    private String title;
    private String description;
    private TaskStatus status;
    
    // Comportamentos ricos
    public void complete() {
        if (status == TaskStatus.COMPLETED) {
            throw new TaskAlreadyCompletedException();
        }
        this.status = TaskStatus.COMPLETED;
    }
}

// domain/model/TaskId.java (Value Object)
public record TaskId(Long value) {
    public TaskId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Invalid task ID");
        }
    }
}
```

**Passo 2**: Definir Use Cases (Input Ports)
```java
// domain/port/in/CreateTaskUseCase.java
public interface CreateTaskUseCase {
    TaskId create(CreateTaskCommand command);
}

// domain/port/in/CreateTaskCommand.java
public record CreateTaskCommand(
    String title,
    String description,
    Priority priority
) {}
```

**Passo 3**: Definir Output Ports
```java
// domain/port/out/TaskRepository.java
public interface TaskRepository {
    void save(Task task);
    Optional<Task> findById(TaskId id);
    List<Task> findAll();
}
```

**Passo 4**: Implementar Service
```java
// domain/service/TaskService.java
@Service
public class TaskService implements CreateTaskUseCase, CompleteTaskUseCase {
    
    private final TaskRepository taskRepository;
    
    @Override
    public TaskId create(CreateTaskCommand command) {
        Task task = Task.create(command.title(), command.description(), command.priority());
        taskRepository.save(task);
        return task.getId();
    }
}
```

**Passo 5**: Implementar Adapters
```java
// adapter/in/web/TaskController.java
@RestController
public class TaskController {
    private final CreateTaskUseCase createTaskUseCase;
    // ...
}

// adapter/out/persistence/TaskPersistenceAdapter.java
@Component
public class TaskPersistenceAdapter implements TaskRepository {
    private final TaskJpaRepository jpaRepository;
    // ...
}
```

### Exercício 2: Novo módulo com Clean Architecture (1.5h)

Crie um módulo de **Autenticação** seguindo Clean Architecture:

**Requisitos**:
- Registrar usuário
- Fazer login
- Validar token
- Atualizar perfil

**Use Cases**:
- RegisterUserUseCase
- LoginUseCase
- ValidateTokenUseCase
- UpdateProfileUseCase

**Output Ports**:
- UserRepository
- PasswordEncoder
- TokenGenerator

## 📚 Material de Estudo

### Leitura Obrigatória
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture by Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Get Your Hands Dirty on Clean Architecture](https://www.baeldung.com/spring-boot-clean-architecture)

### Leitura Complementar
- [Implementing DDD](https://www.amazon.com/Implementing-Domain-Driven-Design-Vaughn-Vernon/dp/0321834577)
- [Hexagonal Architecture with Spring Boot](https://medium.com/@TKonuklar/hexagonal-architecture-with-spring-boot)

### Exemplos de Código
- [Spring Boot Hexagonal Example](https://github.com/thombergs/buckpal)
- [Clean Architecture Example](https://github.com/mattia-battiston/clean-architecture-example)

## 🎯 Objetivos de Aprendizagem

Ao final deste dia, você deve ser capaz de:

- ✅ Explicar os conceitos de Ports & Adapters
- ✅ Organizar código seguindo Arquitetura Hexagonal
- ✅ Distinguir Input Ports de Output Ports
- ✅ Implementar Use Cases explícitos
- ✅ Manter domínio independente de framework
- ✅ Testar lógica de negócio isoladamente

## 🏠 Tarefa de Casa

1. **Completar refatoração**:
   - Converter completamente a aplicação de Tasks
   - Adicionar testes unitários para o domínio
   - Adicionar segundo adapter (ex: CLI ou gRPC)

2. **Estudar**:
   - Bounded Contexts em DDD
   - CQRS pattern
   - Event-Driven Architecture

3. **Preparação para Dia 5**:
   - Revisar REST best practices
   - Estudar OpenAPI/Swagger specification
   - Entender OAuth2 e JWT

## 📝 Notas do Instrutor

```
Pontos de atenção:
- Enfatizar separação entre domínio e infraestrutura
- Mostrar como testar domínio sem Spring
- Explicar naming conventions (UseCase, Port, Adapter)
- Demonstrar facilidade de trocar adapters
- Discutir trade-offs (complexidade vs benefícios)
- Mostrar quando é OVERKILL usar essa arquitetura
```

## 🔗 Links Úteis

- [Hexagonal Architecture](https://netflixtechblog.com/ready-for-changes-with-hexagonal-architecture-b315ec967749)
- [Clean Architecture Template](https://github.com/mattia-battiston/clean-architecture-example)
- [Spring Modulith](https://spring.io/projects/spring-modulith)
- [ArchUnit](https://www.archunit.org/) - Testes arquiteturais
