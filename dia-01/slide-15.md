# Slide 15: REST vs RESTful — Princípios e Maturidade

**Tópico complementar**

---

## 🤔 REST é a mesma coisa que RESTful?

```
╔═══════════════════════════════════════════════════════════════╗
║  REST  = Estilo arquitetural (conceito teórico)               ║
║  RESTful = API que SEGUE os princípios REST corretamente      ║
║                                                               ║
║  Nem toda API que usa HTTP é RESTful!                         ║
╚═══════════════════════════════════════════════════════════════╝
```

---

## 📐 Os 6 Princípios REST (Roy Fielding, 2000)

```mermaid
mindmap
    root((🏛️ REST<br/>Constraints))
        1️⃣ Client-Server
            Separação de responsabilidades
            Cliente cuida da UI
            Servidor cuida dos dados
        2️⃣ Stateless
            Sem estado no servidor
            Cada request é independente
            Token JWT ao invés de sessão
        3️⃣ Cacheable
            Respostas podem ser cacheadas
            Headers Cache-Control, ETag
            Reduz carga no servidor
        4️⃣ Uniform Interface
            URIs identificam recursos
            Verbos HTTP padronizados
            Representações JSON/XML
            HATEOAS
        5️⃣ Layered System
            Camadas intermediárias
            Load balancer, API Gateway
            Cliente não sabe quantas camadas
        6️⃣ Code on Demand
            Opcional
            Servidor pode enviar código
            Ex: JavaScript no browser
```

---

## 🎯 Interface Uniforme — O Princípio Mais Importante

### Recursos bem nomeados (substantivos, não verbos!)

```mermaid
flowchart LR
    subgraph errado["❌ ERRADO — Verbos na URL"]
        E1["GET /getProducts"]
        E2["POST /createProduct"]
        E3["POST /deleteProduct/123"]
        E4["GET /getAllUsers"]
    end

    subgraph certo["✅ CORRETO — Substantivos + Verbos HTTP"]
        C1["GET /api/products"]
        C2["POST /api/products"]
        C3["DELETE /api/products/123"]
        C4["GET /api/users"]
    end

    style errado fill:#FFCDD2,stroke:#C62828
    style certo fill:#C8E6C9,stroke:#2E7D32
```

### Verbos HTTP e seus significados

| Verbo | Ação CRUD | Exemplo | Idempotente? |
|-------|-----------|---------|:------------:|
| `GET` | **Read** | `GET /api/products` | ✅ Sim |
| `POST` | **Create** | `POST /api/products` | ❌ Não |
| `PUT` | **Update** (completo) | `PUT /api/products/1` | ✅ Sim |
| `PATCH` | **Update** (parcial) | `PATCH /api/products/1` | ✅ Sim |
| `DELETE` | **Delete** | `DELETE /api/products/1` | ✅ Sim |

> **Idempotente** = chamar N vezes produz o mesmo resultado que chamar 1 vez.

---

## 📊 Richardson Maturity Model — Níveis de Maturidade REST

```mermaid
block-beta
    columns 1
    block:level3:1
        L3["🏆 Nível 3 — HATEOAS<br/>Hypermedia como motor do estado<br/>Links de navegação nas respostas<br/>API auto-descritiva"]
    end
    block:level2:1
        L2["⭐ Nível 2 — Verbos HTTP<br/>GET, POST, PUT, DELETE corretamente<br/>Status codes adequados (201, 404, etc)<br/>👉 Maioria das APIs 'REST' estão aqui"]
    end
    block:level1:1
        L1["📁 Nível 1 — Recursos<br/>URIs identificam recursos: /products, /orders<br/>Mas usa só POST para tudo"]
    end
    block:level0:1
        L0["🔥 Nível 0 — O Pântano do POX<br/>Um endpoint: POST /api<br/>Ação no body: {'action':'getProduct'}<br/>Basicamente RPC sobre HTTP"]
    end

    style L3 fill:#C8E6C9,stroke:#2E7D32
    style L2 fill:#BBDEFB,stroke:#1565C0
    style L1 fill:#FFF9C4,stroke:#F9A825
    style L0 fill:#FFCDD2,stroke:#C62828
```

---

## 🔗 Nível 3: HATEOAS — O "REST de verdade"

**HATEOAS** = Hypermedia As The Engine Of Application State

```json
// GET /api/products/42
{
  "id": 42,
  "name": "Mouse Gamer",
  "price": 159.90,
  "category": "Electronics",
  "_links": {
    "self": { "href": "/api/products/42" },
    "update": { "href": "/api/products/42", "method": "PUT" },
    "delete": { "href": "/api/products/42", "method": "DELETE" },
    "category": { "href": "/api/categories/electronics" },
    "reviews": { "href": "/api/products/42/reviews" }
  }
}
```

> O cliente **não precisa conhecer as URLs** — a API **ensina** como navegar!

### Na prática com Spring Boot (veremos no Dia 2+)

```java
// Spring HATEOAS
@GetMapping("/{id}")
public EntityModel<Product> findById(@PathVariable Long id) {
    Product product = service.findById(id);
    
    return EntityModel.of(product,
        linkTo(methodOn(ProductController.class).findById(id)).withSelfRel(),
        linkTo(methodOn(ProductController.class).findAll()).withRel("products"),
        linkTo(methodOn(ReviewController.class).findByProduct(id)).withRel("reviews")
    );
}
```

---

## 📋 Status Codes HTTP — Use corretamente!

```mermaid
flowchart TB
    subgraph s2xx["✅ 2xx — Sucesso"]
        S200["200 OK — Resposta com body"]
        S201["201 Created — Recurso criado"]
        S204["204 No Content — Deletado com sucesso"]
    end

    subgraph s4xx["⚠️ 4xx — Erro do Cliente"]
        S400["400 Bad Request — Dados inválidos"]
        S401["401 Unauthorized — Não autenticado"]
        S403["403 Forbidden — Sem permissão"]
        S404["404 Not Found — Recurso não existe"]
        S409["409 Conflict — Conflito (ex: duplicado)"]
        S422["422 Unprocessable — Validação falhou"]
    end

    subgraph s5xx["❌ 5xx — Erro do Servidor"]
        S500["500 Internal Server Error — Bug"]
        S502["502 Bad Gateway — Proxy/Gateway falhou"]
        S503["503 Service Unavailable — Fora do ar"]
    end

    style s2xx fill:#C8E6C9,stroke:#2E7D32
    style s4xx fill:#FFF9C4,stroke:#F9A825
    style s5xx fill:#FFCDD2,stroke:#C62828
```

---

## ✅ Boas Práticas para APIs RESTful

```mermaid
flowchart TB
    subgraph bp["✅ Boas Práticas RESTful"]
        direction TB
        B1["📁 Use substantivos no plural<br/>/api/products, /api/orders"]
        B2["🔢 Versionamento da API<br/>/api/v1/products<br/>ou Header: Accept-Version"]
        B3["📄 Paginação para listas<br/>?page=0&size=20&sort=name,asc"]
        B4["🔍 Filtros por query params<br/>?category=electronics&minPrice=50"]
        B5["📦 Envelope de resposta consistente<br/>{data: [...], page: {total: 100}}"]
        B6["❌ Tratamento de erro padronizado<br/>{error: 'Not Found', status: 404}"]
    end

    style bp fill:#E8F5E9,stroke:#2E7D32
    style B1 fill:#C8E6C9
    style B2 fill:#C8E6C9
    style B3 fill:#C8E6C9
    style B4 fill:#C8E6C9
    style B5 fill:#C8E6C9
    style B6 fill:#C8E6C9
```

### Exemplo: Paginação

```http
GET /api/products?page=0&size=10&sort=price,desc
```

```json
{
  "content": [
    { "id": 1, "name": "Laptop", "price": 5000.00 },
    { "id": 2, "name": "Mouse", "price": 150.00 }
  ],
  "page": {
    "number": 0,
    "size": 10,
    "totalElements": 42,
    "totalPages": 5
  }
}
```

---

## 💡 Dica do Instrutor

```
╔═══════════════════════════════════════════════════════════╗
║  A maioria das APIs do mercado está no Nível 2.           ║
║  HATEOAS (Nível 3) é bonito, mas pouco adotado.          ║
║                                                           ║
║  Foco: Verbos HTTP corretos + Status Codes + Recursos     ║
║  bem nomeados = Você já está à frente de 80% das APIs!    ║
╚═══════════════════════════════════════════════════════════╝
```
