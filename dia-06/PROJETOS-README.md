# 📚 Projetos Java - Dia 06

## 📖 Ordem de Estudo Recomendada

### 1️⃣ **06-persistence-messaging-demo** (Projeto Completo - Demonstração)
**Objetivo**: Projeto completo demonstrando JPA avançado (N+1, JOIN FETCH, @EntityGraph), Flyway migrations, mensageria com RabbitMQ e cache com Redis.

**Conceitos**:
- Queries propositalmente com N+1 + versão corrigida com `JOIN FETCH` e `@EntityGraph`
- Projeção DTO: `OrderSummary` direto na JPQL (sem carregar entidade completa)
- Paginação: `GET /orders?page=0&size=10&sort=createdAt,desc`
- Migrations Flyway: `V1__create_categories.sql`, `V2__create_orders.sql`, `V3__create_order_items.sql`
- Producer: publica `OrderCreatedEvent` no RabbitMQ ao criar pedido
- Consumer: escuta o evento e atualiza estoque (simulado com log)
- Cache Redis: `@Cacheable` na listagem de categorias com TTL de 10 minutos
- `docker-compose.yml` com PostgreSQL + RabbitMQ + Redis

**Porta**: 8090  
**Arquivo de Testes**: `api-requests.http`

```bash
# 1. Subir os containers (PostgreSQL + RabbitMQ + Redis)
cd 06-persistence-messaging-demo
docker compose up -d

# 2. Rodar a aplicação
mvn spring-boot:run
```

**RabbitMQ Management UI**: http://localhost:15672 (guest/guest)

---

### 2️⃣ **06-employee-api-advanced** (Exercício: Persistência, Mensageria e Cache)
**Objetivo**: Otimizar persistência (N+1), adicionar migrations Flyway, mensageria com RabbitMQ e cache com Redis à API de Funcionários.

**Conceitos**:
- Identificar e corrigir o problema N+1 com `JOIN FETCH` e `@EntityGraph`
- Criar projeção DTO `EmployeeSummary` para listagem otimizada
- Adicionar paginação com `Pageable` e `Page<T>`
- Criar migrations Flyway para versionamento de schema
- Publicar `EmployeeCreatedEvent` no RabbitMQ
- Criar consumer com `@RabbitListener`
- Cachear listagem de departamentos com Redis

**Porta**: 8091  
**Arquivo de Testes**: `api-requests.http`

```bash
# 1. Subir os containers (PostgreSQL + RabbitMQ + Redis)
cd 06-employee-api-advanced
docker compose up -d

# 2. Rodar a aplicação
mvn spring-boot:run
```

**TODOs a implementar**: 8 (N+1, JOIN FETCH/@EntityGraph, Projeção DTO, Paginação, Flyway, RabbitMQ Producer, RabbitMQ Consumer, Redis Cache)

---

## 🚀 Como Usar

### 1. **Estude primeiro o projeto completo** (06-persistence-messaging-demo)
   - Suba os containers: `cd 06-persistence-messaging-demo && docker compose up -d`
   - Execute a aplicação: `mvn spring-boot:run`
   - Observe os logs SQL: veja o N+1 acontecendo e a versão corrigida
   - Crie um pedido via POST e veja o evento no RabbitMQ Management UI
   - Acesse a listagem de categorias duas vezes: a segunda vem do Redis (sem log de banco)
   - Use o `api-requests.http` para testar os endpoints

### 2. **Pratique com o exercício** (06-employee-api-advanced)
   ```bash
   cd 06-employee-api-advanced
   docker compose up -d   # sobe PostgreSQL + RabbitMQ + Redis
   mvn spring-boot:run    # verificar que sobe
   ```
   - Implemente os TODOs 1-8 na ordem
   - Após cada TODO, reinicie a aplicação e teste
   - Meta: N+1 corrigido (verificar logs SQL), filas funcionando (ver no RabbitMQ UI), cache ativo (log só na primeira chamada)

---

## ⚠️ Pré-requisitos

| Requisito | Verificação |
|-----------|-------------|
| JDK 21 | `java --version` |
| Maven 3.8+ | `mvn --version` |
| Docker Desktop | `docker --version` |
| Docker Compose | `docker compose version` |

> **IMPORTANTE**: Os projetos deste dia dependem de containers Docker (PostgreSQL, RabbitMQ, Redis). Certifique-se de que o Docker Desktop está rodando antes de iniciar.

> **DICA**: Use o arquivo `api-requests.http` de cada projeto para testar os endpoints diretamente no VS Code (extensão REST Client).
