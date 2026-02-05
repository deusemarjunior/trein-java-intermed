# Slide 8: Dependency Inversion Principle (DIP)

---

## 📖 Definição

> **"Módulos de alto nível não devem depender de módulos de baixo nível. Ambos devem depender de abstrações."**
> 
> **"Abstrações não devem depender de detalhes. Detalhes devem depender de abstrações."**
> 
> *— Robert C. Martin*

```mermaid
graph TD
    subgraph "❌ Sem DIP"
        A[High Level<br/>OrderService] -->|depende| B[Low Level<br/>MySQLRepository]
    end
    
    subgraph "✅ Com DIP"
        C[High Level<br/>OrderService] -->|depende| D[Abstração<br/>OrderRepository]
        E[Low Level<br/>MySQLRepository] -->|implementa| D
        F[Low Level<br/>MongoRepository] -->|implementa| D
    end
    
    style A fill:#f44336,stroke:#c62828,color:#fff
    style B fill:#f44336,stroke:#c62828,color:#fff
    style C fill:#4CAF50,stroke:#2E7D32,color:#fff
    style D fill:#2196F3,stroke:#1565C0,color:#fff
    style E fill:#4CAF50,stroke:#2E7D32,color:#fff
    style F fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## ❌ Violação do DIP

```java
// ❌ Classe de alto nível depende de implementação concreta
@Service
public class OrderService {
    
    // Acoplamento direto com MySQL
    private MySQLOrderRepository repository;
    
    public OrderService() {
        // Instancia diretamente a implementação
        this.repository = new MySQLOrderRepository();
    }
    
    public Order createOrder(Order order) {
        // Lógica de negócio depende de detalhe de infraestrutura
        return repository.saveToMySQL(order);
    }
}

// Implementação concreta
public class MySQLOrderRepository {
    public Order saveToMySQL(Order order) {
        // Código específico do MySQL
        return order;
    }
}
```

**Problema:** Impossível trocar MySQL por outro banco sem modificar OrderService! 🔒

---

## 📊 Arquitetura Tradicional (Violação)

```mermaid
graph TD
    UI[UI Layer<br/>Controllers] -->|depende| BL[Business Layer<br/>Services]
    BL -->|depende| DAL[Data Layer<br/>MySQL/PostgreSQL]
    DB[(Database)]
    DAL -->|acessa| DB
    
    style UI fill:#FF9800,stroke:#F57C00,color:#fff
    style BL fill:#FF9800,stroke:#F57C00,color:#fff
    style DAL fill:#f44336,stroke:#c62828,color:#fff
    
    Note1[❌ Fluxo de dependência<br/>aponta para baixo<br/>Alto nível depende de baixo nível]
```

---

## ✅ Aplicando DIP

```mermaid
graph TD
    subgraph "Domain Layer (High Level)"
        BL[OrderService<br/>Business Logic]
        ABS[OrderRepository<br/><<interface>>]
    end
    
    subgraph "Infrastructure Layer (Low Level)"
        MYSQL[MySQLOrderRepository]
        MONGO[MongoOrderRepository]
        REDIS[RedisOrderRepository]
    end
    
    BL -->|depende| ABS
    MYSQL -.implementa.-> ABS
    MONGO -.implementa.-> ABS
    REDIS -.implementa.-> ABS
    
    style BL fill:#4CAF50,stroke:#2E7D32,color:#fff
    style ABS fill:#2196F3,stroke:#1565C0,color:#fff
    style MYSQL fill:#4CAF50,stroke:#2E7D32,color:#fff
    style MONGO fill:#4CAF50,stroke:#2E7D32,color:#fff
    style REDIS fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## ✅ Código Refatorado

```java
// 1️⃣ Abstração (interface) no domínio
public interface OrderRepository {
    Order save(Order order);
    Order findById(Long id);
    List<Order> findAll();
}

// 2️⃣ Service depende da abstração (Injeção de Dependência)
@Service
@RequiredArgsConstructor  // Lombok - constructor injection
public class OrderService {
    
    // Depende de abstração, não implementação
    private final OrderRepository repository;
    
    public Order createOrder(Order order) {
        // Lógica de negócio isolada dos detalhes
        return repository.save(order);
    }
}

// 3️⃣ Implementação concreta 1 - MySQL
@Repository
public class MySQLOrderRepository implements OrderRepository {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Order save(Order order) {
        em.persist(order);
        return order;
    }
    
    // outros métodos...
}

// 4️⃣ Implementação concreta 2 - MongoDB (alternativa)
@Repository
@Profile("mongodb")  // Ativa apenas com profile mongodb
public class MongoOrderRepository implements OrderRepository {
    private final MongoTemplate mongoTemplate;
    
    @Override
    public Order save(Order order) {
        return mongoTemplate.save(order);
    }
    
    // outros métodos...
}
```

---

## 🔄 Inversão de Controle (IoC)

```mermaid
sequenceDiagram
    participant Spring as Spring Container
    participant Repo as OrderRepository Impl
    participant Service as OrderService
    
    Note over Spring: 1. Application Startup
    Spring->>Repo: Creates instance
    Spring->>Service: Creates instance
    Spring->>Service: Injects OrderRepository
    
    Note over Service: 2. Runtime
    Service->>Repo: save(order)
    Repo-->>Service: saved order
    
    Note over Service,Repo: ✅ Service não sabe qual implementação está usando!
```

---

## 🎯 Benefícios do DIP

```mermaid
mindmap
  root((DIP))
    Testabilidade
      Fácil criar mocks
      Testes isolados
      Sem dependência de DB
    Flexibilidade
      Troca de implementação
      Múltiplas implementações
      Profiles do Spring
    Manutenibilidade
      Mudanças isoladas
      Baixo acoplamento
      Alta coesão
    Reutilização
      Lógica de negócio isolada
      Independente de framework
      Portabilidade
```

---

## 🧪 Testabilidade Comparada

### ❌ Sem DIP - Difícil testar

```java
@Test
public void testCreateOrder() {
    OrderService service = new OrderService();
    // 💥 Precisa de MySQL rodando!
    // 💥 Precisa configurar conexão!
    // 💥 Teste lento!
    Order order = service.createOrder(new Order());
}
```

### ✅ Com DIP - Fácil testar

```java
@Test
public void testCreateOrder() {
    // ✅ Mock da interface
    OrderRepository mockRepo = mock(OrderRepository.class);
    when(mockRepo.save(any())).thenReturn(new Order());
    
    // ✅ Teste rápido, isolado, sem DB
    OrderService service = new OrderService(mockRepo);
    Order order = service.createOrder(new Order());
    
    verify(mockRepo).save(any());
}
```

---

## 🏗️ DIP em Arquitetura Limpa

```mermaid
graph TB
    subgraph "🎯 Domain Layer (Core)"
        UC[Use Cases<br/>Business Rules]
        ENT[Entities<br/>Domain Models]
        PORT[Ports<br/><<interfaces>>]
    end
    
    subgraph "🔌 Infrastructure Layer"
        WEB[Web<br/>Controllers]
        DB[Database<br/>Repositories]
        EXT[External APIs<br/>Services]
    end
    
    UC -->|usa| ENT
    UC -->|define| PORT
    WEB -.depende.-> UC
    DB -.implementa.-> PORT
    EXT -.implementa.-> PORT
    
    style UC fill:#4CAF50,stroke:#2E7D32,color:#fff
    style ENT fill:#4CAF50,stroke:#2E7D32,color:#fff
    style PORT fill:#2196F3,stroke:#1565C0,color:#fff
    
    Note1[✅ Dependências apontam<br/>para dentro (domain)<br/>Domain não conhece infra]
```

---

## 🔧 DIP com Spring Boot

### Constructor Injection (Recomendado)

```java
@Service
@RequiredArgsConstructor  // Lombok
public class OrderService {
    private final OrderRepository repository;      // ✅ final + constructor
    private final PaymentService paymentService;   // ✅ Imutável
    private final EmailService emailService;       // ✅ Testável
}
```

### Field Injection (❌ Evite)

```java
@Service
public class OrderService {
    @Autowired  // ❌ Dificulta testes
    private OrderRepository repository;
}
```

---

## 📊 Comparação: Acoplamento

```mermaid
graph LR
    subgraph "❌ Alto Acoplamento (Sem DIP)"
        A1[OrderService] -->|new| B1[MySQLRepo]
        A1 -->|new| C1[EmailSender]
        A1 -->|new| D1[Logger]
        
        style A1 fill:#f44336,stroke:#c62828,color:#fff
    end
    
    subgraph "✅ Baixo Acoplamento (Com DIP)"
        A2[OrderService] -->|injeta| B2[IRepository]
        A2 -->|injeta| C2[IEmailService]
        A2 -->|injeta| D2[ILogger]
        
        B3[MySQLRepo] -.impl.-> B2
        C3[EmailSender] -.impl.-> C2
        D3[Logger] -.impl.-> D2
        
        style A2 fill:#4CAF50,stroke:#2E7D32,color:#fff
        style B2 fill:#2196F3,stroke:#1565C0,color:#fff
        style C2 fill:#2196F3,stroke:#1565C0,color:#fff
        style D2 fill:#2196F3,stroke:#1565C0,color:#fff
    end
```

---

## 🚨 Sinais de Violação

```mermaid
flowchart TD
    A[Revisar Código] --> B{Usa 'new'<br/>para dependências?}
    B -->|Sim| X[❌ Viola DIP]
    B -->|Não| C{Importa classes<br/>concretas de infra?}
    C -->|Sim| X
    C -->|Não| D{Depende de<br/>implementação específica?}
    D -->|Sim| X
    D -->|Não| E{Difícil criar<br/>testes unitários?}
    E -->|Sim| X
    E -->|Não| F[✅ Respeita DIP]
    
    style F fill:#4CAF50,stroke:#2E7D32,color:#fff
    style X fill:#f44336,stroke:#c62828,color:#fff
```

---

## 💡 Dica do Instrutor

```
⚠️ Regras práticas:
1. Nunca use 'new' para dependências (use @Autowired)
2. Sempre programe para interfaces, não implementações
3. Use constructor injection (não field injection)
4. Domínio não deve conhecer infraestrutura
5. Se difícil testar, provavelmente viola DIP

🎯 Lembre-se:
- DIP != Dependency Injection (DI)
- DIP é o princípio (design)
- DI é a técnica (implementação)
- Spring IoC implementa ambos
```
