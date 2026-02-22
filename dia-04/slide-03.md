# Slide 3: JUnit 5 — Anatomia de um Teste

**Horário:** 09:45 - 10:15

---

## JUnit 5 — A Plataforma de Testes do Java Moderno

### Evolução do JUnit

```mermaid
timeline
    title Evolução do JUnit
    2002 : JUnit 3
         : Herança de TestCase
         : Nomes: testAlgo()
    2006 : JUnit 4
         : Annotations (@Test, @Before)
         : Runners (@RunWith)
    2017 : JUnit 5 (Jupiter)
         : Modular e extensível
         : @DisplayName, @Nested
         : @ParameterizedTest
         : Extensions (@ExtendWith)
```

### Arquitetura do JUnit 5

JUnit 5 não é um JAR monolítico — é composto por **3 módulos**:

```mermaid
flowchart TD
    subgraph "JUnit 5"
        P["🏗️ JUnit Platform<br/>Engine de execução<br/>Roda em IDEs, Maven, Gradle"]
        J["⭐ JUnit Jupiter<br/>API de programação<br/>@Test, @DisplayName, @Nested<br/>É O QUE USAMOS"]
        V["🔄 JUnit Vintage<br/>Backward compatibility<br/>Roda testes JUnit 3/4<br/>Não precisamos"]
    end

    P --> J
    P --> V

    style J fill:#1dd1a1,color:#fff
```

| Módulo | Função | Quando usar |
|--------|--------|-------------|
| **Platform** | Engine de execução (IDE, Maven, Gradle) | Sempre (está por trás) |
| **Jupiter** | API de programação (annotations, assertions) | **Sempre — é o que escrevemos** |
| **Vintage** | Compatibilidade com JUnit 3/4 | Apenas em projetos legados |

> No Spring Boot, basta ter `spring-boot-starter-test` — já inclui JUnit 5 (Jupiter), Mockito e AssertJ. Não precisa adicionar mais nada.

---

## O Padrão AAA — Arrange, Act, Assert

Todo teste bem escrito segue **3 etapas claras e separadas**:

```mermaid
flowchart LR
    A["🔧 ARRANGE<br/>━━━━━━━━━━<br/>Preparar dados<br/>Configurar mocks<br/>Criar objetos"] 
    --> B["▶️ ACT<br/>━━━━━━━━━━<br/>Executar UMA ação<br/>Chamar o método<br/>sendo testado"] 
    --> C["✅ ASSERT<br/>━━━━━━━━━━<br/>Verificar resultado<br/>Comparar esperado<br/>vs obtido"]

    style A fill:#54a0ff,color:#fff
    style B fill:#feca57,color:#333
    style C fill:#1dd1a1,color:#fff
```

### Exemplo Detalhado

```java
@Test
@DisplayName("Deve calcular desconto de 10% para pedidos acima de R$ 100")
void shouldCalculateDiscountForLargeOrders() {
    // ═══════════════════════════════════════════
    // ✅ ARRANGE — Preparar os dados e o cenário
    // ═══════════════════════════════════════════
    var order = new Order(new BigDecimal("200.00"));
    var calculator = new DiscountCalculator();

    // ═══════════════════════════════════════════
    // ✅ ACT — Executar a ação sendo testada
    // ═══════════════════════════════════════════
    BigDecimal discount = calculator.calculate(order);

    // ═══════════════════════════════════════════
    // ✅ ASSERT — Verificar se o resultado é o esperado
    // ═══════════════════════════════════════════
    assertEquals(new BigDecimal("20.00"), discount);
}
```

### Regras de Ouro do AAA

| Etapa | ✅ O que FAZER | ❌ O que NÃO fazer |
|-------|---------------|-------------------|
| **Arrange** | Criar objetos, configurar mocks, preparar dados | Lógica complexa, loops, condicionais |
| **Act** | **UMA** chamada ao método testado | Múltiplas ações (cada teste testa UMA coisa) |
| **Assert** | Verificar resultado esperado (1-5 asserts) | Mais de 5 asserts (sinal de teste fazendo demais) |

### Analogia: AAA é como um experimento científico

```mermaid
flowchart TD
    subgraph "Experimento Científico"
        E1["🧪 Preparar materiais<br/>(tubos, reagentes)"]
        E2["⚗️ Executar experimento<br/>(misturar, aquecer)"]
        E3["📊 Observar resultado<br/>(cor mudou? temperatura?)"]
    end

    subgraph "Teste de Software"
        T1["🔧 Arrange<br/>(objetos, mocks, dados)"]
        T2["▶️ Act<br/>(chamar o método)"]
        T3["✅ Assert<br/>(assertEquals, assertThat)"]
    end

    E1 -.-> T1
    E2 -.-> T2
    E3 -.-> T3
```

---

## Anotações Essenciais do JUnit 5

### @Test e @DisplayName

```java
@Test   // Marca o método como um teste
@DisplayName("Deve criar funcionário com sucesso quando dados são válidos")
void shouldCreateEmployeeSuccessfully() {
    // teste aqui
}
```

> **@DisplayName** gera relatórios legíveis para humanos.
> - Sem ele: `shouldCreateEmployeeSuccessfully`
> - Com ele: `"Deve criar funcionário com sucesso quando dados são válidos"`
> 
> **Dica**: escreva o @DisplayName **em português** para o relatório ficar legível para o time. O nome do método pode ser em inglês (padrão do mercado).

### @BeforeEach e @AfterEach — Lifecycle do Teste

```java
class EmployeeServiceTest {

    private EmployeeService service;
    private EmployeeRepository repository;

    @BeforeEach
    void setUp() {
        // ✅ Executado ANTES de cada @Test
        // Usado para: criar mocks, inicializar objetos, limpar estado
        repository = mock(EmployeeRepository.class);
        service = new EmployeeService(repository);
    }

    @AfterEach
    void tearDown() {
        // ✅ Executado DEPOIS de cada @Test (cleanup)
        // Usado para: fechar conexões, limpar arquivos temporários
    }

    @Test
    void test1() { /* repository está limpo — mock novo */ }

    @Test
    void test2() { /* repository está limpo — novo mock */ }
}
```

### Ciclo de Vida Visual

```mermaid
sequenceDiagram
    participant JUnit as JUnit 5 Engine
    participant Setup as @BeforeEach
    participant Test as @Test
    participant Teardown as @AfterEach

    rect rgb(240, 248, 255)
        Note over JUnit,Teardown: Teste 1
        JUnit->>Setup: setUp() — cria mocks, inicializa
        Setup->>Test: test1() — executa teste
        Test->>Teardown: tearDown() — limpa recursos
    end

    rect rgb(255, 248, 240)
        Note over JUnit,Teardown: Teste 2
        JUnit->>Setup: setUp() — cria mocks NOVOS
        Setup->>Test: test2() — executa teste
        Test->>Teardown: tearDown() — limpa recursos
    end

    rect rgb(240, 255, 240)
        Note over JUnit,Teardown: Teste 3
        JUnit->>Setup: setUp() — cria mocks NOVOS
        Setup->>Test: test3() — executa teste
        Test->>Teardown: tearDown() — limpa recursos
    end
```

> **Importante**: cada teste recebe um **setup novo**. Testes são **isolados** — um não afeta o outro.

### Lifecycles Completos

| Anotação | Quando executa | Quantas vezes | Uso típico |
|----------|---------------|---------------|------------|
| `@BeforeAll` | Antes de **todos** os testes (static) | 1x por classe | Subir container Podman |
| `@BeforeEach` | Antes de **cada** teste | N vezes | Criar mocks, preparar dados |
| `@Test` | O teste em si | 1x cada | O que estamos testando |
| `@AfterEach` | Depois de **cada** teste | N vezes | Limpar estado |
| `@AfterAll` | Depois de **todos** os testes (static) | 1x por classe | Parar container Podman |

---

## @Nested — Agrupando Testes por Funcionalidade

`@Nested` permite organizar testes em **classes internas**, criando uma hierarquia legível.

```java
@DisplayName("EmployeeService")
class EmployeeServiceTest {

    @Nested
    @DisplayName("Ao criar funcionário")
    class CreateEmployee {

        @Test
        @DisplayName("deve salvar quando dados são válidos")
        void shouldSaveWhenDataIsValid() { /* ... */ }

        @Test
        @DisplayName("deve rejeitar quando salário é menor que mínimo")
        void shouldRejectWhenSalaryIsBelowMinimum() { /* ... */ }

        @Test
        @DisplayName("deve rejeitar quando email já existe")
        void shouldRejectWhenEmailAlreadyExists() { /* ... */ }
    }

    @Nested
    @DisplayName("Ao buscar funcionário")
    class FindEmployee {

        @Test
        @DisplayName("deve retornar funcionário quando ID existe")
        void shouldReturnWhenIdExists() { /* ... */ }

        @Test
        @DisplayName("deve lançar exceção quando ID não existe")
        void shouldThrowWhenIdDoesNotExist() { /* ... */ }
    }

    @Nested
    @DisplayName("Ao deletar funcionário")
    class DeleteEmployee {

        @Test
        @DisplayName("deve deletar quando ID existe")
        void shouldDeleteWhenIdExists() { /* ... */ }

        @Test
        @DisplayName("deve lançar exceção quando ID não existe")
        void shouldThrowWhenIdNotFound() { /* ... */ }
    }
}
```

**Saída no IntelliJ / Terminal:**
```
✅ EmployeeService
   ✅ Ao criar funcionário
      ✅ deve salvar quando dados são válidos
      ✅ deve rejeitar quando salário é menor que mínimo
      ✅ deve rejeitar quando email já existe
   ✅ Ao buscar funcionário
      ✅ deve retornar funcionário quando ID existe
      ✅ deve lançar exceção quando ID não existe
   ✅ Ao deletar funcionário
      ✅ deve deletar quando ID existe
      ✅ deve lançar exceção quando ID não existe
```

```mermaid
---
config:
  theme: base
  themeVariables:
    fontSize: 20px
  flowchart:
    nodeSpacing: 60
    rankSpacing: 80
    padding: 25
---
flowchart LR
    Root["🧪 EmployeeServiceTest"]:::rootNode --> C["📦 Ao criar funcionário"]:::createNode
    Root --> F["🔍 Ao buscar funcionário"]:::findNode
    Root --> D["🗑️ Ao deletar funcionário"]:::deleteNode

    C --> C1["✅ deve salvar quando válido"]:::passNode
    C --> C2["✅ deve rejeitar salário baixo"]:::passNode
    C --> C3["✅ deve rejeitar email duplicado"]:::passNode

    F --> F1["✅ deve retornar quando existe"]:::passNode
    F --> F2["✅ deve lançar exceção quando não existe"]:::passNode

    D --> D1["✅ deve deletar quando existe"]:::passNode
    D --> D2["✅ deve lançar exceção quando não existe"]:::passNode

    classDef rootNode fill:#2c3e50,color:#ecf0f1,stroke:#ecf0f1,stroke-width:4px,font-size:24px,font-weight:bold,padding:20px
    classDef createNode fill:#27ae60,color:#fff,stroke:#1e8449,stroke-width:3px,font-size:20px,font-weight:bold,padding:15px
    classDef findNode fill:#2980b9,color:#fff,stroke:#1f618d,stroke-width:3px,font-size:20px,font-weight:bold,padding:15px
    classDef deleteNode fill:#e74c3c,color:#fff,stroke:#c0392b,stroke-width:3px,font-size:20px,font-weight:bold,padding:15px
    classDef passNode fill:#2ecc71,color:#fff,stroke:#27ae60,stroke-width:2px,font-size:18px,padding:12px

    linkStyle default stroke:#bdc3c7,stroke-width:3px
```

---

## Assertions — Verificando Resultados

### JUnit 5 Nativo — Assertions Básicas

```java
// Igualdade
assertEquals("João", employee.getName());
assertEquals(new BigDecimal("3000.00"), employee.getSalary());

// Nulidade
assertNotNull(employee.getId());
assertNull(employee.getDeletedAt());

// Booleano
assertTrue(employee.isActive());
assertFalse(employee.isDeleted());

// Exceção — retorna a exceção para inspeção
var exception = assertThrows(EmployeeNotFoundException.class,
    () -> service.findById(999L));
assertEquals("Funcionário não encontrado com ID: 999", exception.getMessage());

// Mensagem customizada (útil quando falha)
assertEquals("João", employee.getName(),
    "Nome do funcionário deveria ser João");
```

### assertAll — Verifica TUDO de uma vez (não para no primeiro erro)

```java
// ❌ SEM assertAll — PARA no primeiro erro
assertEquals("João", response.name());          // se falhar, não chega aqui ↓
assertEquals("joao@email.com", response.email());
assertEquals(new BigDecimal("3000.00"), response.salary());

// ✅ COM assertAll — verifica TODOS e mostra TODOS os erros de uma vez
assertAll(
    () -> assertEquals("João", response.name()),
    () -> assertEquals("joao@email.com", response.email()),
    () -> assertEquals(new BigDecimal("3000.00"), response.salary()),
    () -> assertNotNull(response.createdAt())
);
```

```mermaid
flowchart TD
    subgraph "❌ Sem assertAll"
        A1["assertEquals nome ❌"] -->|"PARA"| A2["❓ Não executa os demais"]
    end

    subgraph "✅ Com assertAll"
        B1["assertEquals nome ❌"]
        B2["assertEquals email ✅"]
        B3["assertEquals salary ❌"]
        B4["assertNotNull createdAt ✅"]
        B1 & B2 & B3 & B4 --> R["Relatório: 2 falhas, 2 OK"]
    end

    style A2 fill:#ff6b6b,color:#fff
    style R fill:#feca57,color:#333
```

### assertThrows — Testando Exceções

```java
@Test
@DisplayName("Deve lançar exceção quando salário é menor que o mínimo")
void shouldThrowWhenSalaryBelowMinimum() {
    var request = new EmployeeRequest("João", "joao@email.com",
            new BigDecimal("1000.00"), "529.982.247-25", 1L);

    // Captura a exceção E verifica a mensagem
    var exception = assertThrows(SalaryBelowMinimumException.class,
            () -> service.create(request));

    assertEquals("Salário não pode ser menor que R$ 1.412,00", exception.getMessage());
}
```

---

## 📋 Convenções de Nomenclatura

### Nomes de Métodos de Teste

| Padrão | Exemplo | Quando usar |
|--------|---------|-------------|
| `should...When...` | `shouldCreateEmployee_WhenDataIsValid` | Padrão mais comum |
| `should...` | `shouldRejectSalaryBelowMinimum` | Quando o "when" é óbvio |
| `given...When...Then...` | `givenValidData_WhenCreate_ThenReturnEmployee` | BDD style |

> **Escolha UM padrão** e mantenha consistência no projeto inteiro.

### Nomes de Classes de Teste

| Tipo de Teste | Padrão de Nome | Exemplo |
|---------------|----------------|---------|
| Unitário | `{Classe}Test` | `EmployeeServiceTest` |
| Integração | `{Classe}IT` | `EmployeeRepositoryIT` |
| Parametrizado | `{Classe}ParameterizedTest` | `CpfValidatorParameterizedTest` |

```mermaid
flowchart LR
    subgraph "Organização de Classes de Teste"
        direction TB
        S["src/test/java/"]
        S --> B["builder/"]
        S --> SV["service/"]
        S --> R["repository/"]

        B --> EB["EmployeeBuilder.java"]
        SV --> EST["EmployeeServiceTest.java"]
        SV --> EPT["EmployeeServiceParameterizedTest.java"]
        R --> ERI["EmployeeRepositoryIT.java"]
    end
```

---

## 🧠 Resumo Visual — Anatomia de um Teste

```mermaid
flowchart TD
    subgraph "Classe de Teste"
        ANN["@ExtendWith(MockitoExtension.class)<br/>@DisplayName(EmployeeService)"]
        
        subgraph "Campos"
            MOCK["@Mock EmployeeRepository"]
            INJ["@InjectMocks EmployeeService"]
        end
        
        subgraph "@BeforeEach"
            SETUP["setUp() — preparar dados comuns"]
        end
        
        subgraph "@Nested — Ao criar"
            T1["@Test shouldCreate..."]
            T2["@Test shouldReject..."]
        end
        
        subgraph "@Nested — Ao buscar"
            T3["@Test shouldReturn..."]
            T4["@Test shouldThrow..."]
        end
    end

    ANN --> MOCK --> INJ --> SETUP --> T1 & T2
    SETUP --> T3 & T4
```

---

## 💡 Dica do Instrutor

> Escreva o `@DisplayName` **em português** para o relatório ficar legível para o time. O nome do método pode ser em inglês (padrão do mercado), mas o display name é para **humanos lerem**.

```java
@Test
@DisplayName("Deve lançar exceção quando CPF é inválido")
void shouldThrowWhenCpfIsInvalid() { /* ... */ }
```

> **Demonstração sugerida**: escreva um teste simples ao vivo, mostrando as 3 etapas (AAA) claramente separadas. Rode no IntelliJ e mostre o output verde com @DisplayName.
