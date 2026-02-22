# Dia 09 — Projetos: Apresentação e Encerramento

## Projeto do Dia

O dia 09 não possui um novo projeto. O foco é a **finalização e apresentação** do projeto `08-movie-service` iniciado no Dia 08.

## O que deve estar pronto para apresentar

### Projeto: `08-movie-service`

| # | TODO | Conceito | Status |
|---|------|----------|--------|
| 1 | `MovieApiPort` | Hexagonal — Port | □ |
| 2 | `TheMovieDbAdapter` | Feign Client + Adapter | □ |
| 3 | `MovieUseCasePort` | Use Case Port | □ |
| 4 | `MovieService` | Regras de negócio | □ |
| 5 | `MovieController` | REST endpoints | □ |
| 6 | Resilience4j | Retry + Circuit Breaker | □ |
| 7 | Cache Redis | `@Cacheable` com TTL | □ |
| 8 | `GlobalExceptionHandler` | Problem Details (RFC 7807) | □ |
| 9 | Testes Unitários | JUnit 5 + Mockito | □ |
| 10 | Testes de Integração | Testcontainers | □ |
| 11 | Swagger/OpenAPI | Documentação dos endpoints | □ |
| 12 | Segurança JWT | Spring Security + Auth | □ |

## Frontend: TheMovie Web

O frontend já está pronto via Docker. Para rodar:

```bash
docker compose up -d
```

Acesse: http://localhost:3000

## Critérios de Avaliação da Apresentação

| Critério | Peso |
|----------|------|
| Funcionalidade (endpoints + frontend) | 30% |
| Arquitetura (hexagonal, separação) | 20% |
| Qualidade de Código (clean code, DTOs) | 15% |
| Testes (unitários + integração) | 15% |
| Git & PR (commits semânticos, PR) | 10% |
| Apresentação (clareza, demo, decisões) | 10% |

## Estrutura da Apresentação (10-15 min por aluno)

1. **Contexto** (1 min) — Breve descrição do projeto
2. **Demo ao Vivo** (5 min) — Mostrar o frontend funcionando
3. **Decisões Técnicas** (3 min) — Mostrar código relevante
4. **Desafios** (2 min) — O que foi difícil e como resolveu
5. **Aprendizados** (2 min) — O que leva para o dia a dia

## Conceitos Consolidados (Dias 1-8)

- Java 17/21: Records, Sealed Classes, Pattern Matching, Stream API
- Spring Boot 3.x: REST, Spring Data JPA, Flyway
- Arquitetura: Hexagonal / Clean Architecture, SOLID
- Testes: JUnit 5, Mockito, Testcontainers
- Segurança: Spring Security, JWT
- Integrações: Feign Client, Resilience4j, Redis
- Observabilidade: Actuator, Micrometer
- CI/CD: GitHub Actions, Docker, Docker Compose
