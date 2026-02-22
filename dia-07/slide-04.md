# Slide 4: Podman Compose — Orquestrando a Stack

**Horário:** 10:15 - 10:45

---

## O Problema: Múltiplos Containers

Uma aplicação Spring Boot típica depende de **vários serviços**. Subir cada um manualmente é tedioso:

```bash
# ❌ Subir manualmente cada container
podman run -d --name postgres -e POSTGRES_DB=mydb -p 5432:5432 docker.io/library/postgres:16-alpine
podman run -d --name redis -p 6379:6379 docker.io/library/redis:7-alpine
podman run -d --name rabbitmq -p 5672:5672 -p 15672:15672 docker.io/library/rabbitmq:3-management-alpine
podman build -t my-app .
podman run -d --name app -p 8080:8080 --network podman my-app

# 😰 E se precisar reiniciar? E as variáveis de ambiente? E a ordem?
```

```bash
# ✅ Com Podman Compose — UM comando
podman compose up -d
# 🎉 Tudo sobe na ordem certa, com health checks, em 30 segundos
```

---

## podman-compose.yml — Anatomia

```mermaid
graph TB
    subgraph "podman-compose.yml"
        direction TB

        subgraph "services:"
            APP["🖥️ app<br/>build: .<br/>depends_on: postgres, redis, rabbitmq<br/>ports: 8080:8080"]
            PG[("🐘 postgres<br/>image: postgres:16-alpine<br/>ports: 5432:5432<br/>healthcheck: pg_isready")]
            REDIS[("🔴 redis<br/>image: redis:7-alpine<br/>ports: 6379:6379<br/>healthcheck: redis-cli ping")]
            RMQ["🐰 rabbitmq<br/>image: rabbitmq:3-management<br/>ports: 5672, 15672<br/>healthcheck: rabbitmq-diagnostics"]
        end

        subgraph "volumes:"
            V1["pg_data — persiste dados PostgreSQL"]
            V2["redis_data — persiste dados Redis"]
            V3["rabbitmq_data — persiste filas"]
        end

        subgraph "networks:"
            N1["app-network — comunicação entre containers"]
        end
    end

    APP -->|"SPRING_DATASOURCE_URL"| PG
    APP -->|"SPRING_DATA_REDIS_HOST"| REDIS
    APP -->|"SPRING_RABBITMQ_HOST"| RMQ

    style APP fill:#3498db,color:#fff
    style PG fill:#336791,color:#fff
    style REDIS fill:#dc382d,color:#fff
    style RMQ fill:#ff6600,color:#fff
```

---

## Podman Compose Completo — Exemplo

```yaml
version: '3.9'

services:
  # ── Aplicação Spring Boot ──
  app:
    build: .                           # Usa o Containerfile local
    container_name: my-app
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/appdb
      SPRING_DATASOURCE_USERNAME: app
      SPRING_DATASOURCE_PASSWORD: app123
      SPRING_DATA_REDIS_HOST: redis
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      postgres:
        condition: service_healthy     # Espera postgres estar ready
      redis:
        condition: service_healthy
      rabbitmq:
        condition: service_healthy
    networks:
      - app-network

  # ── PostgreSQL ──
  postgres:
    image: postgres:16-alpine
    container_name: app-postgres
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: app
      POSTGRES_PASSWORD: app123
    ports:
      - "5432:5432"
    volumes:
      - pg_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d appdb"]
      interval: 5s
      timeout: 5s
      retries: 5
    networks:
      - app-network

  # ── Redis ──
  redis:
    image: redis:7-alpine
    container_name: app-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 5s
      retries: 5
    networks:
      - app-network

  # ── RabbitMQ ──
  rabbitmq:
    image: rabbitmq:3-management-alpine
    container_name: app-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    ports:
      - "5672:5672"
      - "15672:15672"
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "check_port_connectivity"]
      interval: 10s
      timeout: 10s
      retries: 5
    networks:
      - app-network

volumes:
  pg_data:
  redis_data:
  rabbitmq_data:

networks:
  app-network:
    driver: bridge
```

---

## depends_on + Health Checks

Sem health check, o `depends_on` apenas garante que o container **iniciou**, não que o serviço está **pronto**:

```mermaid
sequenceDiagram
    participant DC as Podman Compose
    participant PG as PostgreSQL
    participant APP as Spring Boot

    DC->>PG: Inicia container postgres
    Note over PG: Container iniciado<br/>mas PostgreSQL ainda<br/>carregando dados...

    alt "depends_on" simples (sem health check)
        DC->>APP: Inicia container app IMEDIATAMENTE
        APP->>PG: Tenta conectar
        PG-->>APP: ❌ Connection refused!
        Note over APP: 💥 App crash!
    end

    alt "depends_on" com condition: service_healthy
        loop Health check a cada 5s
            DC->>PG: pg_isready -U app ?
            PG-->>DC: ❌ Não está pronto
        end
        DC->>PG: pg_isready -U app ?
        PG-->>DC: ✅ Pronto!
        DC->>APP: Agora sim inicia o app
        APP->>PG: Conecta
        PG-->>APP: ✅ Connection OK
    end
```

---

## Volumes — Persistindo Dados

Sem volumes, os dados são **perdidos** quando o container para:

```mermaid
graph TD
    subgraph "Sem volume"
        C1["Container PostgreSQL"] -->|"podman compose down"| LOST["🔴 Dados PERDIDOS<br/>Tabelas, registros... tudo zerado"]
    end

    subgraph "Com volume"
        C2["Container PostgreSQL"] -->|"podman compose down"| V["📁 Volume: pg_data<br/>Dados persistem no disco"]
        V -->|"podman compose up"| C3["Novo container<br/>✅ Dados intactos!"]
    end

    style LOST fill:#e74c3c,color:#fff
    style V fill:#2ecc71,color:#fff
```

---

## Networks — Comunicação entre Containers

Dentro da mesma network Podman, containers se comunicam pelo **nome do serviço**:

```yaml
# No Podman Compose, o nome do serviço vira o hostname
# A app acessa o banco por "postgres" (não "localhost"!)
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/appdb
  #                                       ^^^^^^^^
  #                                       Nome do serviço no podman-compose.yml
```

```mermaid
graph LR
    subgraph "Podman Network: app-network"
        APP["app<br/>:8080"] -->|"postgres:5432"| PG["postgres<br/>:5432"]
        APP -->|"redis:6379"| RED["redis<br/>:6379"]
        APP -->|"rabbitmq:5672"| RMQ["rabbitmq<br/>:5672"]
    end

    USER["Cliente<br/>(Browser/Postman)"] -->|"localhost:8080"| APP

    style APP fill:#3498db,color:#fff
    style PG fill:#336791,color:#fff
    style RED fill:#dc382d,color:#fff
    style RMQ fill:#ff6600,color:#fff
```

---

## Comandos Podman Compose Essenciais

| Comando | Função |
|---------|--------|
| `podman compose up -d` | Sobe todos os serviços em background |
| `podman compose down` | Para e remove todos os containers |
| `podman compose ps` | Lista containers rodando |
| `podman compose logs -f app` | Acompanha logs de um serviço |
| `podman compose restart app` | Reinicia um serviço |
| `podman compose build` | Reconstrói imagens |
| `podman compose up -d --build` | Sobe reconstruindo a imagem |
| `podman compose down -v` | Para e remove containers + volumes |

---

## 🎯 Quiz Rápido

1. **Por que usar `condition: service_healthy` no `depends_on`?**
   - Garante que o serviço está realmente pronto (não apenas que o container iniciou).

2. **Se a app roda no container Podman, ela acessa o PostgreSQL por `localhost:5432`?**
   - **Não!** Dentro do Podman, usa o nome do serviço: `postgres:5432`. `localhost` se refere ao próprio container da app.

3. **O que acontece com os dados se eu rodar `podman compose down -v`?**
   - O `-v` remove os **volumes**, então todos os dados do banco, Redis e RabbitMQ são **apagados**.
