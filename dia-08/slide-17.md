# Slide 17: Daily Final e Encerramento do Dia 8

**Horário:** 16:30 - 17:00

---

## 🗣️ Daily Final — Status de Cada Aluno

```mermaid
graph TD
    subgraph "Perguntas da Daily Final"
        Q1["✅ Quais TODOs completei?"]
        Q2["🔄 O que falta para amanhã?"]
        Q3["🎯 O frontend está funcionando?"]
        Q4["📝 O PR está aberto?"]
    end

    style Q1 fill:#2ecc71,color:#fff
    style Q2 fill:#f39c12,color:#fff
    style Q3 fill:#3498db,color:#fff
    style Q4 fill:#9b59b6,color:#fff
```

---

## Revisão do Dia 8

```mermaid
mindmap
  root((Dia 8))
    Contract First
      Contrato Swagger
      Perguntas ao cliente
      Priorização de tarefas
    Arquitetura Hexagonal
      Ports IN/OUT
      Adapters
      Domain isolado
    Integração Externa
      Feign Client → TheMovieDB
      Resilience4j
      Cache Redis
    Ritos Ágeis
      Daily Scrum
      Kanban Individual
      Commits Semânticos
    Code Review
      PR aberto
      Feedback entre colegas
      Checklist profissional
    Validação
      Frontend TheMovie Web
      Testes unitários
      Testes de integração
```

---

## O que aplicamos de cada dia

| Dia | Conceito | Onde usamos hoje |
|-----|---------|-----------------|
| **Dia 1** | Records | `MovieSummary`, `MovieDetail`, DTOs imutáveis |
| **Dia 2** | JPA + REST | `FavoriteEntity`, `MovieController`, paginação |
| **Dia 3** | Hexagonal + Error Handling | Pacotes `domain/`, `adapter/`, `GlobalExceptionHandler` |
| **Dia 4** | Testes | `MovieServiceTest`, `FavoriteRepositoryIT`, Testcontainers |
| **Dia 5** | Feign + JWT + Swagger | `TheMovieDbClient`, `SecurityConfig`, OpenAPI |
| **Dia 6** | Redis + Flyway | `@Cacheable` populares, migrations SQL |
| **Dia 7** | Podman | `podman-compose.yml` com PostgreSQL, Redis, frontend |

---

## 📋 Para Amanhã (Dia 9)

1. **Manhã (1h30)**: Finalizar TODOs restantes
2. **Refactoring ao vivo**: Instrutores refatoram código de alunos
3. **Apresentação individual**: Demo ao vivo + decisões técnicas (10-15 min)
4. **Soft Skills**: Carreira, consultoria, certificações
5. **Encerramento**: Feedback, retrospectiva, certificados

> **Dica**: Prepare-se para a apresentação — tenha o Podman Compose rodando e saiba explicar suas decisões.

### Commit final do dia:

```bash
git add .
git commit -m "feat: implementar microsserviço de filmes com arquitetura hexagonal"
git push origin feat/movie-service
```
