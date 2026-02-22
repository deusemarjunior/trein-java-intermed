# Slide 10: Walkthrough — 06-persistence-messaging-demo

**Horário:** 13:40 - 13:50

---

## Objetivo do Walkthrough

> **"Mostrar código real é o que convence."**  
> — Pragmatic Programmer

Projeto completo demonstrando **todos os conceitos do Dia 6** integrados em uma única aplicação. Vamos percorrer cada camada juntos.

### Visão Geral da Arquitetura

```mermaid
graph TB
    subgraph "06-persistence-messaging-demo (porta 8090)"
        direction TB
        CTL["OrderController<br/>GET /api/orders (paginado)<br/>POST /api/orders"]
        CAT_CTL["CategoryController<br/>GET /api/categories"]
        SVC["OrderService<br/>create(), findAll()"]
        CAT_SVC["CategoryService<br/>@Cacheable / @CacheEvict"]
        REPO["OrderRepository<br/>JOIN FETCH + @EntityGraph"]
        PUB["OrderEventPublisher<br/>RabbitTemplate"]
        CONS["OrderNotificationConsumer<br/>@RabbitListener"]
    end

    subgraph "Docker Compose"
        DB[("PostgreSQL :5432<br/>+ Flyway Migrations")]
        MQ["RabbitMQ :5672<br/>Exchange: order-events"]
        REDIS[("Redis :6379<br/>Cache: categories")]
    end

    CTL --> SVC
    CAT_CTL --> CAT_SVC
    SVC --> REPO
    SVC --> PUB
    REPO --> DB
    PUB --> MQ
    MQ --> CONS
    CAT_SVC --> REDIS
    CAT_SVC --> DB

    style DB fill:#336791,color:#fff
    style MQ fill:#ff6600,color:#fff
    style REDIS fill:#dc382d,color:#fff
```

### Mapa de Conceitos → Código

```mermaid
graph LR
    subgraph "Conceito"
        C1["N+1 & JOIN FETCH"]
        C2["DTO Projection"]
        C3["Paginação"]
        C4["Flyway"]
        C5["RabbitMQ"]
        C6["Redis Cache"]
    end

    subgraph "Arquivo"
        F1["OrderRepository.java"]
        F2["OrderSummary.java (Record)"]
        F3["OrderController.java"]
        F4["db/migration/V1-V4"]
        F5["OrderEventPublisher.java<br/>OrderNotificationConsumer.java"]
        F6["CategoryService.java<br/>CacheConfig.java"]
    end

    C1 --> F1
    C2 --> F2
    C3 --> F3
    C4 --> F4
    C5 --> F5
    C6 --> F6

    style C1 fill:#3498db,color:#fff
    style C2 fill:#2ecc71,color:#fff
    style C3 fill:#9b59b6,color:#fff
    style C4 fill:#f39c12,color:#fff
    style C5 fill:#ff6600,color:#fff
    style C6 fill:#dc382d,color:#fff
```

---

## Demo 1: Docker Compose — Setup

```bash
cd 06-persistence-messaging-demo
docker compose up -d
docker compose ps  # verificar 3 containers healthy
```

### O que observar

```mermaid
graph LR
    DC["docker compose up -d"]
    DC --> PG["PostgreSQL<br/>:5432 → Ready"]
    DC --> RB["RabbitMQ<br/>:5672 → Ready<br/>:15672 → Management UI"]
    DC --> RD["Redis<br/>:6379 → Ready"]

    style PG fill:#336791,color:#fff
    style RB fill:#ff6600,color:#fff
    style RD fill:#dc382d,color:#fff
```

**Validação:**
```bash
# Verificar que todos estão healthy
docker compose ps
# NAME          STATUS          PORTS
# postgres      Up (healthy)    5432
# rabbitmq      Up (healthy)    5672, 15672
# redis         Up (healthy)    6379
```

---

## Demo 2: Flyway Migrations

```
src/main/resources/db/migration/
├── V1__create_categories.sql       ← Tabela de categorias
├── V2__create_orders.sql           ← Tabela de pedidos (FK → categories)
├── V3__create_order_items.sql      ← Tabela de itens (FK → orders)
└── V4__insert_initial_data.sql     ← Dados iniciais de teste
```

> Ao subir a aplicação, o Flyway executa as migrations automaticamente. Verifique nos logs:

```
Flyway: Migrating schema "public" to version 1 - create categories
Flyway: Migrating schema "public" to version 2 - create orders
Flyway: Migrating schema "public" to version 3 - create order items
Flyway: Migrating schema "public" to version 4 - insert initial data
Flyway: Successfully applied 4 migrations
```

### O que acontece internamente

```mermaid
sequenceDiagram
    participant App as Spring Boot
    participant FW as Flyway
    participant DB as PostgreSQL

    App->>FW: Startup → Flyway autoconfigure
    FW->>DB: SELECT * FROM flyway_schema_history
    DB-->>FW: (tabela não existe)
    FW->>DB: CREATE TABLE flyway_schema_history
    FW->>DB: V1__create_categories.sql ✅
    FW->>DB: V2__create_orders.sql ✅
    FW->>DB: V3__create_order_items.sql ✅
    FW->>DB: V4__insert_initial_data.sql ✅
    FW->>DB: INSERT INTO flyway_schema_history (4 registros)
    FW-->>App: Migrations concluídas
    App->>App: Continua startup (JPA valida schema)
```

---

## Demo 3: N+1 — Antes e Depois

### Endpoint com N+1 (propositalmente ruim)

```
GET /api/orders/n-plus-one
```

📋 **O que observar nos logs SQL:**

```sql
-- 1 query principal
SELECT o.id, o.customer_name, o.total FROM orders o

-- N queries adicionais (uma para cada order)
SELECT c.id, c.name FROM categories c WHERE c.id = ?
SELECT c.id, c.name FROM categories c WHERE c.id = ?
SELECT c.id, c.name FROM categories c WHERE c.id = ?
-- ... (repete para CADA order!)
```

### Endpoint otimizado (JOIN FETCH)

```
GET /api/orders
```

📋 **Comparem — UMA ÚNICA query:**

```sql
SELECT o.id, o.customer_name, o.total, c.id, c.name
FROM orders o
LEFT JOIN categories c ON o.category_id = c.id
-- Apenas 1 query! ✅
```

```mermaid
graph LR
    subgraph "❌ N+1"
        A["1 + N queries<br/>~500ms"]
    end
    subgraph "✅ JOIN FETCH"
        B["1 query com JOIN<br/>~5ms"]
    end

    A --->|"100x mais rápido!"| B

    style A fill:#e74c3c,color:#fff
    style B fill:#2ecc71,color:#fff
```

---

## Demo 4: Projeção DTO + Paginação

```
GET /api/orders?page=0&size=5&sort=createdAt,desc
```

### O que observar no response:

```json
{
  "content": [
    { "orderId": 10, "customerName": "João", "total": 299.90, "status": "COMPLETED" }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": { "orders": [{ "property": "createdAt", "direction": "DESC" }] }
  },
  "totalElements": 50,
  "totalPages": 10,
  "first": true,
  "last": false
}
```

### O que observar no SQL gerado:

```sql
-- Query com paginação automática
SELECT o.id, o.customer_name, o.total, o.status
FROM orders o
ORDER BY o.created_at DESC
LIMIT 5 OFFSET 0

-- Count query (para totalElements)
SELECT COUNT(*) FROM orders
```

---

## Demo 5: RabbitMQ — Producer/Consumer

### Criar um pedido (dispara evento)

```http
POST /api/orders
Content-Type: application/json

{
  "customerId": 1,
  "items": [
    { "productId": 1, "quantity": 2 }
  ]
}
```

### O que observar no log (sequência):

```
📤 Evento publicado: OrderCreatedEvent (orderId=11)
📧 Processando pedido 11: atualizando estoque...
✅ Estoque atualizado para pedido 11
```

```mermaid
sequenceDiagram
    participant You as Seu Request
    participant API as OrderController
    participant SVC as OrderService
    participant DB as PostgreSQL
    participant PUB as EventPublisher
    participant MQ as RabbitMQ
    participant CON as NotificationConsumer

    You->>API: POST /api/orders
    API->>SVC: create(request)
    SVC->>DB: INSERT order + items
    SVC->>PUB: publishOrderCreated(order)
    PUB->>MQ: convertAndSend(event) 📤
    SVC-->>API: OrderResponse
    API-->>You: 201 Created ✅

    Note over MQ,CON: Assíncrono (Thread separada)
    MQ->>CON: deliver(OrderCreatedEvent)
    CON->>CON: log("📧 Processando...") ✅
```

### Verificar no RabbitMQ Management UI

1. Abra **http://localhost:15672** (guest/guest)
2. **Exchanges** → `order-events` (tipo: Direct)
3. **Queues** → `order-notifications`
   - Messages Ready: 0 (consumer processou)
   - Message rates: publish/deliver

---

## Demo 6: Cache Redis — Hit/Miss

### Primeira chamada (MISS — vai ao banco)

```
GET /api/categories
```

Log:
```
🔍 Buscando categorias no banco...
Hibernate: SELECT c.id, c.name, c.description FROM categories c
```

### Segunda chamada (HIT — Redis, sem log!)

```
GET /api/categories
```

Log:
```
(nenhum log! Método nem foi executado — veio direto do Redis ⚡)
```

### Verificar no Redis CLI

```bash
docker exec -it redis-dia06 redis-cli

KEYS *
# 1) "categories::all"

GET "categories::all"
# [{"id":1,"name":"Electronics",...},...]

TTL "categories::all"
# (integer) 580  ← faltam ~9 minutos para expirar
```

```mermaid
graph LR
    REQ1["1ª GET /categories<br/>Cache MISS"] -->|"~50ms"| DB[("PostgreSQL")]
    DB -->|"Salva no cache"| REDIS[("Redis")]
    REQ2["2ª GET /categories<br/>Cache HIT ⚡"] -->|"~2ms"| REDIS

    style DB fill:#336791,color:#fff
    style REDIS fill:#dc382d,color:#fff
```

---

## 📁 Estrutura do Projeto

```
06-persistence-messaging-demo/
├── docker-compose.yml              ← PostgreSQL + RabbitMQ + Redis
├── pom.xml                         ← spring-data-jpa, amqp, redis, flyway
├── api-requests.http               ← Requests de teste (VS Code REST Client)
└── src/main/
    ├── java/com/example/demo/
    │   ├── config/                  ← RabbitMQConfig, CacheConfig
    │   ├── controller/              ← OrderController, CategoryController
    │   ├── dto/                     ← OrderSummary, OrderRequest, OrderCreatedEvent
    │   ├── exception/               ← GlobalExceptionHandler
    │   ├── messaging/               ← OrderEventPublisher, OrderNotificationConsumer
    │   ├── model/                   ← Order, OrderItem, Category
    │   ├── repository/              ← OrderRepository (JOIN FETCH, @EntityGraph)
    │   └── service/                 ← OrderService, CategoryService (@Cacheable)
    └── resources/
        ├── application.yml          ← Conexões: PG + RabbitMQ + Redis
        └── db/migration/            ← V1, V2, V3, V4 (Flyway)
```

---

## ✅ Checklist de Observação para o Aluno

| # | O que observar | Onde ver | Esperado |
|:---:|:---|:---|:---|
| 1 | Container 3x healthy | Terminal (`docker compose ps`) | STATUS: Up |
| 2 | Flyway migrations aplicadas | Log de startup da app | "Successfully applied 4 migrations" |
| 3 | N+1 queries | Console (GET /n-plus-one) | Múltiplas SELECTs |
| 4 | JOIN FETCH | Console (GET /orders) | Uma única SELECT com JOIN |
| 5 | Paginação response | Response JSON | `totalElements`, `totalPages` |
| 6 | Evento RabbitMQ publicado | Console (POST /orders) | "📤 Evento publicado" |
| 7 | Evento RabbitMQ consumido | Console | "📧 Processando..." + "✅ Atualizado" |
| 8 | RabbitMQ Management UI | http://localhost:15672 | Exchange + Queue visíveis |
| 9 | Cache MISS (1ª chamada) | Console (GET /categories) | "🔍 Buscando no banco..." |
| 10 | Cache HIT (2ª+ chamada) | Console | Nenhum log (silêncio!) |
| 11 | TTL no Redis | `redis-cli TTL "categories::all"` | Inteiro decrementando |

> **Agora é com vocês!** Vamos para o exercício `06-employee-api-advanced`.
