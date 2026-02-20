# Slide 5: Arquitetura em Camadas — O Padrão das Consultorias

**Horário:** 10:00 - 10:25

---

## 🏗️ Por que Arquitetura Importa?

Sem arquitetura definida, o código vira **spaghetti**:

```mermaid
graph TD
    subgraph "❌ Sem Estrutura — Spaghetti"
        A[Controller] -->|"SQL direto"| DB[(Database)]
        A -->|"Regras de negócio"| A
        A -->|"Valida dados"| A
        A -->|"Envia email"| A
    end
```

```mermaid
graph TD
    subgraph "✅ Com Camadas — Separação Clara"
        CT["🌐 Controller<br/>(Presentation Layer)"] -->|DTO| SV["⚙️ Service<br/>(Business Layer)"]
        SV -->|Entity| RP["🗄️ Repository<br/>(Data Access Layer)"]
        RP --> DB[(Database)]
    end
```

> A **Arquitetura em Camadas** (Layered Architecture) é o padrão mais utilizado em projetos corporativos Java/Spring.

---

## 📐 As 3 Camadas Fundamentais

```mermaid
graph TB
    subgraph "Presentation Layer"
        C["🌐 Controller<br/>@RestController"]
        DTO_IN["📥 Request DTO"]
        DTO_OUT["📤 Response DTO"]
    end

    subgraph "Business Layer"
        S["⚙️ Service<br/>@Service / @Transactional"]
        V["✅ Validações de Negócio"]
        M["🔄 Mapeamento Entity ↔ DTO"]
    end

    subgraph "Data Access Layer"
        R["🗄️ Repository<br/>JpaRepository"]
        E["📦 Entity<br/>@Entity"]
    end

    subgraph "Infraestrutura"
        DB[(H2 / PostgreSQL / MySQL)]
    end

    C --> S
    S --> R
    R --> DB

    style C fill:#74c0fc,color:#000
    style S fill:#69db7c,color:#000
    style R fill:#ffd43b,color:#000
```

---

## Fluxo Completo: POST /api/products

```mermaid
sequenceDiagram
    participant Client as 📱 Client
    participant Filter as 🔒 Filters / Interceptors
    participant CT as 🌐 Controller
    participant Valid as ✅ @Valid
    participant S as ⚙️ Service
    participant Map as 🔄 Mapper
    participant R as 🗄️ Repository
    participant DB as 🛢️ Database

    Client->>Filter: POST /api/products (JSON)
    Filter->>CT: HttpServletRequest
    CT->>Valid: @Valid @RequestBody ProductRequest
    Valid-->>CT: ✅ Validado (ou lança MethodArgumentNotValidException)
    CT->>S: create(ProductRequest)
    S->>S: Aplica regras de negócio<br/>(verifica SKU duplicado, etc.)
    S->>Map: toEntity(request)
    Map-->>S: Product entity
    S->>R: save(product)
    R->>DB: INSERT INTO products ...
    DB-->>R: Product (com ID gerado)
    R-->>S: Product entity persistida
    S->>Map: toResponse(product)
    Map-->>S: ProductResponse DTO
    S-->>CT: ProductResponse
    CT-->>Client: 201 Created + JSON body
```

---

## Fluxo Completo: GET /api/products/{id} (Not Found)

```mermaid
sequenceDiagram
    participant Client as 📱 Client
    participant CT as 🌐 Controller
    participant S as ⚙️ Service
    participant R as 🗄️ Repository
    participant EH as ⚠️ @ControllerAdvice

    Client->>CT: GET /api/products/999
    CT->>S: findById(999)
    S->>R: findById(999)
    R-->>S: Optional.empty()
    S-->>S: throw ProductNotFoundException(999)
    S-->>CT: 💥 Exceção propagada
    CT-->>EH: 💥 Exceção propagada
    EH->>EH: handleNotFound(ex)
    EH-->>Client: 404 + ProblemDetail JSON
```

---

## Responsabilidades de Cada Camada

| Camada | Responsabilidade | O que NÃO faz | Anotações Spring |
|--------|-----------------|---------------|------------------|
| **Controller** | Receber HTTP, validar `@Valid`, delegar ao Service, retornar `ResponseEntity` + status code | Regras de negócio, SQL, mapeamento complexo | `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping` |
| **Service** | Regras de negócio, orquestrar operações, `@Transactional`, converter DTO ↔ Entity | Receber HTTP, acessar banco direto, retornar `ResponseEntity` | `@Service`, `@Transactional` |
| **Repository** | CRUD, queries customizadas, paginação, acesso a dados | Regras de negócio, HTTP, mapeamento DTO | `@Repository` (implícito), extends `JpaRepository` |

---

## 📏 Regra de Dependência

```mermaid
graph TD
    A["🌐 Controller"] -->|"DEPENDE de"| B["⚙️ Service"]
    B -->|"DEPENDE de"| C["🗄️ Repository"]

    A -.-x|"NUNCA depende de"| C
    C -.-x|"NUNCA depende de"| A
    C -.-x|"NUNCA depende de"| B

    style A fill:#74c0fc,color:#000
    style B fill:#69db7c,color:#000
    style C fill:#ffd43b,color:#000
```

> **As dependências fluem em UMA direção:** Controller → Service → Repository. Nunca o contrário!

---

## ❌ O que NÃO fazer

```java
// ❌ Controller fazendo tudo — regra de negócio + acesso direto ao banco
@PostMapping
public Product create(@RequestBody Product product) {
    if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) { // regra no controller!
        throw new RuntimeException("Invalid price");
    }
    return productRepository.save(product); // acesso direto ao banco!
}

// ❌ Service retornando Entity para o Controller
public Product findById(Long id) {
    return repository.findById(id).orElseThrow(); // Entity exposta!
}

// ❌ Repository com regra de negócio
@Query("SELECT p FROM Product p WHERE p.price > :minPrice AND p.stock > 0 AND p.category = 'ACTIVE'")
List<Product> findAvailableProducts(@Param("minPrice") BigDecimal minPrice);
// O conceito de "disponível" é regra de negócio, não query!
```

---

## ✅ O que fazer

```java
// ✅ Controller delega — não conhece regras de negócio
@PostMapping
public ResponseEntity<ProductResponse> create(
        @Valid @RequestBody ProductRequest request) {
    ProductResponse response = productService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

// ✅ Service aplica regras e retorna DTO
@Transactional
public ProductResponse create(ProductRequest request) {
    validateUniqueSku(request.sku());
    validateMinimumPrice(request.price());
    Product product = ProductMapper.toEntity(request);
    Product saved = repository.save(product);
    return ProductMapper.toResponse(saved);
}

// ✅ Repository apenas faz queries — sem regras
Optional<Product> findBySku(String sku);
List<Product> findByCategory(String category);
```

---

## 📂 Estrutura de Pacotes (Camadas Simples)

```
com.example.products/
│
├── controller/
│   └── ProductController.java        ← @RestController
│
├── dto/
│   ├── ProductRequest.java           ← Record (entrada)
│   └── ProductResponse.java          ← Record (saída)
│
├── mapper/
│   └── ProductMapper.java            ← toEntity(), toResponse()
│
├── service/
│   └── ProductService.java           ← @Service, regras de negócio
│
├── model/
│   └── Product.java                  ← @Entity JPA
│
├── repository/
│   └── ProductRepository.java        ← extends JpaRepository
│
├── exception/
│   ├── ProductNotFoundException.java
│   └── DuplicateSkuException.java
│
└── handler/
    └── GlobalExceptionHandler.java   ← @ControllerAdvice
```

---

## ⚖️ Arquitetura Simples vs. Hexagonal

```mermaid
graph LR
    subgraph "Camadas Simples"
        A1[Controller] --> B1[Service] --> C1[Repository]
    end

    subgraph "Hexagonal"
        A2["Adapter IN<br/>(Controller)"] --> B2["Domain<br/>(UseCase + Service)"] --> C2["Adapter OUT<br/>(JPA Repository)"]
        B2 -.->|"Port IN<br/>(interface)"| A2
        B2 -.->|"Port OUT<br/>(interface)"| C2
    end
```

| Cenário | Arquitetura |
|---------|------------|
| CRUD simples, API pequena | ✅ Camadas simples |
| Regras de negócio complexas | ✅ Hexagonal |
| MVP, hackathon, POC | ✅ Camadas simples |
| Projeto de longo prazo, equipe grande | ✅ Hexagonal |

---

## 💡 Dica do Instrutor

Mostrar no `03-clean-architecture-demo` como o fluxo funciona na prática. Abrir Controller → Service → Repository lado a lado no VS Code (Split Editor) e seguir o caminho de um POST.
