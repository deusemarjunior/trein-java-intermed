# Slide 2: Docker Compose — Setup do Ambiente

**Horário:** 09:15 - 09:45

---

## 🐳 Por que Docker Compose neste dia?

Hoje trabalhamos com **3 serviços de infraestrutura** que precisam rodar localmente:

| Serviço | Porta | Finalidade | Protocolo |
|---------|-------|-----------|-----------|
| **PostgreSQL** | 5432 | Banco de dados relacional (substituindo H2) | TCP/SQL |
| **RabbitMQ** | 5672 / 15672 | Message broker (filas assíncronas) | AMQP / HTTP |
| **Redis** | 6379 | Cache em memória (key-value) | RESP |

> Instalar tudo manualmente? **Não.** Um `docker compose up -d` e tudo sobe em 30 segundos.

### 🏗️ Container vs. Instalação Local

```mermaid
graph LR
    subgraph "❌ Instalação Manual"
        I1["Baixar PostgreSQL 16"] --> I2["Configurar porta, user, senha"]
        I2 --> I3["Baixar RabbitMQ + Erlang"]
        I3 --> I4["Configurar plugins"]
        I4 --> I5["Baixar Redis (Windows WSL)"]
        I5 --> I6["⏱️ ~1 hora + erros de versão"]
    end

    subgraph "✅ Docker Compose"
        D1["docker compose up -d"] --> D2["3 containers rodando<br/>⏱️ ~30 segundos"]
    end

    style I6 fill:#e74c3c,color:#fff
    style D2 fill:#2ecc71,color:#fff
```

### 🌐 Rede dos Containers — Como se comunicam

```mermaid
graph TB
    subgraph "Docker Network (bridge padrão)"
        direction TB
        APP["🖥️ Spring Boot App<br/>(host: localhost)"]

        subgraph "Container: postgres-dia06"
            PG[("PostgreSQL 16<br/>porta interna: 5432")]
        end

        subgraph "Container: rabbitmq-dia06"
            RMQ["RabbitMQ 3<br/>AMQP: 5672<br/>UI: 15672"]
        end

        subgraph "Container: redis-dia06"
            RED[("Redis 7<br/>porta interna: 6379")]
        end
    end

    APP -->|"jdbc:postgresql://localhost:5432"| PG
    APP -->|"amqp://localhost:5672"| RMQ
    APP -->|"redis://localhost:6379"| RED

    style PG fill:#336791,color:#fff
    style RMQ fill:#ff6600,color:#fff
    style RED fill:#dc382d,color:#fff
```

> **Nota**: A aplicação Spring Boot roda no host (sua máquina) e se conecta aos containers via portas mapeadas (`ports:`). Dentro da rede Docker, containers se comunicam pelo nome do serviço.

---

## docker-compose.yml — Os 3 Serviços

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: postgres-dia06
    environment:
      POSTGRES_DB: employeedb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"        # host:container
    volumes:
      - postgres_data:/var/lib/postgresql/data   # dados persistem entre restarts
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5

  rabbitmq:
    image: rabbitmq:3-management-alpine
    container_name: rabbitmq-dia06
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    ports:
      - "5672:5672"    # AMQP (protocolo de mensageria)
      - "15672:15672"  # Management UI (interface web)
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "check_running"]
      interval: 10s
      timeout: 10s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: redis-dia06
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:      # Volume nomeado — dados sobrevivem a docker compose down
```

### Anatomia do docker-compose.yml

```mermaid
graph LR
    DC["📄 docker-compose.yml"]

    DC --> SVC["services:"]
    DC --> VOLS["volumes:"]

    SVC --> PG["🐘 postgres"]
    SVC --> RMQ["🐰 rabbitmq"]
    SVC --> RED["🔴 redis"]

    PG --> IMG1["image: postgres:16-alpine"]
    PG --> ENV1["environment:<br/>POSTGRES_DB<br/>POSTGRES_USER<br/>POSTGRES_PASSWORD"]
    PG --> PORT1["ports: 5432:5432"]
    PG --> VOL1["volumes: postgres_data"]
    PG --> HC1["healthcheck:<br/>pg_isready -U postgres"]

    RMQ --> IMG2["image: rabbitmq:3-management"]
    RMQ --> PORT2["ports:<br/>5672 — AMQP<br/>15672 — Management UI"]
    RMQ --> HC2["healthcheck:<br/>rabbitmq-diagnostics"]

    RED --> IMG3["image: redis:7-alpine"]
    RED --> PORT3["ports: 6379:6379"]
    RED --> HC3["healthcheck:<br/>redis-cli ping"]

    VOLS --> VPG["postgres_data:<br/>Persiste dados entre restarts"]

    style DC fill:#2496ed,color:#fff
    style PG fill:#336791,color:#fff
    style RMQ fill:#ff6600,color:#fff
    style RED fill:#dc382d,color:#fff
    style VOLS fill:#555,color:#fff
    style VPG fill:#777,color:#fff
```

| Propriedade | O que faz | Exemplo |
|-------------|----------|---------|
| `image` | Imagem Docker Hub a usar | `postgres:16-alpine` (leve, ~80MB) |
| `container_name` | Nome do container (para `docker exec`) | `postgres-dia06` |
| `environment` | Variáveis de ambiente | `POSTGRES_DB=employeedb` |
| `ports` | Mapeamento host:container | `"5432:5432"` |
| `volumes` | Persistência de dados | `postgres_data:/var/lib/postgresql/data` |
| `healthcheck` | Verificação de saúde | Comando que retorna 0 se saudável |

---

## 🚀 Hands-on: Subindo os Containers

### Ciclo de Vida dos Containers

```mermaid
stateDiagram-v2
    [*] --> Criado: docker compose up -d
    Criado --> Rodando: container starts
    Rodando --> Healthy: healthcheck passa
    Healthy --> Rodando: healthcheck falha
    Rodando --> Parado: docker compose stop
    Parado --> Rodando: docker compose start
    Rodando --> Removido: docker compose down
    Removido --> [*]

    note right of Healthy
        Pronto para receber conexões
    end note

    note right of Removido
        -v remove volumes (dados)
    end note
```

### Comandos Docker Compose Essenciais

| Comando | O que faz | Quando usar |
|---------|----------|-------------|
| `docker compose up -d` | Cria e inicia containers em background | Início do dia |
| `docker compose ps` | Lista containers e status | Verificar se tudo está up |
| `docker compose logs -f` | Mostra logs em tempo real | Debugar problemas |
| `docker compose stop` | Para containers (mantém dados) | Pausa rápida |
| `docker compose start` | Reinicia containers parados | Retomar trabalho |
| `docker compose down` | Remove containers (mantém volumes) | Fim do dia |
| `docker compose down -v` | Remove tudo, incluindo dados | Recomeçar do zero |
| `docker compose restart` | Para e inicia containers | Aplicar mudanças |

### Passo 1: Subir tudo

```bash
cd 06-persistence-messaging-demo
docker compose up -d
```

### Passo 2: Verificar status

```bash
docker compose ps
```

Saída esperada:
```
NAME               IMAGE                         STATUS
postgres-dia06     postgres:16-alpine            Up (healthy)
rabbitmq-dia06     rabbitmq:3-management-alpine  Up (healthy)
redis-dia06        redis:7-alpine                Up (healthy)
```

### Passo 3: Testar cada serviço

```bash
# PostgreSQL
docker exec -it postgres-dia06 psql -U postgres -d employeedb -c "SELECT 1"

# RabbitMQ — acessar http://localhost:15672 (guest/guest)

# Redis
docker exec -it redis-dia06 redis-cli ping
# Resposta: PONG
```

---

## 🔍 RabbitMQ Management UI

Acesse **http://localhost:15672** com `guest/guest`:

```mermaid
graph TB
    subgraph "RabbitMQ Management UI — Abas Principais"
        OV["📊 Overview<br/>Conexões, canais, filas<br/>Taxas de publicação/consumo"]
        CONN["🔗 Connections<br/>Apps Spring conectadas"]
        CH["📡 Channels<br/>Canais AMQP abertos"]
        EX["🔀 Exchanges<br/>Roteadores de mensagens<br/>(employee-events)"]
        QU["📬 Queues<br/>Filas com mensagens<br/>(employee-notifications)"]
    end

    OV --> CONN --> CH --> EX --> QU

    style EX fill:#ff6600,color:#fff
    style QU fill:#3498db,color:#fff
```

| Aba | O que mostra | O que observar |
|-----|-------------|----------------|
| **Overview** | Taxas de publicação/consumo, total de mensagens | Gráficos de Message rates |
| **Connections** | Aplicações Spring conectadas ao broker | Nome da app, IP, estado |
| **Channels** | Canais abertos por conexão | Prefetch count, consumers |
| **Exchanges** | Exchanges criados (employee-events, order-events) | Type: direct, bindings |
| **Queues** | Filas com contagem de mensagens pendentes | Ready, Unacked, Total |

> **Durante o exercício**: vocês verão as mensagens aparecendo na aba **Queues** em tempo real.

---

## Verificando o Redis via CLI

```bash
# Conectar ao Redis CLI dentro do container
docker exec -it redis-dia06 redis-cli

# Testar conexão
PING
# Resposta: PONG

# Listar todas as chaves (vazio no início)
KEYS *
# (empty array)

# Ver informações do servidor
INFO server
# redis_version:7.x.x
# os: Linux ...
```

```mermaid
graph LR
    CLI["redis-cli"] -->|"PING"| RED[("Redis 7")]
    RED -->|"PONG ✅"| CLI
    CLI -->|"KEYS *"| RED
    RED -->|"(empty array)"| CLI
    CLI -->|"SET nome 'valor'"| RED
    RED -->|"OK"| CLI
    CLI -->|"GET nome"| RED
    RED -->|"'valor'"| CLI

    style RED fill:#dc382d,color:#fff
```

---

## Configuração do application.yml

### Mapa de Conexões — Cada property conecta a um serviço

```mermaid
graph LR
    subgraph "application.yml"
        DS["spring.datasource<br/>url, username, password"]
        JPA["spring.jpa<br/>ddl-auto: validate, show-sql"]
        RMQ["spring.rabbitmq<br/>host, port, user, pass"]
        RDS["spring.data.redis<br/>host, port"]
        CACHE["spring.cache<br/>type: redis, TTL"]
        FLY["spring.flyway<br/>enabled, locations"]
    end

    DS -->|"JDBC"| PG[("PostgreSQL<br/>:5432")]
    JPA -->|"Hibernate"| PG
    FLY -->|"Migrations"| PG
    RMQ -->|"AMQP"| MQ["RabbitMQ<br/>:5672"]
    RDS -->|"RESP"| RED[("Redis<br/>:6379")]
    CACHE --> RED

    style PG fill:#336791,color:#fff
    style MQ fill:#ff6600,color:#fff
    style RED fill:#dc382d,color:#fff
```

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/employeedb
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate  # Flyway cuida do schema!
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

  data:
    redis:
      host: localhost
      port: 6379

  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutos

  flyway:
    enabled: true
    locations: classpath:db/migration
```

---

## ⚠️ Troubleshooting

| Problema | Causa provável | Solução |
|----------|---------------|---------|
| `Connection refused` no PostgreSQL | Container não está rodando | `docker compose up -d` |
| Porta 5432 ocupada | Outro PostgreSQL rodando | `docker ps` e parar o outro container |
| RabbitMQ UI não abre | Porta 15672 não mapeada | Verificar `docker-compose.yml` |
| Redis `PONG` não responde | Container parado | `docker compose restart redis` |

```bash
# Comando útil: reiniciar tudo do zero
docker compose down -v && docker compose up -d
```

---

## ✅ Checklist de Verificação

Antes de prosseguir, confirme que todos os 3 serviços estão **healthy**:

- [ ] `docker compose ps` mostra 3 containers "Up (healthy)"
- [ ] PostgreSQL responde: `docker exec -it postgres-dia06 psql -U postgres -c "SELECT 1"`
- [ ] RabbitMQ UI acessível: http://localhost:15672
- [ ] Redis responde: `docker exec -it redis-dia06 redis-cli ping`

> **Tudo OK? Vamos para o conteúdo principal: o problema N+1!** 🚀
