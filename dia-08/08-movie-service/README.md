# 08-movie-service — Microsserviço de Filmes

## 📋 Descrição

Microsserviço de Filmes com **Arquitetura Hexagonal**, integrando com a API do [TheMovieDB](https://developer.themoviedb.org/reference/getting-started) e alimentando o frontend **TheMovie Web** (React) fornecido como imagem Podman.

## 🏗️ Arquitetura

```
src/main/java/com/example/movieservice/
├── MovieServiceApplication.java
├── domain/
│   ├── model/              ← Records do domínio (Movie, MoviePage, MovieCredits)
│   ├── port/
│   │   ├── in/             ← Use cases (MovieUseCasePort)
│   │   └── out/            ← Ports de saída (MovieApiPort, FavoriteRepositoryPort)
│   ├── service/            ← Implementação das regras de negócio (MovieService)
│   └── exception/          ← Exceções de domínio
├── adapter/
│   ├── in/web/             ← Controllers REST + DTOs de entrada/saída
│   └── out/
│       ├── rest/           ← Feign Client + DTOs do TheMovieDB + Mapper
│       └── persistence/    ← Entities JPA + Repositories + Adapters
├── config/                 ← Security, CORS, Swagger
└── security/               ← JWT (JwtUtil, JwtAuthenticationFilter)
```

## 🚀 Como Executar

### 1. Obter API Key do TheMovieDB

1. Crie uma conta em https://www.themoviedb.org/signup
2. Vá em **Settings → API** e copie o **API Read Access Token** (Bearer Token)
3. Configure no `application.yml`:

```yaml
tmdb:
  api:
    key: SEU_TOKEN_AQUI
```

Ou via variável de ambiente:
```bash
export TMDB_API_KEY=seu_token_aqui
```

### 2. Subir a infraestrutura

```bash
podman compose up -d
```

Isso sobe:
- **PostgreSQL** na porta 5435
- **Redis** na porta 6382
- **TheMovie Web** (frontend React) na porta 3000

### 3. Rodar a aplicação

```bash
mvn spring-boot:run
```

### 4. Validar

- **Backend**: http://localhost:8080/actuator/health
- **Frontend**: http://localhost:3000
- **Swagger**: http://localhost:8080/swagger-ui.html

## 📝 TODOs

| # | TODO | Arquivo | Conceito |
|---|------|---------|----------|
| 1 | Criar `MovieApiPort` | `domain/port/out/MovieApiPort.java` | Port de saída (Hexagonal) |
| 2 | Implementar `TheMovieDbAdapter` | `adapter/out/rest/TheMovieDbAdapter.java` + `TheMovieDbClient.java` | Feign Client + Adapter |
| 3 | Criar `MovieUseCasePort` | `domain/port/in/MovieUseCasePort.java` | Port de entrada (Use Case) |
| 4 | Implementar `MovieService` | `domain/service/MovieService.java` | Regras de negócio |
| 5 | Criar `MovieController` | `adapter/in/web/MovieController.java` | REST endpoints |
| 6 | Configurar Resilience4j | `TheMovieDbAdapter.java` + `application.yml` | Retry + Circuit Breaker |
| 7 | Cachear filmes populares | `TheMovieDbAdapter.java` + `MovieServiceApplication.java` | Redis `@Cacheable` |
| 8 | `GlobalExceptionHandler` | `adapter/in/web/GlobalExceptionHandler.java` | Problem Details RFC 7807 |
| 9 | Testes unitários | `test/.../MovieServiceTest.java` | JUnit 5 + Mockito |
| 10 | Testes de integração | `test/.../FavoriteRepositoryIT.java` | Testcontainers |
| 11 | Documentar com Swagger | `MovieController.java` | `@Operation`, `@ApiResponse` |
| 12 | Segurança JWT | `AuthController.java` + `SecurityConfig.java` + `JwtAuthenticationFilter.java` | Spring Security + JWT |

### Ordem recomendada

```
TODO 1 → TODO 2 → TODO 3 → TODO 4 → TODO 5 → (testar frontend)
→ TODO 8 → TODO 6 → TODO 7 → TODO 9 → TODO 10 → TODO 11 → TODO 12
```

## 🖥️ Frontend: TheMovie Web

O frontend é uma aplicação React pré-construída, entregue como imagem Podman.

**Já sobe automaticamente com `podman compose up -d`.**

Acesse: http://localhost:3000

### Endpoints que o frontend consome:

| Método | Endpoint | Funcionalidade |
|--------|----------|----------------|
| `GET` | `/api/movies/search?query={q}&page={p}` | Busca de filmes |
| `GET` | `/api/movies/{id}` | Detalhes do filme |
| `GET` | `/api/movies/popular?page={p}` | Filmes populares |
| `POST` | `/api/movies/{id}/favorite` | Favoritar |
| `DELETE` | `/api/movies/{id}/favorite` | Desfavoritar |
| `POST` | `/api/movies/{id}/watch-later` | Assistir depois |
| `GET` | `/api/movies/favorites?page=0&size=10` | Listar favoritos |
| `POST` | `/auth/login` | Login (JWT) |

### Critério de aceite

> O backend está "pronto" quando o frontend TheMovie Web exibe os dados corretamente — buscar filmes, ver detalhes, favoritar e listar favoritos.

## 🔧 O que já vem pronto

- ✅ Estrutura de pacotes hexagonal
- ✅ Domain models: `Movie`, `MoviePage`, `MovieCredits` (records)
- ✅ Exceções de domínio: `MovieNotFoundException`, `MaxFavoritesException`, etc.
- ✅ Persistência completa: entities, repositories, adapters (Favorite + WatchLater)
- ✅ DTOs do TheMovieDB: `TmdbSearchResponse`, `TmdbMovieResponse`, `TmdbCreditsResponse`
- ✅ Mapper `TmdbMovieMapper`: converte DTOs do TheMovieDB → domínio
- ✅ `FeignConfig`: interceptor com Bearer Token automático
- ✅ `SecurityConfig`: permite tudo por padrão (para testar sem JWT primeiro)
- ✅ `CorsConfig`: CORS configurado para `localhost:3000`
- ✅ `SwaggerConfig`: Swagger UI com suporte a Bearer Token
- ✅ `JwtUtil`: geração e validação de tokens JWT
- ✅ Flyway migrations: tabelas `favorites`, `watch_later`, `users`
- ✅ `AbstractIntegrationTest`: base Testcontainers com PostgreSQL
- ✅ `podman-compose.yml`: PostgreSQL + Redis + TheMovie Web
- ✅ `Containerfile`: multi-stage build otimizado
- ✅ `openapi.yaml`: contrato Swagger completo
- ✅ `api-requests.http`: requisições prontas para teste
- ✅ Usuários seed: `user@movie.com` / `admin@movie.com` (senha: `123456`)

## 📊 Conceitos aplicados (Dias 1-8)

| Dia | Conceito | Onde aparece no projeto |
|-----|----------|------------------------|
| 1 | Records | Domain models (`Movie`, `MoviePage`, `MovieCredits`) |
| 2 | Spring Data JPA | `FavoriteJpaRepository`, `WatchLaterJpaRepository` |
| 3 | Arquitetura Hexagonal | Toda a estrutura de pacotes (ports/adapters) |
| 4 | Testes | `MovieServiceTest` (Mockito), `FavoriteRepositoryIT` (Testcontainers) |
| 5 | Feign + JWT + Swagger | `TheMovieDbClient`, `JwtUtil`, `SwaggerConfig` |
| 6 | Redis Cache + Flyway | `@Cacheable`, migrations SQL |
| 7 | Podman + Actuator | `Containerfile`, `podman-compose.yml`, `application.yml` |
| 8 | Integração completa | Tudo junto em um projeto real! |
