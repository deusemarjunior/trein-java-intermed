# Slide 5: Mockito — @Mock, @InjectMocks, when/thenReturn

**Horário:** 11:00 - 11:30

---

## Por que Mockito? — O Problema das Dependências

### O problema: "Teste unitário" com dependências reais

```java
// ❌ Teste "unitário" que depende do banco de dados
class EmployeeServiceTest {

    @Test
    void shouldCreateEmployee() {
        // 💥 Precisa de banco rodando, dados limpos, connection pool...
        var repository = new EmployeeRepository(dataSource);
        var service = new EmployeeService(repository);

        service.create(request); // 💥 Salva no banco de verdade! Lento!
    }
}
```

**Problemas deste approach:**
- 🐌 Lento (precisa do banco)
- 💥 Frágil (depende de dados existentes)
- 🔗 Acoplado (se o banco cai, o teste falha)
- 🧹 Hard to clean (dados sujos entre testes)

### A solução: substituir dependências por Mocks

```java
// ✅ Teste unitário ISOLADO — sem banco, sem rede, sem Docker
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;  // 🎭 FALSO — simulação do banco

    @InjectMocks
    private EmployeeService service;  // ⭐ REAL — classe sendo testada

    @Test
    void shouldCreateEmployee() {
        when(repository.save(any())).thenReturn(savedEmployee);

        service.create(request); // ⚡ Rápido, isolado, controlado
    }
}
```

```mermaid
flowchart LR
    subgraph "❌ Sem Mock — Teste de Integração"
        S1["EmployeeService<br/>(real)"] --> R1["EmployeeRepository<br/>(real)"] --> DB1["🐘 PostgreSQL<br/>(real)"]
    end

    subgraph "✅ Com Mock — Teste Unitário"
        S2["EmployeeService<br/>(real ⭐)"] --> R2["EmployeeRepository<br/>(MOCK 🎭)<br/>resposta controlada"]
    end

    style R2 fill:#feca57,color:#333
    style S2 fill:#1dd1a1,color:#fff
    style DB1 fill:#336791,color:#fff
```

---

## O que é um Mock?

Um **mock** é um **objeto falso** que simula o comportamento de uma dependência real.

```mermaid
classDiagram
    class EmployeeRepository {
        <<interface>>
        +save(Employee) Employee
        +findById(Long) Optional~Employee~
        +findAll() List~Employee~
        +existsByEmail(String) boolean
        +delete(Employee) void
    }

    class RealRepositoryImpl {
        <<Spring Data JPA>>
        +save(Employee) → INSERT no PostgreSQL
        +findById(Long) → SELECT no PostgreSQL
        +findAll() → SELECT * no PostgreSQL
        +existsByEmail(String) → SELECT COUNT no PostgreSQL
    }

    class MockRepository {
        <<Mockito Mock>>
        +save(Employee) → retorna null (padrão)
        +findById(Long) → retorna Optional.empty (padrão)
        +findAll() → retorna List vazia (padrão)
        +existsByEmail(String) → retorna false (padrão)
    }

    EmployeeRepository <|.. RealRepositoryImpl : "Produção"
    EmployeeRepository <|.. MockRepository : "Testes"

    note for MockRepository "Todos os métodos retornam\nvalores padrão SEGUROS:\nnull, 0, false, empty"
```

### Comportamento padrão do Mock

| Tipo de retorno | Valor padrão do Mock |
|-----------------|---------------------|
| `Object` | `null` |
| `int`, `long` | `0` |
| `boolean` | `false` |
| `Optional<T>` | `Optional.empty()` |
| `List<T>` | `[]` (lista vazia) |
| `void` | Nada (não faz nada) |

> O mock **não faz nada** a menos que você o **programe** com `when(...).thenReturn(...)`.

---

## Configuração do Mockito

### Forma 1: @ExtendWith — Recomendada ⭐

```java
@ExtendWith(MockitoExtension.class)  // ← Habilita Mockito nesta classe
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;    // mock da dependência 1

    @Mock
    private DepartmentRepository departmentRepository; // mock da dependência 2

    @InjectMocks
    private EmployeeService employeeService;  // classe real — recebe os mocks
}
```

### Forma 2: Manual — Para entender o que acontece por baixo

```java
class EmployeeServiceTest {

    private EmployeeRepository employeeRepository;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);       // cria mock manualmente
        employeeService = new EmployeeService(employeeRepository); // injeta manualmente
    }
}
```

> **@ExtendWith(MockitoExtension.class)** faz automaticamente o que a Forma 2 faz manualmente. Use sempre a Forma 1.

### Fluxo de criação dos Mocks

```mermaid
sequenceDiagram
    participant JUnit as JUnit 5
    participant Ext as MockitoExtension
    participant Test as Classe de Teste

    JUnit->>Ext: @ExtendWith — ativa extensão
    Ext->>Test: Escaneia campos @Mock
    Ext->>Ext: Cria mock de EmployeeRepository
    Ext->>Ext: Cria mock de DepartmentRepository
    Ext->>Test: Escaneia campo @InjectMocks
    Ext->>Ext: Cria instância REAL de EmployeeService
    Ext->>Ext: Injeta mocks no construtor do EmployeeService
    Ext->>Test: Pronto! Mocks injetados ✅
```

---

## @Mock — Criando Dependências Falsas

```java
@Mock
private EmployeeRepository employeeRepository;
```

O mock é uma **implementação vazia** da interface:
- ✅ Todos os métodos existem (mesma assinatura)
- ❌ Nenhum efeito colateral (não salva no banco, não envia email)
- 🎯 Você **programa** o comportamento com `when(...).thenReturn(...)`

---

## @InjectMocks — Injetando Mocks na Classe Real

```java
@InjectMocks
private EmployeeService employeeService;
```

O Mockito **cria uma instância REAL** de `EmployeeService` e injeta todos os `@Mock`:

```java
// Equivalente a fazer manualmente:
employeeService = new EmployeeService(
    employeeRepository,          // @Mock
    departmentRepository         // @Mock
);
```

```mermaid
flowchart TD
    subgraph "O que @InjectMocks faz"
        M1["@Mock<br/>employeeRepository"]
        M2["@Mock<br/>departmentRepository"]
        
        INJ["@InjectMocks<br/>employeeService = new EmployeeService(...)"]
        
        M1 -->|"injetado"| INJ
        M2 -->|"injetado"| INJ
    end

    style INJ fill:#1dd1a1,color:#fff
    style M1 fill:#feca57,color:#333
    style M2 fill:#feca57,color:#333
```

> **@Mock** = cria o falso. **@InjectMocks** = injeta os falsos no objeto real.

---

## when(...).thenReturn(...) — Programando Respostas

### Cenário 1: Retornar um valor específico

```java
@Test
@DisplayName("Deve criar funcionário com sucesso")
void shouldCreateEmployeeSuccessfully() {
    // Arrange
    var request = new EmployeeRequest("João", "joao@email.com",
            new BigDecimal("3000.00"), "529.982.247-25", 1L);
    var department = new Department(1L, "Tecnologia");
    var savedEmployee = EmployeeBuilder.anEmployee().build();

    // ═══ Programar respostas dos mocks ═══
    when(employeeRepository.existsByEmail("joao@email.com")).thenReturn(false);
    when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
    when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

    // Act
    var response = employeeService.create(request);

    // Assert
    assertThat(response.name()).isEqualTo("João");
}
```

```mermaid
sequenceDiagram
    participant Test as Teste
    participant Service as EmployeeService (real)
    participant Mock1 as employeeRepository (mock)
    participant Mock2 as departmentRepository (mock)

    Test->>Test: when(existsByEmail).thenReturn(false)
    Test->>Test: when(findById).thenReturn(department)
    Test->>Test: when(save).thenReturn(savedEmployee)

    Test->>Service: create(request)
    Service->>Mock1: existsByEmail("joao@email.com")
    Mock1-->>Service: false ← programado!
    Service->>Mock2: findById(1L)
    Mock2-->>Service: Optional.of(department) ← programado!
    Service->>Mock1: save(employee)
    Mock1-->>Service: savedEmployee ← programado!
    Service-->>Test: EmployeeResponse
```

### Cenário 2: Lançar uma exceção

```java
@Test
@DisplayName("Deve lançar exceção quando departamento não existe")
void shouldThrowWhenDepartmentNotFound() {
    var request = new EmployeeRequest("João", "joao@email.com",
            new BigDecimal("3000.00"), "529.982.247-25", 999L);

    // Mock retorna empty = departamento não existe
    when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(DepartmentNotFoundException.class,
            () -> employeeService.create(request));
}
```

### Cenário 3: Retornar o argumento recebido (thenAnswer)

```java
// Retorna o mesmo objeto que foi passado ao save()
// Útil para simular o comportamento de save() que retorna o entity salvo
when(employeeRepository.save(any(Employee.class)))
    .thenAnswer(invocation -> {
        Employee e = invocation.getArgument(0);
        e.setId(1L);  // simular geração de ID
        return e;
    });
```

---

## Matchers — Flexibilizando Argumentos

Matchers permitem definir **quais argumentos** devem acionar o mock.

```java
// Valor exato — só funciona para findById(1L)
when(repository.findById(1L)).thenReturn(Optional.of(employee));

// Qualquer valor long — funciona para findById(qualquer_numero)
when(repository.findById(anyLong())).thenReturn(Optional.of(employee));

// Qualquer objeto do tipo Employee
when(repository.save(any(Employee.class))).thenReturn(employee);

// Qualquer string
when(repository.existsByEmail(anyString())).thenReturn(false);

// String exata com eq() — necessário quando mistura com matchers
when(repository.findByEmail(eq("joao@email.com"))).thenReturn(Optional.of(employee));
```

### Tabela de Matchers Comuns

| Matcher | O que aceita | Exemplo |
|---------|-------------|---------|
| `any()` | Qualquer valor (incluindo null) | `any()` |
| `any(Class.class)` | Qualquer objeto do tipo | `any(Employee.class)` |
| `anyLong()` | Qualquer `long` | `anyLong()` |
| `anyString()` | Qualquer `String` (não null) | `anyString()` |
| `anyList()` | Qualquer `List` | `anyList()` |
| `eq(value)` | Valor exato (usar quando mistura matchers) | `eq(1L)` |
| `argThat(predicate)` | Argumento que satisfaz um predicado | `argThat(e -> e.getName().equals("João"))` |

### ⚠️ Regra Importante: Matchers são "tudo ou nada"

```java
// ❌ ERRO: mistura matcher (any) com valor literal (1L)
when(repo.method(any(), 1L)).thenReturn(result);

// ✅ CORRETO: matcher em TODOS os argumentos
when(repo.method(any(), eq(1L))).thenReturn(result);
```

```mermaid
flowchart LR
    subgraph "❌ Errado"
        A["when(repo.method(<b>any()</b>, <b>1L</b>))"]
        A --> E["💥 InvalidUseOfMatchersException"]
    end

    subgraph "✅ Correto"
        B["when(repo.method(<b>any()</b>, <b>eq(1L)</b>))"]
        B --> S["✅ Funciona!"]
    end

    style E fill:#ff6b6b,color:#fff
    style S fill:#1dd1a1,color:#fff
```

---

## 📝 Exemplo Completo — Teste Unitário com Mockito

```java
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    @DisplayName("Deve criar funcionário com sucesso quando dados são válidos")
    void shouldCreateEmployeeSuccessfully() {
        // ═══ ARRANGE ═══
        var department = new Department(1L, "Tecnologia");
        var request = new EmployeeRequest("João Silva", "joao@email.com",
                new BigDecimal("3000.00"), "529.982.247-25", 1L);
        var savedEmployee = EmployeeBuilder.anEmployee()
                .withName("João Silva")
                .withEmail("joao@email.com")
                .withDepartment(department)
                .build();

        when(employeeRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        // ═══ ACT ═══
        EmployeeResponse response = employeeService.create(request);

        // ═══ ASSERT ═══
        assertAll(
            () -> assertThat(response.name()).isEqualTo("João Silva"),
            () -> assertThat(response.email()).isEqualTo("joao@email.com"),
            () -> assertThat(response.departmentName()).isEqualTo("Tecnologia")
        );
    }
}
```

---

## 🧠 Mapa Mental — Mockito Básico

```mermaid
mindmap
  root((Mockito<br/>Básico))
    @Mock
      Cria objeto falso
      Retorna null/0/false/empty
      Sem efeito colateral
      Precisa @ExtendWith
    @InjectMocks
      Cria instância REAL
      Injeta mocks via construtor
      É a classe TESTADA
    when/thenReturn
      Programa resposta
      Associa entrada → saída
      Só funciona para mocks
    when/thenThrow
      Simula exceção
      Testa cenários de erro
    when/thenAnswer
      Resposta dinâmica
      Acessa argumentos
      Simula save com ID
    Matchers
      any — qualquer valor
      anyLong — qualquer long
      eq — valor exato
      argThat — predicado
```

---

## ⚠️ Erros Comuns dos Alunos

| Erro | Sintoma | Solução |
|------|---------|---------|
| Esquecer `@ExtendWith(MockitoExtension.class)` | `@Mock` não funciona, NPE | Adicionar na classe |
| Misturar matchers com valores | `InvalidUseOfMatchersException` | Usar `eq()` em todos |
| Programar mock que não é chamado | Teste passa por acaso | Verificar com `verify()` |
| Confundir `@Mock` com `@InjectMocks` | NPE ou mock no lugar errado | `@Mock` = dependência, `@InjectMocks` = testado |
| Não programar retorno do mock | Método retorna null, teste falha com NPE | Adicionar `when().thenReturn()` |

---

## 💡 Dica do Instrutor

> Mostre no IntelliJ: `@Mock` tem **underline** (warning) se não tiver `@ExtendWith(MockitoExtension.class)`. É o erro #1 dos alunos.

> **Demonstração sugerida**: 
> 1. Escreva um teste SEM `@ExtendWith` → mostra o NPE
> 2. Adicione `@ExtendWith` → funciona
> 3. Explique: "O Mockito precisa ser ativado — sem @ExtendWith, os @Mock são null"
