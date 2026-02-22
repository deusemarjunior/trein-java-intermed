# Slide 3: Planning — Quebrando Tarefas e Setup Git

**Horário:** 09:30 - 10:00

---

## 📋 Kanban Individual

Organize suas tarefas em um quadro Kanban (GitHub Projects, Trello ou até papel):

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'fontSize': '18px', 'fontFamily': 'arial'}, 'flowchart': {'nodeSpacing': 20, 'rankSpacing': 40, 'padding': 15}}}%%

graph TD
    subgraph TODO1["📋 TO DO — Alta Prioridade"]
        T1["TODO 1: MovieApiPort"]
        T2["TODO 2: TheMovieDbAdapter"]
        T3["TODO 3: MovieUseCasePort"]
        T4["TODO 4: MovieService"]
        T5["TODO 5: MovieController"]
    end

    subgraph TODO2["📋 TO DO — Média Prioridade"]
        T6["TODO 6: Resilience4j"]
        T7["TODO 7: Cache Redis"]
        T8["TODO 8: Error Handler"]
    end

    subgraph TODO3["📋 TO DO — Normal"]
        T9["TODO 9: Testes Unitários"]
        T10["TODO 10: Testes Integração"]
        T11["TODO 11: Swagger"]
        T12["TODO 12: JWT"]
    end

    subgraph INPROG["🔄 IN PROGRESS"]
        IP["(máx 1-2 por vez)"]
    end

    subgraph REVIEW["👀 CODE REVIEW"]
        CR["PR aberto"]
    end

    subgraph FINALIZADO["✅ DONE"]
        DONE["Validado com frontend"]
    end

    TODO1 --> INPROG
    TODO2 --> INPROG
    TODO3 --> INPROG
    INPROG --> REVIEW --> FINALIZADO

    style TODO1 fill:#ffeaea,stroke:#e74c3c,stroke-width:2px
    style TODO2 fill:#fff5e0,stroke:#f39c12,stroke-width:2px
    style TODO3 fill:#eafff0,stroke:#2ecc71,stroke-width:2px
    style IP fill:#f39c12,color:#fff
    style CR fill:#9b59b6,color:#fff
    style DONE fill:#2ecc71,color:#fff
    style INPROG fill:#fff8e1,stroke:#f39c12,stroke-width:2px
    style REVIEW fill:#f3e5f5,stroke:#9b59b6,stroke-width:2px
    style FINALIZADO fill:#e8f5e9,stroke:#2ecc71,stroke-width:2px
```

---

## 🎯 Estratégia de Priorização

Nem todos os TODOs têm o mesmo valor. Priorize pelo **impacto no frontend**:

```mermaid
graph TD
    subgraph "🔴 Prioridade Alta — Frontend não funciona sem isso"
        P1["TODO 1-2: Port + Adapter TheMovieDB"]
        P2["TODO 3-4: Use Case + Service"]
        P3["TODO 5: Controller (endpoints)"]
    end

    subgraph "🟡 Prioridade Média — Qualidade e robustez"
        P4["TODO 6: Resilience4j"]
        P5["TODO 7: Cache Redis"]
        P6["TODO 8: Error Handler"]
    end

    subgraph "🟢 Prioridade Normal — Completude"
        P7["TODO 9-10: Testes"]
        P8["TODO 11: Swagger"]
        P9["TODO 12: JWT"]
    end

    P1 --> P2 --> P3 --> P4

    style P1 fill:#e74c3c,color:#fff
    style P2 fill:#e74c3c,color:#fff
    style P3 fill:#e74c3c,color:#fff
    style P4 fill:#f39c12,color:#fff
    style P5 fill:#f39c12,color:#fff
    style P6 fill:#f39c12,color:#fff
    style P7 fill:#2ecc71,color:#fff
    style P8 fill:#2ecc71,color:#fff
    style P9 fill:#2ecc71,color:#fff
```

> **Dica de consultoria**: entregue o que funciona primeiro. Depois melhore.

---

## 🔀 Setup Git — Fluxo Profissional

```bash
# 1. Fork do template
# (Feito no GitHub — botão "Fork")

# 2. Clonar o fork
git clone https://github.com/SEU-USUARIO/08-movie-service.git
cd 08-movie-service

# 3. Criar branch de desenvolvimento
git checkout -b feat/movie-search

# 4. Desenvolver com commits semânticos
git add .
git commit -m "feat: criar MovieApiPort com operações de busca"

# 5. Subir e abrir PR
git push origin feat/movie-search
```

---

## Commits Semânticos — Cheat Sheet

| Prefixo | Quando usar | Exemplo |
|---------|-------------|---------|
| `feat:` | Nova funcionalidade | `feat: criar endpoint GET /api/movies/search` |
| `fix:` | Correção de bug | `fix: corrigir mapeamento de poster_path` |
| `refactor:` | Refatoração sem mudar comportamento | `refactor: extrair mapeamento para MovieMapper` |
| `test:` | Adicionar ou corrigir testes | `test: adicionar testes para MovieService` |
| `docs:` | Documentação | `docs: documentar endpoints no Swagger` |
| `chore:` | Configuração/infra | `chore: configurar Resilience4j no application.yml` |

```bash
# ❌ Ruim
git commit -m "ajustes"
git commit -m "fix"
git commit -m "wip"

# ✅ Bom
git commit -m "feat: implementar busca de filmes via TheMovieDB"
git commit -m "fix: tratar erro 404 quando filme não existe no TheMovieDB"
git commit -m "test: adicionar 5 cenários de teste para MovieService"
```
