# Slide 13: Walkthrough — 03-clean-architecture-demo

**Horário:** 13:30 - 14:00

---

## 🎬 DEMO AO VIVO: API de Catálogo de Produtos

> Objetivo: mostrar na prática **tudo** que foi ensinado na manhã — Clean Code, DTOs, Hexagonal, @ControllerAdvice, @Valid, Custom Validator.

### Rodando o projeto

```bash
cd 03-clean-architecture-demo
mvn spring-boot:run
# Porta: 8083
# Banco: H2 em memória
# Console H2: http://localhost:8083/h2-console
```

---

## Arquitetura do Projeto — Mapa Visual

```mermaid
graph TB
    subgraph "🌐 Client"
        HTTP["HTTP Requests<br/>(Postman / .http)"]
    end

    subgraph "🔵 Adapter IN — Web"
        CTRL["ProductController<br/>@RestController"]
        REQ["ProductRequest<br/>@Valid + @ValidSku"]
        RES["ProductResponse<br/>(Record)"]
        MAP1["ProductWebMapper"]
        GEH["GlobalExceptionHandler<br/>@ControllerAdvice"]
    end

    subgraph "🟢 Domain — Core"
        UC["ProductUseCase<br/>«interface»"]
        SVC["ProductService<br/>(regras de negócio)"]
        MODEL["Product<br/>(modelo de domínio)"]
        PORT["ProductRepositoryPort<br/>«interface»"]
        EX1["ProductNotFoundException"]
        EX2["DuplicateSkuException"]
    end

    subgraph "🟠 Adapter OUT — Persistence"
        REPO["JpaProductRepository<br/>implements Port"]
        ENTITY["ProductJpaEntity<br/>@Entity"]
        MAP2["ProductPersistenceMapper"]
    end

    subgraph "🛢️ Database"
        H2[(H2 In-Memory)]
    end

    HTTP --> CTRL
    CTRL --> REQ
    CTRL --> UC
    UC --> SVC
    SVC --> MODEL
    SVC --> PORT
    SVC --> EX1
    SVC --> EX2
    PORT --> REPO
    REPO --> ENTITY
    REPO --> MAP2
    ENTITY --> H2
    SVC --> MAP1
    MAP1 --> RES
    GEH -.->|"captura exceções"| CTRL

    style SVC fill:#2ecc71,color:#fff,stroke-width:3px
    style UC fill:#27ae60,color:#fff
    style PORT fill:#27ae60,color:#fff
    style CTRL fill:#3498db,color:#fff
    style REPO fill:#e67e22,color:#fff
    style GEH fill:#e74c3c,color:#fff
```

---

## Estrutura de Pacotes do Projeto

```
com.example.cleanarchitecture/
│
├── domain/                              ← 🟢 CORE (zero imports de Spring!)
│   ├── model/
│   │   └── Product.java                 (entidade de domínio)
│   ├── port/
│   │   ├── in/
│   │   │   └── ProductUseCase.java      (interface de entrada)
│   │   └── out/
│   │       └── ProductRepositoryPort.java (interface de saída)
│   ├── service/
│   │   └── ProductService.java          (regras de negócio)
│   └── exception/
│       ├── ProductNotFoundException.java
│       └── DuplicateSkuException.java
│
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── ProductController.java   (REST API — chama UseCase)
│   │       ├── dto/
│   │       │   ├── ProductRequest.java  (DTO entrada com @Valid)
│   │       │   └── ProductResponse.java (DTO saída — Record)
│   │       ├── mapper/
│   │       │   └── ProductWebMapper.java (Request ↔ Domain)
│   │       └── handler/
│   │           └── GlobalExceptionHandler.java
│   └── out/
│       └── persistence/
│           ├── JpaProductRepository.java (implements RepositoryPort)
│           ├── ProductJpaEntity.java     (entidade JPA)
│           └── ProductPersistenceMapper.java (Domain ↔ JPA)
│
├── config/
│   └── BeanConfig.java                  (wiring via @Bean)
│
└── validation/
    ├── ValidSku.java                    (@interface)
    └── SkuValidator.java               (ConstraintValidator)
```

---

## O que Observar na Demo

### 1. Fluxo do POST /api/products — Passo a Passo

```mermaid
sequenceDiagram
    participant Client as 🌐 Client
    participant CTRL as 📥 Controller
    participant VALID as ✅ @Valid + @ValidSku
    participant MAP1 as 🔄 WebMapper
    participant SVC as ⚙️ Service
    participant PORT as 📤 Repository Port
    participant REPO as 🟠 JPA Repository
    participant MAP2 as 🔄 PersistenceMapper
    participant DB as 🛢️ H2

    Client->>CTRL: POST /api/products {name, sku, price}
    CTRL->>VALID: @Valid ProductRequest
    Note over VALID: @NotBlank name ✅<br/>@ValidSku "NOT-0001" ✅<br/>@Positive price ✅
    VALID-->>CTRL: Validação OK
    CTRL->>MAP1: toEntity(request)
    MAP1-->>CTRL: Product (domínio)
    CTRL->>SVC: create(product)
    SVC->>SVC: existsBySku("NOT-0001")?
    Note over SVC: Regra de negócio:<br/>SKU deve ser único
    SVC->>PORT: save(product)
    PORT->>MAP2: toJpaEntity(product)
    MAP2-->>PORT: ProductJpaEntity
    PORT->>REPO: save(jpaEntity)
    REPO->>DB: INSERT INTO products...
    DB-->>REPO: Entity com ID gerado
    REPO-->>SVC: Product salvo
    SVC-->>CTRL: Product
    CTRL->>MAP1: toResponse(product)
    MAP1-->>CTRL: ProductResponse
    CTRL-->>Client: 201 Created + JSON
```

### 2. Cenários de Erro para Demonstrar

```mermaid
graph LR
    subgraph "Cenários de Erro"
        E1["POST com name vazio<br/>→ 400 + errors.name"] 
        E2["POST com SKU 'invalido'<br/>→ 400 + errors.sku"]
        E3["POST com SKU duplicado<br/>→ 409 Conflict"]
        E4["GET com ID 999<br/>→ 404 Not Found"]
        E5["POST sem body<br/>→ 400 Bad Request"]
    end

    E1 -->|"@NotBlank"| V["@Valid<br/>Bean Validation"]
    E2 -->|"@ValidSku"| V
    E3 -->|"DuplicateSkuException"| GEH["@ControllerAdvice"]
    E4 -->|"ProductNotFoundException"| GEH
    E5 -->|"HttpMessageNotReadable"| GEH

    style V fill:#f39c12,color:#fff
    style GEH fill:#e74c3c,color:#fff
```

### 3. Custom Validator @ValidSku
- SKU deve seguir padrão `XXX-0000` (3 letras maiúsculas + hífen + 4 dígitos)
- Exemplos válidos: `NOT-0001`, `CEL-1234`, `MON-9999`
- Exemplos inválidos: `invalido`, `not-0001` (minúsculo), `NOTEBOOK-01`

---

## Testando com api-requests.http

```http
### ✅ Criar produto (esperar 201)
POST http://localhost:8083/api/products
Content-Type: application/json

{
    "name": "Notebook Dell",
    "sku": "NOT-0001",
    "price": 4500.00,
    "description": "Notebook Dell Inspiron 15"
}

### ✅ Buscar por ID (esperar 200)
GET http://localhost:8083/api/products/1

### ✅ Listar todos (esperar 200)
GET http://localhost:8083/api/products

### ❌ SKU duplicado (esperar 409 Conflict)
POST http://localhost:8083/api/products
Content-Type: application/json

{
    "name": "Outro Notebook",
    "sku": "NOT-0001",
    "price": 3500.00
}

### ❌ Dados inválidos (esperar 400 com errors map)
POST http://localhost:8083/api/products
Content-Type: application/json

{
    "name": "",
    "sku": "invalido",
    "price": -10
}

### ❌ ID inexistente (esperar 404 ProblemDetail)
GET http://localhost:8083/api/products/999

### ✅ Deletar (esperar 204 No Content)
DELETE http://localhost:8083/api/products/1
```

---

## 🔍 O que NÃO está no domain/

```mermaid
graph TD
    subgraph "🟢 domain/ — o que TEM"
        D1["Product.java (POJO)"]
        D2["ProductUseCase.java (interface)"]
        D3["ProductRepositoryPort.java (interface)"]
        D4["ProductService.java"]
        D5["ProductNotFoundException.java"]
    end

    subgraph "❌ domain/ — o que NÃO TEM"
        N1["❌ @Entity, @Table"]
        N2["❌ @RestController, @GetMapping"]
        N3["❌ @Repository, JpaRepository"]
        N4["❌ @Autowired, @Bean"]
        N5["❌ HttpServletRequest"]
        N6["❌ ResponseEntity"]
    end

    style D1 fill:#2ecc71,color:#fff
    style D2 fill:#2ecc71,color:#fff
    style D3 fill:#2ecc71,color:#fff
    style N1 fill:#e74c3c,color:#fff
    style N2 fill:#e74c3c,color:#fff
    style N3 fill:#e74c3c,color:#fff
```

> **Teste rápido:** abra `ProductService.java` e olhe os `import`. Se tiver `org.springframework`, algo está errado!

---

## 💡 Roteiro para o Instrutor

1. **Abrir 3 arquivos lado a lado:** Controller, Service, Repository
2. **Seguir o fluxo** de uma requisição POST do início ao fim
3. Mostrar que o `domain/` **não importa nada** do Spring (verificar imports)
4. **Provocar CADA cenário de erro** e mostrar as respostas ProblemDetail
5. Mostrar o `BeanConfig.java` e explicar o wiring: "Quem conecta Port ↔ Adapter?"
6. Perguntar: "Se eu trocar H2 por PostgreSQL, quantos arquivos mudo?"  
   Resposta: **só o application.yml** (e adicionar driver no pom.xml)
