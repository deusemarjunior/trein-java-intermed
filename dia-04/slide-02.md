# Slide 2: Pirâmide de Testes

**Horário:** 09:15 - 09:45

---

## Por que Testar?

> **"Código sem testes é código legado."** — Michael Feathers, *Working Effectively with Legacy Code*

### O que é um teste automatizado?

Um teste automatizado é um **programa que verifica outro programa**. Em vez de abrir o Postman, clicar, olhar o resultado e conferir manualmente — o teste faz tudo isso sozinho, em milissegundos.

```mermaid
flowchart LR
    subgraph "❌ Teste Manual"
        M1["Abrir Postman"] --> M2["Enviar request"] --> M3["Olhar response"] --> M4["Conferir com olho 👁️"]
        M4 --> M5["Repetir para<br/>cada cenário"]
    end

    subgraph "✅ Teste Automatizado"
        A1["mvn test"] --> A2["JUnit executa<br/>TODOS os testes"] --> A3["✅ Verde = OK<br/>🔴 Vermelho = Bug"]
    end

    style A3 fill:#1dd1a1,color:#fff
```

### O Custo de Não Testar

```mermaid
graph LR
    A["Bug em Dev"] -->|"💰 R$ 1"| B["Correção fácil<br/>IDE + 5 min"]
    C["Bug em QA"] -->|"💰 R$ 10"| D["Retrabalho<br/>Volta para dev"]
    E["Bug em Produção"] -->|"💰 R$ 100+"| F["Incidente, rollback<br/>Cliente insatisfeito<br/>Suporte, reunião"]
```

| Quando encontra o bug | Custo relativo | Impacto | Exemplo |
|------------------------|----------------|---------|---------|
| Durante o desenvolvimento | 1x | Nenhum | Teste unitário pega NPE |
| Em testes/QA | 10x | Retrabalho, atraso | Teste manual descobre campo errado |
| Em produção | 100x+ | Incidente, perda de receita | Cliente recebe cálculo errado |

> **Testes automatizados** detectam bugs no momento mais barato: **durante o desenvolvimento**.

---

## A Pirâmide de Testes

A Pirâmide de Testes (Martin Fowler / Mike Cohn) define **onde investir esforço** para maximizar confiança com menor custo.

```mermaid
graph TB
    subgraph pyramid["Pirâmide de Testes"]
        direction TB
        E2E["🔺 E2E (End-to-End)<br/>≈10% dos testes<br/>Lento · Caro · Frágil<br/>Sistema completo: HTTP → DB → Response"]
        INT["🔶 Integração<br/>≈20% dos testes<br/>Velocidade média<br/>Classe + Dependência real (DB, API)"]
        UNIT["🟩 Unitário<br/>≈70% dos testes<br/>Rápido · Isolado · Barato<br/>Uma classe isolada com mocks"]
    end

    E2E --> INT --> UNIT

    style E2E fill:#ff6b6b,color:#fff
    style INT fill:#feca57,color:#333
    style UNIT fill:#1dd1a1,color:#fff
```

### Detalhamento de Cada Camada

| Camada | O que testa | Velocidade | Custo de manutenção | Proporção | Ferramentas |
|--------|-------------|------------|---------------------|-----------|-------------|
| **Unitário** | Uma classe isolada (Service, Mapper, Validator) | ⚡ Milissegundos | 💚 Baixo | ~70% | JUnit 5 + Mockito |
| **Integração** | Classe + dependência real (Service + DB, API) | 🕐 Segundos | 🟡 Médio | ~20% | Testcontainers + Spring Boot Test |
| **E2E** | Sistema completo (HTTP → Service → DB → Response) | 🐌 Minutos | 🔴 Alto | ~10% | RestAssured, Selenium, Cypress |

---

## O que Cada Camada Garante

### 🟩 Testes Unitários (Base da Pirâmide)

Testam a **lógica de negócio** isoladamente — sem banco, sem rede, sem filesystem.

```mermaid
flowchart LR
    subgraph "Teste Unitário"
        T["🧪 Teste"] --> S["EmployeeService<br/>(classe real)"]
        S --> M["EmployeeRepository<br/>(MOCK)<br/>resposta controlada"]
    end

    style M fill:#feca57,color:#333
    style S fill:#1dd1a1,color:#fff
```

```java
// Testa a LÓGICA de negócio isoladamente
@Test
void shouldRejectSalaryBelowMinimum() {
    var request = new EmployeeRequest("João", "joao@email.com",
            new BigDecimal("1000.00"), "529.982.247-25", 1L);

    assertThrows(InvalidSalaryException.class,
            () -> employeeService.create(request));
}
```

- ✅ Roda em **milissegundos** (sem banco, sem rede)
- ✅ Isola a classe testada com **mocks**
- ✅ Feedback **imediato** no IDE
- ✅ Não precisa de Podman, servidor, ou infraestrutura

### 🔶 Testes de Integração (Meio da Pirâmide)

Testam a **interação entre componentes** — classe + banco real, classe + API real.

```mermaid
flowchart LR
    subgraph "Teste de Integração"
        T["🧪 Teste"] --> R["EmployeeRepository<br/>(real)"]
        R --> DB["🐘 PostgreSQL<br/>(Testcontainers)"]
    end

    style DB fill:#336791,color:#fff
    style R fill:#feca57,color:#333
```

```java
// Testa a PERSISTÊNCIA com banco real
@Test
void shouldSaveAndFindEmployeeInDatabase() {
    var employee = new Employee();
    employee.setName("João");
    employee.setEmail("joao@email.com");

    Employee saved = repository.save(employee);
    Optional<Employee> found = repository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("João");
}
```

- ✅ Verifica **queries JPQL**, constraints, migrations
- ✅ Usa banco **real** (PostgreSQL via Testcontainers)
- ⚠️ Mais lento que unitário (2-5 segundos)
- ⚠️ Requer Podman rodando

### 🔴 Testes E2E (Topo da Pirâmide)

Testam o **fluxo completo** — da request HTTP até a response.

```mermaid
flowchart LR
    subgraph "Teste E2E"
        T["🧪 Teste<br/>HTTP Client"] -->|"POST /api/employees"| C["Controller"]
        C --> S["Service"]
        S --> R["Repository"]
        R --> DB["🐘 PostgreSQL"]
        DB --> R
        R --> S
        S --> C
        C -->|"201 Created"| T
    end

    style T fill:#ff6b6b,color:#fff
```

```java
// Testa o FLUXO COMPLETO: HTTP → Service → DB → Response
@Test
void shouldCreateEmployeeViaApi() {
    var response = restTemplate.postForEntity("/api/employees", request, EmployeeResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().name()).isEqualTo("João");
}
```

- ✅ Garante que **tudo funciona junto** (rotas, serialização, validação, banco)
- ⚠️ Lento, frágil, caro de manter
- ⚠️ Reservar para **fluxos críticos** do negócio

---

## 📐 Onde Investir Esforço? — Árvore de Decisão

```mermaid
flowchart TD
    A["🤔 O que quero testar?"] --> B{"É lógica de negócio<br/>pura? (cálculo, validação, regra)"}
    B -->|"Sim"| C["🟩 Teste Unitário<br/>JUnit + Mockito<br/>📍 EmployeeServiceTest"]
    B -->|"Não"| D{"Depende do banco<br/>ou recurso externo?"}
    D -->|"Banco (queries, constraints)"| E["🟡 Teste de Integração<br/>Testcontainers<br/>📍 EmployeeRepositoryIT"]
    D -->|"API externa"| F["🟡 Teste de Integração<br/>WireMock"]
    D -->|"Não"| G{"É um fluxo<br/>crítico E2E?"}
    G -->|"Sim (checkout, pagamento)"| H["🔴 Teste E2E<br/>RestTemplate + DB"]
    G -->|"Não"| I["🟩 Teste Unitário<br/>é suficiente"]

    style C fill:#1dd1a1,color:#fff
    style E fill:#feca57,color:#333
    style F fill:#feca57,color:#333
    style H fill:#ff6b6b,color:#fff
    style I fill:#1dd1a1,color:#fff
```

### Regras Práticas — Mapeamento Camada → Tipo de Teste

| Camada da Aplicação | Tipo de Teste | Justificativa |
|---------------------|---------------|---------------|
| **Service** | 🟩 Unitário (com Mockito) | Regras de negócio não precisam de banco |
| **Repository** | 🟡 Integração (Testcontainers) | Queries JPQL e constraints dependem do banco real |
| **Controller** | 🟡 Integração (`@WebMvcTest`) | Validação de rotas, serialização, `@Valid` |
| **Mapper** | 🟩 Unitário (sem mocks) | Transformação pura de dados, sem dependências |
| **Validator** | 🟩 Unitário ou Parametrizado | Lógica pura (CPF, email) |
| **Builder** | ❌ Não precisa de teste | É ferramenta de teste, não código de produção |

```mermaid
flowchart TD
    subgraph "O que testar no dia-a-dia"
        S["Service<br/>🟩 Unitário"] 
        R["Repository<br/>🟡 Integração"]
        M["Mapper<br/>🟩 Unitário"]
        V["Validator<br/>🟩 Parametrizado"]
        C["Controller<br/>🟡 Opcional"]
    end

    S -->|"@Mock Repository"| R
    S -->|"Usa"| M
    S -->|"Usa"| V
```

---

## ⚠️ Anti-Patterns de Teste — O que NÃO Fazer

### ❌ Ice Cream Cone (Pirâmide Invertida)

```
/________________\  E2E (muitos) — CARO! Lento! Frágil!
 \              /   Integração (muitos) — LENTO!
  \            /    Unitário (poucos) — ⚠️ Base fraca
   \__________/     → Feedback lento, suíte frágil, CI lento
```

```mermaid
flowchart TD
    subgraph "❌ Ice Cream Cone"
        E1["🔴 Muitos E2E<br/>CI leva 40 min"]
        I1["🟡 Muitos Integração<br/>Testes lentos"]
        U1["🙈 Poucos Unitários<br/>Sem cobertura de lógica"]
    end
    
    E1 --> I1 --> U1
```

**Consequência**: CI leva 40+ minutos, testes quebram com frequência, feedback lento.

### ❌ Hourglass (Ampulheta)

```
/______\    E2E (muitos)
 |    |     Integração (poucos) — GAP! Peças não se encaixam
/______\    Unitário (muitos)
→ Peças isoladas OK, mas não verificamos se se encaixam
```

**Consequência**: Unitários passam, E2E passa, mas queries erradas no banco não são detectadas.

### ✅ Pirâmide Correta

```
    /\        E2E (poucos) — fluxos críticos
   /  \       Integração (alguns) — banco + queries
  /    \      Unitário (muitos) — lógica de negócio
 /______\     → Feedback rápido + confiança alta
```

---

## 📊 Comparação Visual das Abordagens

```mermaid
graph LR
    subgraph "Sem Testes"
        A1["Medo de<br/>refatorar 😰"]
        A2["Bug em<br/>produção 🔥"]
        A3["Teste manual<br/>toda vez 😩"]
    end

    subgraph "Com Testes"
        B1["Refatorar com<br/>confiança 💪"]
        B2["Bug detectado<br/>em segundos ⚡"]
        B3["CI/CD<br/>automático 🚀"]
    end

    A1 -.->|"Testes unitários"| B1
    A2 -.->|"Testes de integração"| B2
    A3 -.->|"mvn test"| B3

    style B1 fill:#1dd1a1,color:#fff
    style B2 fill:#1dd1a1,color:#fff
    style B3 fill:#1dd1a1,color:#fff
    style A1 fill:#ff6b6b,color:#fff
    style A2 fill:#ff6b6b,color:#fff
    style A3 fill:#ff6b6b,color:#fff
```

---

## 🧠 Conceitos-Chave para Levar

| Conceito | Definição |
|----------|-----------|
| **Teste Unitário** | Testa uma única classe isolada, substituindo dependências por mocks |
| **Teste de Integração** | Testa a interação entre componentes com dependências reais (banco, API) |
| **Teste E2E** | Testa o sistema completo de ponta a ponta (request → response) |
| **Pirâmide de Testes** | Modelo que sugere proporção 70% unitário, 20% integração, 10% E2E |
| **Mock** | Objeto falso que simula o comportamento de uma dependência |
| **Isolamento** | Cada teste roda independente dos outros — sem estado compartilhado |
| **Feedback rápido** | Testes unitários rodam em milissegundos — resultado imediato |

---

## 💡 Dica do Instrutor

> Comece **sempre** pelos testes unitários do Service. Se a lógica de negócio está coberta, você já tem 70% de confiança. Adicione testes de integração apenas para o que **requer o banco real** (queries, constraints, transactions).

> **Pergunta para a turma**: "Se vocês tivessem que escolher entre 100 testes E2E lentos ou 100 testes unitários rápidos, qual escolheriam? Por quê?"
