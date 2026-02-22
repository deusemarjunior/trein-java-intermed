# Slide 4: Arquitetura Hexagonal — Revisão Aplicada

**Horário:** 09:30 - 10:00 (continuação do Planning)

---

## 🏛️ Arquitetura do `08-movie-service`

```mermaid
graph TB
    subgraph "Adapter IN (Web)"
        MC["MovieController<br/>REST API"]
        AC["AuthController<br/>Login JWT"]
    end

    subgraph "Domain (Negócio)"
        MUC["MovieUseCasePort<br/>(interface)"]
        MS["MovieService<br/>(implementação)"]
        MAP["MovieApiPort<br/>(interface)"]
        FRP["FavoriteRepositoryPort<br/>(interface)"]
    end

    subgraph "Adapter OUT (Infra)"
        TMDB["TheMovieDbAdapter<br/>Feign Client → TheMovieDB"]
        REPO["FavoriteJpaAdapter<br/>JPA → PostgreSQL"]
        CACHE["CacheAdapter<br/>Redis"]
    end

    subgraph "Infraestrutura"
        TMDB_API["🎬 TheMovieDB API"]
        PG["🐘 PostgreSQL"]
        RD["🔴 Redis"]
    end

    MC --> MUC
    MUC -.->|implementa| MS
    MS --> MAP
    MS --> FRP
    MAP -.->|implementa| TMDB
    FRP -.->|implementa| REPO
    TMDB --> TMDB_API
    REPO --> PG
    CACHE --> RD

    style MC fill:#3498db,color:#fff
    style MS fill:#2ecc71,color:#fff
    style TMDB fill:#9b59b6,color:#fff
    style REPO fill:#9b59b6,color:#fff
```

---

## 📁 Estrutura de Pacotes

```
08-movie-service/
├── docker-compose.yml          ← PostgreSQL + Redis + TheMovie Web
├── openapi.yaml                ← Contrato Swagger (do cliente)
├── src/main/java/com/example/movieservice/
│   ├── domain/                 ← Regras de negócio (sem dependências externas)
│   │   ├── model/
│   │   │   ├── Movie.java
│   │   │   ├── Favorite.java
│   │   │   └── WatchLater.java
│   │   ├── port/
│   │   │   ├── in/
│   │   │   │   └── MovieUseCasePort.java       ← TODO 3
│   │   │   └── out/
│   │   │       ├── MovieApiPort.java            ← TODO 1
│   │   │       └── FavoriteRepositoryPort.java
│   │   └── service/
│   │       └── MovieService.java                ← TODO 4
│   ├── adapter/
│   │   ├── in/
│   │   │   └── web/
│   │   │       ├── MovieController.java         ← TODO 5
│   │   │       ├── AuthController.java          ← TODO 12
│   │   │       ├── dto/
│   │   │       │   ├── MovieSearchResponse.java
│   │   │       │   ├── MovieDetailResponse.java
│   │   │       │   └── LoginRequest.java
│   │   │       └── handler/
│   │   │           └── GlobalExceptionHandler.java ← TODO 8
│   │   └── out/
│   │       ├── rest/
│   │       │   ├── TheMovieDbAdapter.java        ← TODO 2
│   │       │   └── dto/
│   │       │       ├── TmdbSearchResponse.java
│   │       │       └── TmdbMovieDetail.java
│   │       └── persistence/
│   │           ├── FavoriteJpaAdapter.java
│   │           ├── entity/
│   │           └── repository/
│   └── config/
│       ├── SecurityConfig.java
│       ├── CacheConfig.java
│       └── ResilienceConfig.java                 ← TODO 6
└── src/test/java/
    ├── MovieServiceTest.java                     ← TODO 9
    └── FavoriteRepositoryIT.java                 ← TODO 10
```

---

## 🔑 Regra de Ouro da Hexagonal

```mermaid
graph LR
    OUT_ADAPTER["Adapter OUT<br/>(Feign, JPA, Redis)"]
    DOMAIN["Domain<br/>(Service, Ports, Models)"]
    IN_ADAPTER["Adapter IN<br/>(Controller, DTOs)"]

    IN_ADAPTER -->|"depende de"| DOMAIN
    OUT_ADAPTER -->|"depende de"| DOMAIN
    DOMAIN -.->|"NÃO depende de ninguém"| DOMAIN

    style DOMAIN fill:#2ecc71,color:#fff
    style IN_ADAPTER fill:#3498db,color:#fff
    style OUT_ADAPTER fill:#9b59b6,color:#fff
```

> O **domain** nunca importa classes de Spring, JPA, Feign ou Redis. Ele só conhece **interfaces (ports)**.
