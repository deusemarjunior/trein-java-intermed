# Slide 1: Abertura e Recap do Dia 3

**Horário:** 09:00 - 09:15

---

## 📝 Recapitulando o Dia 3

No Dia 3 aprendemos a transformar **"código que funciona"** em **"código profissional"**:

- ✓ **Clean Code** — Nomenclatura significativa, métodos pequenos e coesos, DRY
- ✓ **Code Smells** — God Class, Long Method, Feature Envy, Primitive Obsession
- ✓ **Arquitetura em Camadas** — Controller → Service → Repository, cada um com sua responsabilidade
- ✓ **DTOs** — Records como Request/Response, mapeamento Entity ↔ DTO
- ✓ **Arquitetura Hexagonal** — Ports & Adapters, domain/, adapter/in/web/, adapter/out/persistence/
- ✓ **Tratamento de Erros** — @ControllerAdvice, Problem Details (RFC 7807), Custom Exceptions
- ✓ **Validação** — Bean Validation (@Valid), Custom Validators (@ValidCpf)
- ✓ **Refactoring** — Extract Method, Rename, Extract Class, Replace Conditional

> **Hoje vamos aprender a garantir que tudo isso funciona — e continua funcionando!**

---

## 🔗 Conexão entre os Dias — A Jornada do Desenvolvedor

```mermaid
flowchart LR
    subgraph "Dia 1 — Fundamentos"
        D1[Records, Sealed Classes<br/>Streams, Pattern Matching]
    end

    subgraph "Dia 2 — Persistência"
        D2[Spring Data JPA<br/>Queries, Paginação<br/>REST APIs]
    end

    subgraph "Dia 3 — Qualidade do Código"
        D3[Clean Code<br/>Code Smells<br/>Arquitetura em Camadas<br/>DTOs, Validação]
    end

    subgraph "Dia 4 — Testes ⭐"
        D4[JUnit 5<br/>Mockito<br/>Testcontainers<br/>Data Builders]
    end

    D1 -->|"Records usados<br/>como DTOs"| D2
    D2 -->|"API REST<br/>a ser testada"| D3
    D3 -->|"Código limpo<br/>é testável"| D4

    style D4 fill:#1dd1a1,color:#fff,stroke:#10ac84
```

### Por que testes são o próximo passo natural?

| O que fizemos nos dias anteriores | O que testes garantem |
|-----------------------------------|----------------------|
| Criamos **Records** e **DTOs** | Testes verificam que o mapeamento está correto |
| Escrevemos **regras de negócio** no Service | Testes unitários validam cada regra isoladamente |
| Configuramos **JPA + PostgreSQL** | Testes de integração garantem que queries funcionam no banco real |
| Adicionamos **validação** (@Valid, @ValidCpf) | Testes parametrizados cobrem múltiplos cenários |
| Implementamos **Error Handling** | Testes verificam que exceções corretas são lançadas |

---

## 🧭 Mapa Mental do Dia 4

```mermaid
mindmap
  root((Dia 4 — Testes<br/>e Qualidade))
    Pirâmide de Testes
      Unitário — rápido e isolado
      Integração — banco real
      E2E — sistema completo
      Proporção ideal 70/20/10
      Anti-patterns — Ice Cream Cone
    JUnit 5
      Padrão AAA — Arrange Act Assert
      @Test e @DisplayName
      @BeforeEach e @AfterEach
      @Nested — agrupamento
      @ParameterizedTest + @CsvSource
      @ValueSource e @MethodSource
      Assertions e assertAll
      AssertJ — fluente e legível
    Mockito
      @Mock e @InjectMocks
      when/thenReturn — programa respostas
      when/thenThrow — simula erros
      verify — confirma chamadas
      ArgumentCaptor — captura args
      @Spy — comportamento real parcial
      Matchers — any, eq, anyLong
    Testcontainers
      PostgreSQL real em Podman
      @Container — gerenciamento automático
      @DynamicPropertySource — config dinâmica
      AbstractIntegrationTest — classe base
      saveAndFlush — forçar constraint
    Data Builders
      Builder Pattern para testes
      Valores default sensatos
      Métodos with fluentes
      Legibilidade e manutenção
```

---

## 🎯 Objetivos de Aprendizagem

Ao final deste dia, o aluno será capaz de:

1. **Explicar** a Pirâmide de Testes e decidir **onde** investir esforço
2. **Escrever** testes unitários com JUnit 5 seguindo o padrão AAA
3. **Parametrizar** testes com `@ParameterizedTest` + `@CsvSource`
4. **Isolar** dependências com `@Mock` e `@InjectMocks` (Mockito)
5. **Capturar** argumentos com `ArgumentCaptor` e verificar com `verify`
6. **Configurar** Testcontainers com PostgreSQL real para testes de integração
7. **Criar** Data Builders fluentes para massa de dados legível
8. **Atingir** cobertura >80% na camada Service

---

## 📊 Fluxo do Dia — Agenda Visual

```mermaid
gantt
    title Agenda do Dia 4
    dateFormat HH:mm
    axisFormat %H:%M

    section Manhã
    Recap + Abertura             :a1, 09:00, 15min
    Pirâmide de Testes           :a2, 09:15, 30min
    JUnit 5 — Anatomia (AAA)    :a3, 09:45, 30min
    JUnit 5 — Parametrizado     :a4, 10:15, 30min
    Coffee Break ☕              :a5, 10:45, 15min
    Mockito — Mock/InjectMocks  :a6, 11:00, 30min
    Mockito — Captor/Verify/Spy :a7, 11:30, 30min

    section Almoço
    Almoço 🍽️                   :a8, 12:00, 60min

    section Tarde
    Testcontainers              :a9, 13:00, 20min
    Data Builders               :a10, 13:20, 20min
    Walkthrough testing-demo    :a11, 13:40, 30min
    Exercício TODOs 1-7         :a12, 14:10, 140min
    Review e Q&A                :a13, 16:30, 30min
```

---

## 🔄 O Ciclo do Software Confiável

```mermaid
flowchart TD
    A["📝 Escrever Código<br/>(Dia 3: Clean Code)"] --> B["🧪 Escrever Testes<br/>(Dia 4: JUnit + Mockito)"]
    B --> C["✅ Rodar Testes<br/>(mvn test)"]
    C -->|"🟢 Passou"| D["🚀 Deploy com Confiança"]
    C -->|"🔴 Falhou"| E["🔧 Corrigir Bug"]
    E --> A
    D --> F["🔄 Refactoring Seguro"]
    F --> C

    style B fill:#1dd1a1,color:#fff
    style D fill:#54a0ff,color:#fff
    style E fill:#ff6b6b,color:#fff
```

> **Sem testes**: qualquer mudança é arriscada — "funciona, não mexe!".  
> **Com testes**: refactoring e novas features se tornam seguros — "mudou? roda os testes!".

---

## 🧠 O que é "Código Testável"?

O código limpo que escrevemos no Dia 3 tem uma consequência importante: **é testável**.

```mermaid
flowchart LR
    subgraph "❌ Código Não Testável"
        A1["God Class<br/>faz tudo sozinha"]
        A2["new Repository()<br/>dentro do Service"]
        A3["Lógica no Controller"]
    end

    subgraph "✅ Código Testável"
        B1["Classes pequenas<br/>responsabilidade única"]
        B2["Injeção de dependência<br/>via construtor"]
        B3["Lógica no Service<br/>Controller é fino"]
    end

    A1 -.->|"Refactoring"| B1
    A2 -.->|"DI"| B2
    A3 -.->|"Separação"| B3
```

### Princípios que tornam o código testável

| Princípio do Dia 3 | Benefício para Testes (Dia 4) |
|---------------------|-------------------------------|
| **Responsabilidade Única** | Cada classe é testada isoladamente |
| **Injeção de Dependência** | Permite substituir por mocks |
| **Métodos pequenos** | Fácil de cobrir todos os caminhos |
| **Separação em camadas** | Teste unitário no Service, integração no Repository |
| **DTOs (Records)** | Dados imutáveis, fácil de comparar em asserts |

---

## 📦 Projetos do Dia

### 1️⃣ `04-testing-demo` — Projeto completo (referência)

API de Catálogo de Produtos com **todos os testes implementados**.

```mermaid
flowchart TD
    subgraph "Testes Unitários (Mockito)"
        ST["ProductServiceTest<br/>7 testes"]
        SPT["ProductServiceParameterizedTest<br/>3 testes"]
    end

    subgraph "Testes de Integração (Testcontainers)"
        RT["ProductRepositoryIT<br/>6 testes"]
    end

    subgraph "Código de Produção"
        C[ProductController] --> S[ProductService]
        S --> R[ProductRepository]
        R --> DB[(PostgreSQL)]
    end

    ST -->|"@Mock"| S
    SPT -->|"@Mock"| S
    RT -->|"@Autowired"| R
```

### 2️⃣ `04-employee-api-tests` — Exercício (7 TODOs)

API de Gestão de Funcionários — **você vai adicionar os testes!**

---

## 📚 Referências Bibliográficas

| Livro/Recurso | Autor | Relevância |
|---------------|-------|------------|
| **Growing Object-Oriented Software, Guided by Tests** | Steve Freeman, Nat Pryce | Filosofia TDD e builders |
| **Effective Unit Testing** | Lasse Koskela | Padrões de teste, legibilidade |
| **Working Effectively with Legacy Code** | Michael Feathers | Testabilidade de código existente |
| **Clean Code** (Cap. 9 — Unit Tests) | Robert C. Martin | Regras de testes limpos |
| **JUnit 5 User Guide** | JUnit Team | Referência oficial |
| **Mockito Documentation** | Mockito Team | Mocks, spies, captors |
| **Testcontainers Docs** | Testcontainers Team | Configuração e boas práticas |

---

## 🔧 Pré-requisitos do Dia

> **IMPORTANTE**: Podman Desktop precisa estar rodando para os testes de integração com Testcontainers!

```bash
# Verificar Podman
podman --version
podman run docker.io/library/hello-world

# Verificar Java
java --version

# Verificar Maven
mvn --version
```

### Checklist de verificação

```mermaid
flowchart LR
    A["Podman<br/>instalado?"] -->|Sim| B["Podman<br/>rodando?"]
    A -->|Não| A1["❗ Instalar<br/>Podman Desktop"]
    B -->|Sim| C["JDK 21<br/>instalado?"]
    B -->|Não| B1["❗ Iniciar<br/>Podman Desktop"]
    C -->|Sim| D["Maven 3.8+<br/>instalado?"]
    C -->|Não| C1["❗ Instalar JDK 21"]
    D -->|Sim| E["✅ Pronto<br/>para começar!"]
    D -->|Não| D1["❗ Instalar Maven"]

    style E fill:#1dd1a1,color:#fff
    style A1 fill:#ff6b6b,color:#fff
    style B1 fill:#ff6b6b,color:#fff
    style C1 fill:#ff6b6b,color:#fff
    style D1 fill:#ff6b6b,color:#fff
```
