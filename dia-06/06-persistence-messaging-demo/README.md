# 06-persistence-messaging-demo

Projeto demonstração do **Dia 6** — Persistência Avançada e Mensageria.

## Tecnologias

- **Spring Data JPA** — N+1, JOIN FETCH, @EntityGraph, Projeção DTO, Pageable
- **Flyway** — Migrations SQL versionadas
- **RabbitMQ** — Mensageria assíncrona com Spring AMQP
- **Redis** — Cache com Spring Cache (@Cacheable, @CacheEvict, @CachePut)
- **PostgreSQL 16** — Banco de dados relacional
- **Docker Compose** — Infraestrutura local

## Pré-requisitos

- Java 21
- Maven 3.9+
- Docker Desktop

## Como executar

### 1. Subir infraestrutura

```bash
docker compose up -d
```

Isso inicia:
- **PostgreSQL** em `localhost:5432` (demodb / demo / demo123)
- **RabbitMQ** em `localhost:5672` (management: http://localhost:15672 — guest/guest)
- **Redis** em `localhost:6379`

### 2. Rodar a aplicação

```bash
mvn spring-boot:run
```

A API sobe na porta **8090**.

### 3. Testar

Use o arquivo `api-requests.http` ou os comandos abaixo:

```bash
# Criar pedido (dispara evento no RabbitMQ)
curl -X POST http://localhost:8090/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"João","customerEmail":"joao@email.com","categoryId":1,"items":[{"productName":"Notebook","quantity":1,"unitPrice":3999.90}]}'

# ❌ Listar com N+1 (observe os logs!)
curl http://localhost:8090/api/orders/n-plus-one

# ✅ Listar otimizado (JOIN FETCH)
curl http://localhost:8090/api/orders/optimized

# ✅ Projeção DTO com paginação
curl "http://localhost:8090/api/orders/summary?page=0&size=5"

# Categorias (com cache Redis)
curl http://localhost:8090/api/categories
curl http://localhost:8090/api/categories/1
```

## Estrutura do projeto

```
src/main/java/com/example/demo/
├── PersistenceMessagingDemoApplication.java
├── config/
│   ├── CacheConfig.java          ← Redis cache config
│   └── RabbitMQConfig.java       ← Exchange, Queue, Binding
├── controller/
│   ├── CategoryController.java   ← CRUD + cache demo
│   └── OrderController.java      ← N+1 demo + CRUD
├── dto/
│   ├── OrderCreatedEvent.java    ← Evento RabbitMQ
│   ├── OrderRequest.java         ← DTO entrada
│   ├── OrderResponse.java        ← DTO saída
│   └── OrderSummary.java         ← Projeção DTO (Record)
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── messaging/
│   ├── OrderEventPublisher.java  ← Producer RabbitMQ
│   └── OrderNotificationConsumer.java ← Consumer RabbitMQ
├── model/
│   ├── Category.java
│   ├── Order.java                ← ManyToOne + OneToMany
│   └── OrderItem.java
├── repository/
│   ├── CategoryRepository.java
│   └── OrderRepository.java     ← JOIN FETCH + @EntityGraph + DTO Projection
└── service/
    ├── CategoryService.java      ← @Cacheable, @CacheEvict, @CachePut
    └── OrderService.java         ← N+1 demo + event publishing
```

## Verificação Redis (CLI)

```bash
docker exec -it demo-redis redis-cli
KEYS *
GET categories::1
TTL categories::1
```

## Parar infraestrutura

```bash
docker compose down
```
