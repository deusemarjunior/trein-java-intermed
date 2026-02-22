# Slide 1: Abertura e Recap do Dia 5

**Horário:** 09:00 - 09:15

---

## 📝 Recapitulando o Dia 5

No Dia 5 aprendemos a **conectar serviços e proteger APIs**:

- ✓ **Feign Client** — Consumo declarativo de APIs externas (interface + anotações)
- ✓ **Resilience4j** — Retry, Circuit Breaker e Fallback (tolerância a falhas)
- ✓ **CORS** — Permitindo acesso de frontends em domínios diferentes
- ✓ **Spring Security + JWT** — Autenticação stateless com token
- ✓ **OpenAPI/Swagger** — Documentação interativa e testável no browser

> **Hoje vamos otimizar performance, versionar banco, desacoplar processos e cachear dados!**

### 🧠 Revisão Rápida — Associe os Conceitos

| Dia | Tema Central | Resultado |
|-----|-------------|-----------|
| **Dia 1** | Fundamentos Java Moderno | Records, Sealed Classes, Streams — linguagem expressiva |
| **Dia 2** | Persistência e REST | Spring Data JPA, APIs REST — dados acessíveis |
| **Dia 3** | Qualidade do Código | Clean Code, Arquitetura limpa — código sustentável |
| **Dia 4** | Testes Automatizados | JUnit 5, Mockito, Testcontainers — confiança para evoluir |
| **Dia 5** | Integração e Segurança | Feign, JWT, Swagger — API de produção |
| **Dia 6** | **Persistência e Mensageria** | N+1, Flyway, RabbitMQ, Redis — **performance e desacoplamento** |

---

## 🔗 Conexão entre os Dias — A Jornada do Desenvolvedor

```mermaid
flowchart TD
    D1["<b>Dia 1 — Fundamentos da Linguagem</b><br/>☕ Records, Sealed Classes<br/>Text Blocks, Pattern Matching<br/>Stream API"]

    D1 -->|"Records usados como DTOs<br/>Streams para transformar dados"| D2

    D2["<b>Dia 2 — Persistência e APIs REST</b><br/>🗄️ Spring Data JPA<br/>Queries, Paginação, Sorting<br/>REST Controllers, DTOs"]

    D2 -->|"API REST criada<br/>precisa de qualidade"| D3

    D3["<b>Dia 3 — Qualidade do Código</b><br/>🏛️ Clean Code, SOLID<br/>Arquitetura Hexagonal<br/>Validação, Error Handling"]

    D3 -->|"Código limpo<br/>é código testável"| D4

    D4["<b>Dia 4 — Testes Automatizados</b><br/>🧪 JUnit 5, Mockito<br/>Testcontainers, AssertJ<br/>Data Builders, TDD"]

    D4 -->|"API testada e validada<br/>pronta para integrar e proteger"| D5

    D5["<b>Dia 5 — Integração e Segurança</b><br/>🔒 Feign Client + Resilience4j<br/>Spring Security + JWT<br/>CORS + OpenAPI/Swagger"]

    D5 -->|"API segura e integrada<br/>agora precisa de performance"| D6

    D6["<b>⭐ Dia 6 — Persistência e Mensageria</b><br/>⚡ N+1, JOIN FETCH, @EntityGraph<br/>📦 Flyway Migrations<br/>🐰 RabbitMQ + 🔴 Redis"]

    style D1 fill:#4a90d9,color:#fff,stroke:#2c6fad
    style D2 fill:#4a90d9,color:#fff,stroke:#2c6fad
    style D3 fill:#4a90d9,color:#fff,stroke:#2c6fad
    style D4 fill:#4a90d9,color:#fff,stroke:#2c6fad
    style D5 fill:#4a90d9,color:#fff,stroke:#2c6fad
    style D6 fill:#1dd1a1,color:#fff,stroke:#10ac84
```

---

## 🧩 Mapa Mental do Dia 6 — Todos os Conceitos

```mermaid
mindmap
  root((Dia 6<br/>Persistência<br/>e Mensageria))
    JPA Avançado
      Problema N+1
        Lazy Loading
        LAZY vs EAGER
        Diagnóstico show-sql
      Soluções
        JOIN FETCH — JPQL
        EntityGraph — Declarativo
        Projeção DTO — Record
        BatchSize — Lotes
      Paginação
        Pageable
        Page vs Slice
        Sort
    Flyway
      Versionamento SQL
      Nomenclatura Vn__desc
      flyway_schema_history
      ddl-auto validate
      Rollback manual
    RabbitMQ
      Mensageria Assíncrona
      Protocolo AMQP
      Exchange Direct
      Queue durable
      Binding + Routing Key
      Producer — RabbitTemplate
      Consumer — RabbitListener
      Jackson2Json Converter
    Redis
      Cache in-memory
      Key-Value Store
      Cacheable
      CacheEvict
      CachePut
      TTL — Time to Live
      Serialização JSON
```

### Por que Persistência Avançada e Mensageria são o próximo passo?

| O que fizemos nos dias anteriores | A lacuna | O que aprendemos hoje |
|-----------------------------------|----------|----------------------|
| Spring Data JPA com queries | findAll() esconde N+1 queries | **JOIN FETCH** e **@EntityGraph** |
| `ddl-auto: update` no H2 | Sem controle de schema em produção | **Flyway Migrations** |
| API síncrona bloqueante | Processos demorados travam a resposta | **RabbitMQ** — mensageria assíncrona |
| Toda consulta bate no banco | Dados frequentes consultados toda hora | **Redis** — cache em memória |

---

## 🎯 Objetivos do Dia 6

Ao final deste dia, o aluno será capaz de:

1. **N+1** — Identificar e resolver o problema de performance N+1 no JPA
2. **JOIN FETCH / @EntityGraph** — Carregar relacionamentos de forma eficiente
3. **Projeções DTO** — Retornar apenas os campos necessários direto do banco
4. **Flyway** — Versionar o schema do banco com migrations SQL incrementais
5. **RabbitMQ** — Publicar e consumir mensagens assíncronas entre serviços
6. **Redis** — Cachear dados frequentes com TTL e invalidação automática

### 📊 Taxonomia de Bloom — Nível de Aprendizagem

```mermaid
graph LR
    L1["1. Lembrar<br/>Conceitos-chave"]
    L2["2. Entender<br/>Por quê cada técnica"]
    L3["3. Aplicar<br/>Implementar no código"]
    L4["4. Analisar<br/>Diagnosticar N+1, logs"]
    L5["5. Avaliar<br/>Escolher a melhor solução"]

    L1 --> L2 --> L3 --> L4 --> L5

    style L1 fill:#3498db,color:#fff
    style L2 fill:#2ecc71,color:#fff
    style L3 fill:#f39c12,color:#fff
    style L4 fill:#e74c3c,color:#fff
    style L5 fill:#9b59b6,color:#fff
```

> Hoje vamos do **nível 1 ao 5** — vocês vão entender os conceitos, implementar no código, diagnosticar problemas e decidir quando usar cada ferramenta.

---

## 🏗️ O que vamos construir

```mermaid
graph TB
    subgraph "Employee API (porta 8090/8091)"
        CTL["EmployeeController<br/>GET /api/employees?page=0&size=10"]
        SVC["EmployeeService<br/>create(), findAll()"]
        REPO["EmployeeRepository<br/>JOIN FETCH + @EntityGraph"]
        PUB["EmployeeEventPublisher<br/>RabbitTemplate.convertAndSend()"]
        CACHE["DepartmentService<br/>@Cacheable + @CacheEvict"]
    end

    subgraph "PostgreSQL (container)"
        DB[("PostgreSQL<br/>+ Flyway Migrations")]
    end

    subgraph "RabbitMQ (container)"
        EXCH["Exchange<br/>employee-events"]
        QUEUE["Queue<br/>employee-notifications"]
        CONS["EmployeeNotificationConsumer<br/>@RabbitListener"]
    end

    subgraph "Redis (container)"
        REDIS[("Redis<br/>Cache departments")]
    end

    CTL --> SVC
    SVC --> REPO
    REPO --> DB
    SVC --> PUB
    PUB --> EXCH
    EXCH --> QUEUE
    QUEUE --> CONS
    CACHE --> REDIS
    CACHE --> DB

    style DB fill:#336791,color:#fff
    style REDIS fill:#dc382d,color:#fff
    style EXCH fill:#ff6600,color:#fff
    style QUEUE fill:#ff6600,color:#fff
```

### 🔌 Stack Tecnológica do Dia

```mermaid
graph LR
    subgraph "Aplicação Java"
        JAVA["Java 21"]
        SB["Spring Boot 3.2"]
        JPA["Spring Data JPA<br/>Hibernate 6"]
        AMQP["Spring AMQP"]
        CACHE["Spring Cache"]
        FW["Flyway"]
    end

    subgraph "Infraestrutura (Docker)"
        PG["PostgreSQL 16"]
        RMQ["RabbitMQ 3"]
        RED["Redis 7"]
    end

    JPA --> PG
    FW --> PG
    AMQP --> RMQ
    CACHE --> RED

    style JAVA fill:#5382a1,color:#fff
    style SB fill:#6db33f,color:#fff
    style PG fill:#336791,color:#fff
    style RMQ fill:#ff6600,color:#fff
    style RED fill:#dc382d,color:#fff
```

### Dois projetos, um padrão

| Projeto | Porta | Papel | Objetivo |
|---------|-------|-------|----------|
| `06-persistence-messaging-demo` | 8090 | **Demonstração** (professor) | Referência completa com todos os conceitos |
| `06-employee-api-advanced` | 8091 | **Exercício** (aluno) | 8 TODOs para implementar passo a passo |

---

## 📎 Agenda Detalhada

| # | Horário | Slide | Conteúdo |
|---|---------|-------|----------|
| 1 | 09:00 | Este slide | Recap + Introdução |
| 2 | 09:15 | Slide 2 | Docker Compose — Setup do Ambiente |
| 3 | 09:45 | Slide 3 | JPA N+1 — O Problema e Diagnóstico |
| 4 | 10:15 | Slide 4 | JPA N+1 — Soluções (JOIN FETCH, @EntityGraph) |
| - | 10:45 | ☕ | Coffee Break |
| 5 | 11:00 | Slide 5 | Projeções DTO e Paginação |
| 6 | 11:30 | Slide 6 | Migrations com Flyway |
| 7 | 12:00 | 🍽️ | Almoço |
| 8 | 13:00 | Slide 7 | Mensageria — Conceitos |
| 9 | 13:10 | Slide 8 | RabbitMQ — Producer e Consumer |
| 10 | 13:20 | Slide 9 | Cache com Redis |
| 11 | 13:40 | Slide 10 | Walkthrough Demo |
| 12 | 13:50 | Slide 11 | Exercício (TODOs 1-2) |
| 13 | 14:30 | Slide 12 | Exercício (TODOs 3-4) |
| 14 | 15:10 | Slide 13 | Exercício (TODO 5) |
| 15 | 15:30 | Slide 14 | Exercício (TODOs 6-7) |
| 16 | 16:10 | Slide 15 | Exercício (TODO 8) |
| 17 | 16:30 | Slide 16 | Review + Q&A |
