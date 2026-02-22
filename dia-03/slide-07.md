# Slide 7: Arquitetura Hexagonal — Ports & Adapters

**Horário:** 11:00 - 11:30

---

## 📖 Origem e Conceitos

> "Permita que uma aplicação seja igualmente dirigida por **usuários, programas, testes automatizados** ou scripts batch, e que seja desenvolvida e testada **em isolamento** de seus dispositivos de runtime e bancos de dados."
> — **Alistair Cockburn**, 2005

A arquitetura hexagonal (também chamada **Ports & Adapters**) foi proposta para resolver o problema de código de negócio ficar preso a detalhes de infraestrutura (banco, HTTP, filas, etc.).

Outros nomes que se referem à mesma ideia:
- **Clean Architecture** — Robert C. Martin (2012)
- **Onion Architecture** — Jeffrey Palermo (2008)

```mermaid
graph LR
    subgraph "Evolução Arquitetural"
        A["Código Spaghetti<br/>tudo junto"] -->|separação| B["Camadas<br/>Controller→Service→Repo"]
        B -->|inversão de<br/>dependência| C["Hexagonal<br/>Ports & Adapters"]
        C -->|mais rigor<br/>mais camadas| D["Clean Architecture<br/>Uncle Bob"]
    end
    style A fill:#e74c3c,color:#fff
    style B fill:#f39c12,color:#fff
    style C fill:#2ecc71,color:#fff
    style D fill:#3498db,color:#fff
```

---

## 🤔 O Problema do "Service que faz tudo"

Mesmo com camadas, o Service pode ficar **acoplado à infraestrutura**:

```java
// ❌ Service acoplado — sabe sobre JPA, HTTP, SMTP
@Service
public class ProductService {
    private final JpaRepository<Product, Long> repo;     // JPA específico
    private final RestTemplate restTemplate;              // HTTP específico
    private final JavaMailSender mailSender;              // SMTP específico

    // Regras de negócio MISTURADAS com infraestrutura
    // Se trocar JPA por MongoDB → muda tudo
    // Se trocar RestTemplate por Feign → muda tudo
    // Para testar → precisa de banco + SMTP + API externa rodando!
}
```

### O Problema em Diagrama

```mermaid
graph TD
    subgraph "❌ Camadas — Dependência vai para FORA"
        C1["Controller"] --> S1["Service"]
        S1 --> R1["JpaRepository"]
        S1 --> RT["RestTemplate"]
        S1 --> MS["JavaMailSender"]
        R1 --> DB1[(PostgreSQL)]
        RT --> API["API Externa"]
        MS --> SMTP["Servidor SMTP"]
    end
    style S1 fill:#e74c3c,color:#fff
    style R1 fill:#95a5a6,color:#fff
    style RT fill:#95a5a6,color:#fff
    style MS fill:#95a5a6,color:#fff
```

> **Problema**: Para testar o `Service`, preciso de **PostgreSQL + API externa + SMTP** rodando. Impossível testar isoladamente.

---

## 🎯 A Ideia da Arquitetura Hexagonal

> "O domínio não sabe (e não se importa) se os dados vêm de um banco PostgreSQL, de uma API REST ou de um arquivo CSV."

### O Hexágono — Visão Completa

```mermaid
graph TD
    subgraph Adapters_IN["🔵 ADAPTERS IN (quem chama)"]
        REST["REST Controller<br/>@RestController"]
        GQL["GraphQL<br/>Resolver"]
        CLI["CLI / Batch<br/>CommandLineRunner"]
        GRPC["gRPC<br/>Service"]
    end

    subgraph Domain["🟢 DOMAIN (core — sem frameworks)"]
        PI["📥 Port IN<br/>ProductUseCase"]
        SVC["ProductService<br/>(regras de negócio)"]
        PO["📤 Port OUT<br/>ProductRepository"]
        EXC["Exceptions<br/>NotFoundException"]
        SVC --- PI
        SVC --- PO
        SVC --- EXC
    end

    subgraph Adapters_OUT["🟠 ADAPTERS OUT (implementações)"]
        JPA["JPA Repository<br/>PostgreSQL"]
        MONGO["MongoDB<br/>Repository"]
        EXTAPI["External API<br/>Client"]
        QUEUE["Message Queue<br/>RabbitMQ / Kafka"]
    end

    REST -->|"implementa"| PI
    GQL -->|"implementa"| PI
    CLI -->|"implementa"| PI
    GRPC -->|"implementa"| PI

    PO -->|"interface"| JPA
    PO -->|"interface"| MONGO
    PO -->|"interface"| EXTAPI
    PO -->|"interface"| QUEUE

    style Domain fill:#d5f5e3,stroke:#27ae60,stroke-width:3px
    style Adapters_IN fill:#d6eaf8,stroke:#2980b9,stroke-width:2px
    style Adapters_OUT fill:#fdebd0,stroke:#e67e22,stroke-width:2px
```

### A Regra de Dependência

```mermaid
graph LR
    A["Adapters IN<br/>(Web, CLI, gRPC)"] -->|"dependem de"| D["Domain<br/>(Ports + Service)"]
    AO["Adapters OUT<br/>(JPA, Mongo, API)"] -->|"implementam"| D
    D -.->|"❌ NUNCA<br/>depende de"| A
    D -.->|"❌ NUNCA<br/>depende de"| AO
    style D fill:#2ecc71,color:#fff,stroke-width:3px
    style A fill:#3498db,color:#fff
    style AO fill:#e67e22,color:#fff
```

> **Dependency Inversion Principle (DIP):** O domínio define interfaces (Ports). Adapters implementam. Dependências apontam **para dentro**, nunca para fora.

---

## Conceitos: Ports e Adapters — Detalhado

| Conceito | O que é | Direção | Exemplo | Quem implementa |
|----------|---------|---------|---------|-----------------|
| **Port IN** | Interface que o domínio **expõe** | Entrada → Domínio | `ProductUseCase` | `ProductService` |
| **Port OUT** | Interface que o domínio **precisa** | Domínio → Saída | `ProductRepository` | `JpaProductRepository` |
| **Adapter IN** | Componente que **chama** o domínio | Externo → Port IN | `ProductController` (REST) | Framework (Spring) |
| **Adapter OUT** | Componente que **é chamado** pelo domínio | Port OUT → Externo | `JpaProductRepository` (JPA) | Framework (Spring Data) |

```mermaid
sequenceDiagram
    participant Client as 🌐 Client
    participant AdapterIN as 🔵 Adapter IN<br/>(Controller)
    participant PortIN as 📥 Port IN<br/>(UseCase)
    participant Service as 🟢 Service<br/>(Business Logic)
    participant PortOUT as 📤 Port OUT<br/>(Repository)
    participant AdapterOUT as 🟠 Adapter OUT<br/>(JpaRepo)
    participant DB as 🛢️ Database

    Client->>AdapterIN: POST /api/products
    AdapterIN->>PortIN: create(request)
    PortIN->>Service: create(request)
    Service->>Service: Regras de negócio<br/>(validar SKU, calcular)
    Service->>PortOUT: save(product)
    PortOUT->>AdapterOUT: save(product)
    AdapterOUT->>DB: INSERT INTO...
    DB-->>AdapterOUT: Product com ID
    AdapterOUT-->>Service: Product salvo
    Service-->>AdapterIN: ProductResponse
    AdapterIN-->>Client: 201 Created + JSON
```

---

## Estrutura de Pacotes

```
com.example.products/
│
├── domain/                              ← CORE (sem dependência de framework)
│   ├── model/
│   │   └── Product.java                 (entidade de domínio PURA — sem @Entity JPA!)
│   ├── port/
│   │   ├── in/
│   │   │   └── ProductUseCase.java      (interface: createProduct, findById...)
│   │   └── out/
│   │       └── ProductRepository.java   (interface: save, findById, findAll...)
│   ├── service/
│   │   └── ProductService.java          (implements ProductUseCase — SÓ regras de negócio)
│   └── exception/
│       ├── ProductNotFoundException.java (exceção de domínio)
│       └── DuplicateSkuException.java   (exceção de domínio)
│
├── adapter/
│   ├── in/
│   │   └── web/                         ← ADAPTER IN (REST API)
│   │       ├── ProductController.java   (@RestController — chama UseCase)
│   │       ├── dto/
│   │       │   ├── ProductRequest.java  (DTO entrada — @Valid aqui)
│   │       │   └── ProductResponse.java (DTO saída — sem campos internos)
│   │       ├── mapper/
│   │       │   └── ProductWebMapper.java (Request ↔ Domain, Domain ↔ Response)
│   │       └── handler/
│   │           └── GlobalExceptionHandler.java (@ControllerAdvice)
│   └── out/
│       └── persistence/                 ← ADAPTER OUT (JPA)
│           ├── JpaProductRepository.java (implements ProductRepository do domain)
│           ├── ProductJpaEntity.java    (@Entity JPA — diferente do domain/model!)
│           └── ProductPersistenceMapper.java (Domain ↔ JPA Entity)
│
└── config/
    └── BeanConfig.java                  ← Wiring: @Bean para conectar Port ↔ Adapter
```

### Por que 2 models (Domain vs JPA)?

```mermaid
graph LR
    subgraph "domain/model/"
        DM["Product<br/>(POJO puro)<br/>sem @Entity<br/>sem @Column"]
    end
    subgraph "adapter/out/persistence/"
        JPA["ProductJpaEntity<br/>@Entity<br/>@Table<br/>@Column<br/>@GeneratedValue"]
    end
    DM <-->|"PersistenceMapper<br/>toJpaEntity() / toDomain()"| JPA
    style DM fill:#2ecc71,color:#fff
    style JPA fill:#e67e22,color:#fff
```

> Em projetos simples/médios, é aceitável usar a mesma classe `@Entity` no domain. Em projetos grandes, separe para total independência do JPA.

---

## Na Prática: Interfaces (Ports)

```java
// Port IN — o que o domínio OFERECE (contrato para quem chama)
public interface ProductUseCase {
    ProductResponse create(ProductRequest request);
    ProductResponse findById(Long id);
    List<ProductResponse> findAll();
    void delete(Long id);
}

// Port OUT — o que o domínio PRECISA (contrato para infraestrutura)
public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    void deleteById(Long id);
    boolean existsBySku(String sku);
}
```

### Wiring: Conectando Tudo

```java
// config/BeanConfig.java — quem conecta as peças
@Configuration
public class BeanConfig {

    @Bean
    public ProductUseCase productUseCase(ProductRepository repository) {
        return new ProductService(repository);
        // Spring injeta JpaProductRepository (que implementa ProductRepository)
        // ProductService não sabe que está usando JPA!
    }
}
```

---

## 🧪 Testabilidade — O Grande Benefício

```mermaid
graph TD
    subgraph "Teste Unitário (sem Spring, sem banco)"
        T1["ProductServiceTest"] --> S["ProductService"]
        S --> MOCK["Mock<br/>ProductRepository<br/>(Mockito)"]
        style MOCK fill:#9b59b6,color:#fff
    end

    subgraph "Teste de Integração (com banco real)"
        T2["ProductControllerIT"] --> CTRL["Controller"]
        CTRL --> SVC["Service"]
        SVC --> REAL["JpaProductRepository<br/>(H2 em memória)"]
        REAL --> H2[(H2)]
    end
```

> **Sem hexagonal:** preciso do Spring + Banco para testar regras de negócio.  
> **Com hexagonal:** testo regras de negócio com **Mockito puro** — rápido, isolado, confiável.

---

## Quando usar Hexagonal vs. Camadas?

| Cenário | Camadas Simples | Hexagonal | Justificativa |
|---------|:-:|:-:|---------------|
| CRUD simples, poucos endpoints | ✅ | | Over-engineering desnecessário |
| Domínio complexo, muitas regras | | ✅ | Isola regra de negócio |
| Múltiplas fontes de dados (SQL + NoSQL + API) | | ✅ | Troca de adapter sem afetar domínio |
| Equipe grande, projeto de longo prazo | | ✅ | Boundaries claras, menos conflitos |
| MVP, prova de conceito, hackathon | ✅ | | Velocidade > perfeição |
| Microserviço com lógica significativa | | ✅ | Testes unitários rápidos |
| API com 3-4 endpoints e pouca lógica | ✅ | | Simplicidade vence |

> **Dica:** Comece com camadas simples. Quando sentir **dor de acoplamento** (difícil testar, difícil trocar banco, Service gigante), migre para hexagonal. A migração é incremental.

---

## 📊 Resumo Visual

```mermaid
graph TB
    subgraph "Regra de Dependência"
        direction TB
        EXT["🌍 Mundo Externo<br/>(HTTP, CLI, Filas)"]
        AIN["🔵 Adapters IN<br/>(Controllers, Consumers)"]
        PORTS["📥📤 PORTS<br/>(Interfaces)"]
        CORE["🟢 DOMAIN CORE<br/>(Service + Model + Exceptions)"]
        AOUT["🟠 Adapters OUT<br/>(JPA, Clients, Publishers)"]
        INFRA["🏗️ Infraestrutura<br/>(PostgreSQL, RabbitMQ, APIs)"]

        EXT --> AIN
        AIN --> PORTS
        PORTS --> CORE
        AOUT --> PORTS
        INFRA --> AOUT
    end

    style CORE fill:#2ecc71,color:#fff,stroke-width:3px
    style PORTS fill:#f1c40f,color:#000,stroke-width:2px
    style AIN fill:#3498db,color:#fff
    style AOUT fill:#e67e22,color:#fff
```

---

## 🎯 Pergunta para a turma

> Se eu precisar trocar de PostgreSQL para MongoDB, quantos arquivos eu mudo?  
> Resposta: **apenas 2!** `JpaProductRepository` → `MongoProductRepository` e `ProductJpaEntity` → sem entity.  
> O `ProductService` (que tem toda a lógica) **não muda nenhuma linha.**

---

## 💡 Dica do Instrutor

Mostrar o diagrama de pacotes no `03-clean-architecture-demo` e como as dependências apontam para dentro (domain). Desenhar no quadro: "Se eu trocar JPA por MongoDB, o que muda? O que NÃO muda?"
