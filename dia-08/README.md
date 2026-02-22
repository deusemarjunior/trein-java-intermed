# Dia 8 - Projeto Prático: O Desafio da Consultoria (Individual)

**Duração**: 5 horas  
**Objetivo**: Desenvolver individualmente um **Microsserviço de Filmes** completo com Arquitetura Hexagonal, integrando com a API do [TheMovieDB](https://developer.themoviedb.org/reference/getting-started) e implementando um backend que alimenta o frontend **TheMovie Web** (React) fornecido pelo instrutor como imagem Docker.

> **Pré-requisito**: Dias 1-7 concluídos. Docker Desktop instalado e rodando. Conta no [TheMovieDB](https://developer.themoviedb.org/reference/getting-started) para obter a API Key.

> **⚠️ Importante**: O desafio é **individual**, mas cada aluno seguirá todos os passos e ritos como se estivesse em um time de consultoria — Daily Scrum, Kanban, Code Review via PR, commits semânticos e apresentação técnica.

---

## 🎯 Agenda do Dia

| Horário | Duração | Tópico | Tipo |
|---------|---------|--------|------|
| 09:00 - 09:30 | 30min | Briefing: Entrega do contrato Swagger, análise, perguntas ao "cliente" | Discussão |
| 09:30 - 10:00 | 30min | Planning: Quebra de tarefas, setup Git (fork + branches), Kanban individual | Planejamento |
| 10:00 - 10:45 | 45min | Desenvolvimento individual — TODOs 1-4 | Hands-on |
| 10:45 - 11:00 | 15min | ☕ Coffee Break | - |
| 11:00 - 12:00 | 1h | Desenvolvimento individual — TODOs 5-8 | Hands-on |
| 12:00 - 13:00 | 1h | 🍽️ Almoço | - |
| 13:00 - 13:15 | 15min | Daily Scrum simulado: status, impedimentos, próximos passos | Discussão |
| 13:15 - 15:00 | 1h45 | Desenvolvimento individual — TODOs 9-12 | Hands-on |
| 15:00 - 15:30 | 30min | Code Review: PRs cruzados entre colegas + feedback | Review |
| 15:30 - 16:30 | 1h | Ajustes finais e validação com o frontend **TheMovie Web** | Hands-on |
| 16:30 - 17:00 | 30min | Daily final + acompanhamento de progresso | Discussão |

---

## 📦 Material Necessário (Checklist Instrutor)

### Software
- [ ] JDK 21 instalado
- [ ] Maven 3.8+
- [ ] IDE com suporte a Java (IntelliJ ou VS Code)
- [ ] Docker Desktop rodando
- [ ] _(Opcional)_ Postman ou extensão REST Client no VS Code

### Preparação
- [ ] Projeto template `08-movie-service` disponível para fork no GitHub
- [ ] Contrato Swagger/OpenAPI (`openapi.yaml`) pronto para entrega
- [ ] Frontend **TheMovie Web** acessível via Docker:
  ```bash
  docker run -d --name themovie-web -p 3000:80 \
    -e REACT_APP_API_URL=http://localhost:8080 \
    ghcr.io/deusemar/themovie-web:latest
  ```
- [ ] Verificar que o frontend carrega em http://localhost:3000
- [ ] API Key do TheMovieDB funcional para testes

---

## 📋 Conteúdo Programático

---

### 1. Dinâmica de Consultoria — Contract First (Individual)

Mesmo trabalhando individualmente, o aluno segue o fluxo profissional de uma consultoria:

- O instrutor entrega um **contrato Swagger/OpenAPI** que define os endpoints do microsserviço
- Um **frontend já pronto** (TheMovie Web) consome esse contrato — o aluno desenvolve o backend que o alimenta
- Fazer as perguntas certas antes de codar: escopar, negociar e priorizar
- Definição de "pronto": o backend funciona quando o **TheMovie Web** (frontend React via Docker) exibe os dados corretamente

> **Simulação**: Mesmo sendo individual, o aluno documenta decisões, mantém commits semânticos e abre PR como se estivesse em um time real.

---

### 2. Ritos Ágeis (Aplicados Individualmente)

O aluno pratica os ritos como se fosse membro de um time de consultoria:

- **Daily Scrum simulado**: cada aluno compartilha com a turma — o que fiz, o que vou fazer, quais impedimentos
- **Timeboxing**: aprender a trabalhar com prazos curtos e entregas incrementais
- **Kanban individual**: To Do → In Progress → Code Review → Done (usar GitHub Projects ou quadro físico)
- **Retrospectiva pessoal**: ao final do dia, anotar o que funcionou e o que melhorar

---

### 3. Git Profissional

- Fork do repositório template → branch por feature → Pull Request
- Commits semânticos: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`
- Code Review via PR entre colegas: checklist de nomenclatura, testes, tratamento de erros, segurança
- Feedback construtivo: como apontar problemas sem ser ofensivo

---

## 🖥️ Frontend: TheMovie Web (React)

> O frontend é uma aplicação **React** pré-construída e entregue como **imagem Docker**. O aluno **não precisa ter Node.js instalado** — basta rodar o container e apontar para o seu backend.

**Como executar:**

```bash
# Rodar o frontend apontando para o backend local (porta 8080)
docker run -d \
  --name themovie-web \
  -p 3000:80 \
  -e REACT_APP_API_URL=http://localhost:8080 \
  ghcr.io/deusemar/themovie-web:latest
```

Após subir, acessar **http://localhost:3000** no navegador.

**Variáveis de ambiente:**
| Variável | Descrição | Default |
|----------|-----------|----------|
| `REACT_APP_API_URL` | URL base do backend (API do aluno) | `http://localhost:8080` |

**Endpoints que o frontend consome:**
| Método | Endpoint | Funcionalidade no frontend |
|--------|----------|----------------------------|
| `GET` | `/api/movies/search?query={q}&page={p}` | Busca de filmes |
| `GET` | `/api/movies/{id}` | Página de detalhes do filme |
| `GET` | `/api/movies/popular?page={p}` | Carrossel de filmes populares |
| `POST` | `/api/movies/{id}/favorite` | Botão de favoritar |
| `DELETE` | `/api/movies/{id}/favorite` | Botão de desfavoritar |
| `POST` | `/api/movies/{id}/watch-later` | Botão "assistir depois" |
| `GET` | `/api/movies/favorites?page=0&size=10` | Página de favoritos |
| `POST` | `/auth/login` | Tela de login (JWT) |

**Dica:** inclua o frontend no `docker-compose.yml` do projeto para subir tudo junto:

```yaml
services:
  # ... PostgreSQL, Redis, etc.

  themovie-web:
    image: ghcr.io/deusemar/themovie-web:latest
    ports:
      - "3000:80"
    environment:
      - REACT_APP_API_URL=http://localhost:8080
    depends_on:
      - app
```

> **Critério de aceite**: o backend está "pronto" quando o frontend exibe os dados corretamente — buscar filmes, ver detalhes, favoritar e listar favoritos.

---

## ✏️ Projeto Exercício: `08-movie-service`

> Microsserviço de Filmes com Arquitetura Hexagonal — consome a API do TheMovieDB e expõe endpoints definidos pelo contrato Swagger fornecido pelo instrutor. O frontend **TheMovie Web** (React) roda via Docker e consome esse contrato. **Desenvolvimento individual.**

**O que já vem pronto no template:**
- Estrutura de pacotes hexagonal: `domain/`, `adapter/in/web/`, `adapter/out/rest/`, `adapter/out/persistence/`
- `docker-compose.yml` com PostgreSQL + Redis + **TheMovie Web** (frontend React)
- `application.yml` configurado para os containers e para a API do TheMovieDB
- Migrations Flyway iniciais (`V1__create_favorites.sql`, `V2__create_watch_later.sql`)
- `AbstractIntegrationTest` com Testcontainers
- Contrato Swagger/OpenAPI (`openapi.yaml`) entregue pelo instrutor
- `README.md` com a User Story e critérios de aceite

**TODOs:**
- `// TODO 1: Criar o Port de saída (interface) MovieApiPort no domain/:`
  - `//   - searchMovies(query, page): buscar filmes por texto`
  - `//   - getMovieDetails(movieId): detalhes de um filme`
  - `//   - getPopularMovies(page): listar filmes populares`
  - `//   - getMovieCredits(movieId): elenco e equipe`
- `// TODO 2: Implementar o Adapter REST TheMovieDbAdapter (adapter/out/rest/):`
  - `//   - Usar Feign Client para consumir https://api.themoviedb.org/3/`
  - `//   - Endpoints: /search/movie, /movie/{id}, /movie/popular, /movie/{id}/credits`
  - `//   - Enviar API Key via header Authorization: Bearer {token}`
  - `//   - Mapear resposta JSON do TheMovieDB para objetos do domínio`
- `// TODO 3: Criar o Port de entrada (use case) MovieUseCasePort:`
  - `//   - Definir operações de negócio: buscar, detalhar, listar populares, favoritar, marcar para assistir`
- `// TODO 4: Implementar MovieService no domain/ (lógica de negócio):`
  - `//   - Orquestrar chamadas ao MovieApiPort (TheMovieDB)`
  - `//   - Gerenciar favoritos e lista "assistir depois" no banco local`
  - `//   - Regra: máximo 20 filmes na lista de favoritos por usuário`
- `// TODO 5: Criar MovieController (adapter/in/web/) seguindo o contrato Swagger:`
  - `//   - GET /api/movies/search?query={q}&page={p} — buscar filmes`
  - `//   - GET /api/movies/{id} — detalhes do filme (dados do TheMovieDB + status de favorito local)`
  - `//   - GET /api/movies/popular?page={p} — filmes populares`
  - `//   - POST /api/movies/{id}/favorite — favoritar filme`
  - `//   - DELETE /api/movies/{id}/favorite — desfavoritar`
  - `//   - POST /api/movies/{id}/watch-later — marcar para assistir depois`
  - `//   - GET /api/movies/favorites?page=0&size=10 — listar favoritos (paginado)`
- `// TODO 6: Configurar Resilience4j para chamadas ao TheMovieDB:`
  - `//   - Retry: maxAttempts=3, waitDuration=500ms`
  - `//   - CircuitBreaker: failureRateThreshold=50`
  - `//   - Fallback: retornar lista vazia ou cached data quando TheMovieDB estiver fora`
- `// TODO 7: Cachear filmes populares com Redis (@Cacheable, TTL 30 min)`
- `// TODO 8: Implementar GlobalExceptionHandler com Problem Details (RFC 7807)`
- `// TODO 9: Criar testes unitários para MovieService (mínimo 5 cenários):`
  - `//   - Buscar filmes com sucesso`
  - `//   - Favoritar filme com sucesso`
  - `//   - Favoritar além do limite (20) → exceção`
  - `//   - Detalhar filme inexistente → exceção`
  - `//   - Fallback quando TheMovieDB indisponível`
- `// TODO 10: Criar testes de integração com Testcontainers para FavoriteRepository`
- `// TODO 11: Documentar endpoints com OpenAPI/Swagger (já definidos no contrato)`
- `// TODO 12: Proteger endpoints de favoritos com JWT (usuário autenticado)`

---

## 📝 Dinâmica do Dia

- **Manhã**: Entrega do contrato Swagger pelo instrutor, análise dos endpoints, perguntas ao "cliente", planejamento individual (Kanban) e início do desenvolvimento com arquitetura hexagonal
- **Tarde**: Desenvolvimento ativo, integração com TheMovieDB, validação com o **TheMovie Web** (`docker compose up` → http://localhost:3000), code review entre colegas via Pull Request

> **Lembrete**: Mesmo sendo individual, mantenha os ritos — commits semânticos, branches por feature, Daily Scrum e Code Review. O objetivo é praticar o fluxo profissional completo.

---

## 📊 Checklist de Progresso Individual

Use este checklist como Kanban pessoal:

- [ ] Fork do template e setup do ambiente (`docker compose up`)
- [ ] TODOs 1-2: Ports e Adapter REST (TheMovieDB)
- [ ] TODOs 3-4: Use Case e Service (lógica de negócio)
- [ ] TODO 5: Controller com todos os endpoints
- [ ] TODO 6: Resilience4j (retry, circuit breaker, fallback)
- [ ] TODO 7: Cache Redis
- [ ] TODO 8: GlobalExceptionHandler
- [ ] TODOs 9-10: Testes unitários e de integração
- [ ] TODO 11: Documentação OpenAPI
- [ ] TODO 12: Segurança JWT
- [ ] Validação com o frontend TheMovie Web
- [ ] PR aberto com commits semânticos
- [ ] Code Review de pelo menos 1 colega
