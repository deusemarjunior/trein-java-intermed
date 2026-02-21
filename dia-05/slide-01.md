# Slide 1: Abertura e Recap do Dia 4

**Horário:** 09:00 - 09:15

---

## 📝 Recapitulando o Dia 4

No Dia 4 aprendemos a **garantir qualidade com testes automatizados**:

- ✓ **Pirâmide de Testes** — Unitários (70%) + Integração (20%) + E2E (10%)
- ✓ **JUnit 5** — Padrão AAA, @Test, @DisplayName, @ParameterizedTest
- ✓ **Mockito** — @Mock, @InjectMocks, when/thenReturn, verify, ArgumentCaptor
- ✓ **Testcontainers** — PostgreSQL real em Docker, @DynamicPropertySource
- ✓ **Data Builders** — EmployeeBuilder fluente com defaults sensatos
- ✓ **Cobertura** — >80% na camada Service

> **Hoje vamos aprender a conectar serviços, proteger APIs e documentar tudo!**

### 🧠 Revisão Rápida — Associe os Conceitos

| Dia | Tema Central | Resultado |
|-----|-------------|-----------|
| **Dia 1** | Fundamentos Java Moderno | Records, Sealed Classes, Streams — linguagem expressiva |
| **Dia 2** | Persistência e REST | Spring Data JPA, APIs REST — dados acessíveis |
| **Dia 3** | Qualidade do Código | Clean Code, Arquitetura limpa — código sustentável |
| **Dia 4** | Testes Automatizados | JUnit 5, Mockito, Testcontainers — confiança para evoluir |
| **Dia 5** | **Integração e Segurança** | Feign, JWT, Swagger — **API de produção** |

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

    D5["<b>⭐ Dia 5 — Integração e Segurança</b><br/>🔒 Feign Client + Resilience4j<br/>Spring Security + JWT<br/>CORS + OpenAPI/Swagger"]

    style D1 fill:#4a90d9,color:#fff,stroke:#2c6fad
    style D2 fill:#4a90d9,color:#fff,stroke:#2c6fad
    style D3 fill:#4a90d9,color:#fff,stroke:#2c6fad
    style D4 fill:#4a90d9,color:#fff,stroke:#2c6fad
    style D5 fill:#1dd1a1,color:#fff,stroke:#10ac84
```

### Por que Integração e Segurança são o próximo passo?

| O que fizemos nos dias anteriores | A lacuna | O que aprendemos hoje |
|-----------------------------------|----------|----------------------|
| Criamos APIs REST completas | A API vive sozinha, isolada | **Feign Client** — consumir outras APIs |
| Testes garantem que funciona | O serviço externo pode cair | **Resilience4j** — retry, fallback, circuit breaker |
| Validação e error handling | Qualquer um acessa tudo | **Spring Security + JWT** — autenticação |
| Código limpo e arquitetura | Ninguém sabe usar a API | **OpenAPI/Swagger** — documentação interativa |

---

## 🎯 Objetivos do Dia 5

Ao final deste dia, o aluno será capaz de:

1. **Feign Client** — Consumir APIs externas de forma declarativa, sem boilerplate HTTP
2. **Resilience4j** — Proteger a aplicação com Retry, Circuit Breaker e Fallback
3. **CORS** — Configurar permissões para frontends em domínios diferentes
4. **Spring Security + JWT** — Implementar autenticação/autorização stateless completa
5. **OpenAPI/Swagger** — Documentar e testar endpoints interativamente no browser

---

## 🏗️ O que vamos construir

```mermaid
graph TB
    subgraph "Frontend (localhost:3000)"
        FE[React / Angular / Vue]
    end

    subgraph "Employee API (localhost:8088)"
        AUTH[AuthController<br/>POST /auth/login]
        EMP[EmployeeController<br/>GET/POST /api/employees]
        DEPT_C[DepartmentController<br/>GET /api/departments]
        SEC[Security Filter<br/>JWT Validation]
        SWAGGER[Swagger UI<br/>/swagger-ui.html]
    end

    subgraph "Department Service (externo)"
        DEPT["GET /api/departments/id"]
    end

    FE -->|"1. CORS enabled"| AUTH
    AUTH -->|"2. JWT Token"| FE
    FE -->|"3. Bearer Token"| EMP
    FE --> SWAGGER
    DEPT_C -->|"Feign Client<br/>+ Resilience4j"| DEPT
    SEC -->|"Valida JWT"| EMP
    SEC -->|"Valida JWT"| DEPT_C

    style SEC fill:#e74c3c,color:#fff
    style AUTH fill:#3498db,color:#fff
    style SWAGGER fill:#2ecc71,color:#fff
```

### Dois projetos, um padrão

| Projeto | Porta | Papel | Objetivo |
|---------|-------|-------|----------|
| `05-integration-security-demo` | 8088 | **Demonstração** (professor) | Referência completa com todos os conceitos |
| `05-employee-api-secure` | 8089 | **Exercício** (aluno) | 8 TODOs para implementar passo a passo |

---

## 📎 Agenda Detalhada

| # | Horário | Slide | Conteúdo |
|---|---------|-------|----------|
| 1 | 09:00 | Este slide | Recap + Introdução |
| 2 | 09:15 | Slide 2 | Feign Client |
| 3 | 09:45 | Slide 3 | Resilience4j |
| 4 | 10:15 | Slide 4 | CORS |
| - | 10:45 | ☕ | Coffee Break |
| 5 | 11:00 | Slide 5 | Security + JWT (Parte 1) |
| 6 | 11:30 | Slide 6 | Security + JWT (Parte 2) |
| - | 12:00 | 🍽️ | Almoço |
| 7 | 13:00 | Slide 7 | OpenAPI / Swagger |
| 8 | 13:20 | Slide 8 | Walkthrough Demo |
| 9 | 13:50 | Slide 9 | Exercício (TODOs 1-4) |
| 10 | 15:30 | Slide 10 | Exercício (TODOs 5-8) |
| 11 | 16:30 | Slide 11 | Review + Q&A |
