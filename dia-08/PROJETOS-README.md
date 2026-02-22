# 📚 Projetos Java - Dia 08

## 📖 Projeto do Dia

### 1️⃣ **08-movie-service** (Exercício Individual — Desafio da Consultoria)
**Objetivo**: Desenvolver individualmente um Microsserviço de Filmes completo com Arquitetura Hexagonal, integrando com a API do TheMovieDB e validando com o frontend TheMovie Web (React via Docker).

**Conceitos aplicados (todos os dias anteriores)**:
- **Dia 1**: Records como DTOs do domínio (`MovieSummary`, `MovieDetail`, `MovieSearchResult`)
- **Dia 2**: Spring Data JPA para favoritos/watch-later, REST Controllers, paginação
- **Dia 3**: Arquitetura Hexagonal (Ports & Adapters), `GlobalExceptionHandler`, Problem Details
- **Dia 4**: Testes unitários (JUnit 5 + Mockito), testes de integração (Testcontainers)
- **Dia 5**: Feign Client (TheMovieDB), Resilience4j, JWT, CORS, OpenAPI/Swagger
- **Dia 6**: Cache Redis (`@Cacheable`), Flyway migrations
- **Dia 7**: Docker Compose (PostgreSQL + Redis + frontend)

**Porta**: 8080  
**Frontend**: http://localhost:3000 (TheMovie Web via Docker)

```bash
# 1. Fork do template no GitHub

# 2. Clonar o fork
git clone https://github.com/SEU-USUARIO/08-movie-service.git
cd 08-movie-service

# 3. Subir infraestrutura com Docker Compose
docker compose up -d

# 4. Rodar a aplicação
mvn spring-boot:run

# 5. Verificar
# Backend: http://localhost:8080/api/movies/popular
# Frontend: http://localhost:3000
# Swagger: http://localhost:8080/swagger-ui.html
```

**TODOs a implementar**: 12
| TODO | Descrição | Prioridade |
|------|-----------|-----------|
| 1 | `MovieApiPort` — Port de saída (interface) | 🔴 Alta |
| 2 | `TheMovieDbAdapter` — Adapter REST com Feign | 🔴 Alta |
| 3 | `MovieUseCasePort` — Port de entrada (use case) | 🔴 Alta |
| 4 | `MovieService` — Lógica de negócio | 🔴 Alta |
| 5 | `MovieController` — Endpoints REST | 🔴 Alta |
| 6 | Resilience4j — Retry + Circuit Breaker | 🟡 Média |
| 7 | Cache Redis — `@Cacheable` populares | 🟡 Média |
| 8 | `GlobalExceptionHandler` — Problem Details | 🟡 Média |
| 9 | Testes unitários — MovieService (5 cenários) | 🟢 Normal |
| 10 | Testes de integração — FavoriteRepository | 🟢 Normal |
| 11 | Swagger/OpenAPI — Documentação | 🟢 Normal |
| 12 | JWT — Proteger endpoints de favoritos | 🟢 Normal |

**Verificação final**:
- [ ] Frontend TheMovie Web exibe filmes populares
- [ ] Busca de filmes funciona
- [ ] Detalhes do filme carregam
- [ ] Favoritar/desfavoritar funciona
- [ ] PR aberto com commits semânticos
- [ ] Code Review realizado em colega

---

## 🚀 Como Usar

### 1. **Faça o fork e configure o ambiente**
   ```bash
   git clone https://github.com/SEU-USUARIO/08-movie-service.git
   cd 08-movie-service
   docker compose up -d
   ```

### 2. **Configure a API Key do TheMovieDB**
   - Crie uma conta em https://developer.themoviedb.org/
   - Gere uma API Key (v4 auth — Bearer Token)
   - Configure no `application.yml` ou como variável de ambiente:
   ```bash
   export TMDB_API_KEY=sua-api-key-aqui
   ```

### 3. **Implemente os TODOs em ordem de prioridade**
   - Comece pelos TODOs 1-5 (core — frontend precisa disso)
   - Depois 6-8 (qualidade e robustez)
   - Por último 9-12 (completude)

### 4. **Valide com o frontend**
   - Acesse http://localhost:3000
   - Busque filmes, veja detalhes, favorite
   - Se funcionar → seu backend está pronto!

### 5. **Abra o PR e peça Code Review**
   ```bash
   git add .
   git commit -m "feat: implementar microsserviço de filmes"
   git push origin feat/movie-service
   # Abra o PR no GitHub
   ```
