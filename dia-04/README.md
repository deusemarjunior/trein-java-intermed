# Dia 4 - Estratégias de Testes e Qualidade

**Duração**: 5 horas  
**Objetivo**: Confiança para colocar em produção — aplicando testes unitários com JUnit 5 e Mockito, testes de integração com Testcontainers e Data Builders para massa de dados legível

---

## 🎯 Agenda do Dia

| Horário | Duração | Tópico | Tipo |
|---------|---------|--------|------|
| 09:00 - 09:15 | 15min | Recap Dia 3 e Introdução ao Dia 4 | Discussão |
| 09:15 - 09:45 | 30min | Pirâmide de Testes | Teórico |
| 09:45 - 10:15 | 30min | JUnit 5 — Anatomia de um Teste (AAA) | Teórico |
| 10:15 - 10:45 | 30min | JUnit 5 — ParameterizedTest e Assertions | Teórico |
| 10:45 - 11:00 | 15min | ☕ Coffee Break | - |
| 11:00 - 11:30 | 30min | Mockito — @Mock, @InjectMocks, when/thenReturn | Teórico |
| 11:30 - 12:00 | 30min | Mockito — ArgumentCaptor, verify, @Spy | Teórico |
| 12:00 - 13:00 | 1h | 🍽️ Almoço | - |
| 13:00 - 13:20 | 20min | Testcontainers — Testes de Integração com Banco Real | Teórico |
| 13:20 - 13:40 | 20min | Data Builders — Massa de Dados Legível | Teórico |
| 13:40 - 14:10 | 30min | Walkthrough `04-testing-demo` + SonarQube/JaCoCo | Demo |
| 14:10 - 16:30 | 2h20 | Exercício `04-employee-api-tests` (TODOs 1-7) | Hands-on |
| 16:30 - 17:00 | 30min | Review, Cobertura, SonarQube e Q&A | Discussão |

---

## 📦 Material Necessário (Checklist Instrutor)

### Software
- [ ] JDK 21 instalado
- [ ] Maven 3.8+
- [ ] Docker Desktop rodando (para Testcontainers e SonarQube)
- [ ] IDE com suporte a JUnit 5 (IntelliJ recomendado)
- [ ] _(Opcional)_ SonarLint extension no VS Code

### Preparação
- [ ] Projeto `04-testing-demo` rodando com todos os testes passando
- [ ] Projeto `04-employee-api-tests` com TODOs prontos e infraestrutura de testes configurada
- [ ] Docker Desktop funcionando (necessário para Testcontainers com PostgreSQL)
- [ ] Verificar que `docker run hello-world` funciona

---

## 📋 Conteúdo Programático

---

### 1. Pirâmide de Testes

A Pirâmide de Testes define **onde investir esforço** para maximizar confiança com menor custo.

```
        /\
       /  \        E2E (poucos)
      /    \       → Lento, caro, frágil
     /------\
    /        \     Integração (alguns)
   /          \    → Banco real, API real
  /------------\
 /              \  Unitário (muitos)
/________________\ → Rápido, isolado, barato
```

#### O que cada camada garante

| Camada | O que testa | Velocidade | Custo | Proporção |
|--------|-------------|------------|-------|-----------|
| **Unitário** | Uma classe isolada (Service, Mapper) | Milissegundos | Baixo | ~70% |
| **Integração** | Classe + dependência real (Service + DB) | Segundos | Médio | ~20% |
| **E2E** | Sistema completo (HTTP → DB → Response) | Minutos | Alto | ~10% |

#### Proporção ideal

```
Muitos unitários → base sólida, feedback rápido
Alguns de integração → garante que as peças se encaixam
Poucos E2E → garante o fluxo completo (mas é caro de manter)
```

> **Regra prática**: Se o teste unitário é rápido e confiável, não precisa de teste de integração para a mesma coisa. Reserve integração para o que **só funciona com o banco real** (queries JPQL, constraints, migrations).

---

### 2. JUnit 5 — Anatomia de um Teste

JUnit 5 é o framework padrão para testes em Java. Todo teste segue o padrão **AAA**:

```java
@Test
@DisplayName("Deve calcular desconto de 10% para pedidos acima de R$ 100")
void shouldCalculateDiscountForLargeOrders() {
    // Arrange — preparar os dados
    var order = new Order(new BigDecimal("200.00"));
    var calculator = new DiscountCalculator();

    // Act — executar a ação
    BigDecimal discount = calculator.calculate(order);

    // Assert — verificar o resultado
    assertEquals(new BigDecimal("20.00"), discount);
}
```

#### Anotações Essenciais

| Anotação | Função |
|----------|--------|
| `@Test` | Marca um método como teste |
| `@DisplayName("...")` | Nome legível no relatório de testes |
| `@BeforeEach` | Executado **antes** de cada teste (setup) |
| `@AfterEach` | Executado **depois** de cada teste (cleanup) |
| `@BeforeAll` | Executado **uma vez** antes de todos os testes (static) |
| `@Nested` | Agrupa testes relacionados em classes internas |
| `@Disabled` | Desativa um teste temporariamente |

#### @ParameterizedTest + @CsvSource

Testa múltiplos cenários com uma única estrutura:

```java
@ParameterizedTest
@CsvSource({
    "100.00, 10.00",   // 10% de desconto
    "200.00, 20.00",
    "50.00, 0.00",     // sem desconto abaixo de 100
    "99.99, 0.00"
})
@DisplayName("Deve calcular desconto corretamente para diversos valores")
void shouldCalculateDiscount(String orderValue, String expectedDiscount) {
    var order = new Order(new BigDecimal(orderValue));
    var calculator = new DiscountCalculator();

    BigDecimal discount = calculator.calculate(order);

    assertEquals(new BigDecimal(expectedDiscount), discount);
}
```

#### Assertions (JUnit 5 + AssertJ)

```java
// JUnit 5 básico
assertEquals(expected, actual);
assertNotNull(result);
assertTrue(condition);
assertThrows(NotFoundException.class, () -> service.findById(999L));

// assertAll — verifica tudo de uma vez (não para no primeiro erro)
assertAll(
    () -> assertEquals("João", response.name()),
    () -> assertEquals("joao@email.com", response.email()),
    () -> assertEquals(new BigDecimal("3000.00"), response.salary())
);

// AssertJ — mais fluente e legível
assertThat(result).isNotNull();
assertThat(result.name()).isEqualTo("João");
assertThat(employees).hasSize(3).extracting("name").contains("João", "Maria");
```

---

### 3. Mockito — Isolando Dependências

Mockito permite **simular dependências** para testar uma classe de forma isolada.

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;  // dependência simulada

    @InjectMocks
    private ProductService productService;  // classe testada com mocks injetados

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void shouldCreateProductSuccessfully() {
        // Arrange
        var request = new ProductRequest("Notebook", "NOT-0001", new BigDecimal("2500.00"), "Notebook Dell");
        var savedProduct = new Product(1L, "Notebook", "NOT-0001", new BigDecimal("2500.00"), "Notebook Dell");

        when(productRepository.existsBySku("NOT-0001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        var response = productService.create(request);

        // Assert
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Notebook");
        verify(productRepository).save(any(Product.class));
    }
}
```

#### Conceitos Principais

| Conceito | O que faz | Quando usar |
|----------|-----------|-------------|
| `@Mock` | Cria implementação falsa | Dependências do objeto testado |
| `@InjectMocks` | Injeta os mocks automaticamente | Na classe sendo testada |
| `@Spy` | Observa comportamento real parcial | Quando quer manter lógica real + interceptar |
| `when(...).thenReturn(...)` | Define resposta controlada | Simular retorno do repository/client |
| `verify(...)` | Confirma que método foi chamado | Verificar efeitos colaterais |
| `ArgumentCaptor` | Captura argumento passado | Inspecionar o que foi salvo no banco |

#### ArgumentCaptor

```java
@Test
@DisplayName("Deve salvar produto com timestamps corretos")
void shouldSaveProductWithTimestamps() {
    // Arrange
    var request = new ProductRequest("Notebook", "NOT-0001", new BigDecimal("2500.00"), "Dell");
    when(productRepository.existsBySku(anyString())).thenReturn(false);
    when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // Act
    productService.create(request);

    // Assert — capturar exatamente o que foi passado ao repository
    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(captor.capture());

    Product captured = captor.getValue();
    assertThat(captured.getName()).isEqualTo("Notebook");
    assertThat(captured.getCreatedAt()).isNotNull();
}
```

---

### 4. Testcontainers — Testes de Integração com Banco Real

#### Por que não usar H2 para testes?

| Problema | H2 | PostgreSQL Real |
|----------|----|--------------------|
| SQL nativo | Não suporta `ILIKE`, `jsonb`, `ON CONFLICT` | Suporta tudo |
| Constraints | Comportamento diferente em `UNIQUE`, `CHECK` | Idêntico à produção |
| Tipos de dados | Sem `UUID`, `JSONB`, `ARRAY` nativos | Suporte completo |
| Migrations | Pode falhar com Flyway/Liquibase SQL nativo | Funciona idêntico |

> **Regra**: teste com o **mesmo banco que roda em produção**. Testcontainers sobe um PostgreSQL real em um container Docker em segundos.

#### Configuração com Testcontainers

```java
@SpringBootTest
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

```java
class ProductRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar e buscar produto por ID")
    void shouldSaveAndFindById() {
        // Arrange
        var product = new Product();
        product.setName("Notebook");
        product.setSku("NOT-0001");
        product.setPrice(new BigDecimal("2500.00"));

        // Act
        Product saved = productRepository.save(product);
        Optional<Product> found = productRepository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Notebook");
        assertThat(found.get().getSku()).isEqualTo("NOT-0001");
    }
}
```

---

### 5. Data Builders — Massa de Dados Legível

#### O Problema

```java
// ❌ Construtores enormes — o que é cada parâmetro?
var employee = new Employee(1L, "João Silva", "joao@email.com", "123.456.789-09",
        new BigDecimal("3000.00"), department, LocalDateTime.now(), LocalDateTime.now());
```

#### A Solução: Builder Pattern para Testes

```java
// ✅ Builder fluente — legível e com defaults sensatos
var employee = EmployeeBuilder.anEmployee()
        .withName("João Silva")
        .withSalary(new BigDecimal("5000.00"))
        .build();

// Só altera o que importa para o teste — o resto vem com defaults válidos
```

#### Implementação do Builder

```java
public class EmployeeBuilder {

    private Long id = 1L;
    private String name = "João Silva";
    private String email = "joao@email.com";
    private String cpf = "529.982.247-25";
    private BigDecimal salary = new BigDecimal("3000.00");
    private Department department = new Department(1L, "Tecnologia");

    public static EmployeeBuilder anEmployee() {
        return new EmployeeBuilder();
    }

    public EmployeeBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public EmployeeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public EmployeeBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public EmployeeBuilder withSalary(BigDecimal salary) {
        this.salary = salary;
        return this;
    }

    public EmployeeBuilder withCpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public EmployeeBuilder withDepartment(Department department) {
        this.department = department;
        return this;
    }

    public Employee build() {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setCpf(cpf);
        employee.setSalary(salary);
        employee.setDepartment(department);
        return employee;
    }
}
```

#### Uso em Testes

```java
@BeforeEach
void setUp() {
    // Defaults sensatos — dados válidos prontos para uso
    defaultEmployee = EmployeeBuilder.anEmployee().build();
}

@Test
void shouldRejectLowSalary() {
    // Altera APENAS o que importa para este teste
    var employee = EmployeeBuilder.anEmployee()
            .withSalary(new BigDecimal("1000.00"))
            .build();
    // ...
}

@Test
void shouldRejectDuplicateEmail() {
    var employee = EmployeeBuilder.anEmployee()
            .withEmail("duplicado@email.com")
            .build();
    // ...
}
```

---

## 📦 Projetos do Dia

### 📖 `04-testing-demo` (Projeto Completo - Demonstração)

API de Catálogo de Produtos com testes unitários e de integração — **tudo pronto e passando**.

**O que demonstra:**
- `ProductServiceTest`: testes unitários com `@Mock`, `@InjectMocks`, `ArgumentCaptor`
- `ProductServiceParameterizedTest`: `@ParameterizedTest` com `@CsvSource` para validações
- `ProductRepositoryIT`: testes de integração com Testcontainers (PostgreSQL real)
- `ProductBuilder`: builder fluente para criação de dados de teste
- `AbstractIntegrationTest`: classe base com `PostgreSQLContainer` configurado
- Cobertura >80% na camada Service

**Porta**: 8086

```bash
cd 04-testing-demo
mvn test        # Roda todos os testes
mvn spring-boot:run  # Roda a aplicação
```

### ✏️ `04-employee-api-tests` (Exercício: Testes Unitários e de Integração)

Adicionar testes completos à API de Funcionários do dia anterior.

**O que já vem pronto:**
- Projeto completo da API de funcionários (Service, DTOs, Validação, ExceptionHandler)
- Dependências de teste (`spring-boot-starter-test`, `testcontainers`)
- `AbstractIntegrationTest` com `PostgreSQLContainer` configurado
- Classes de teste vazias com TODOs

**Porta**: 8087

```bash
cd 04-employee-api-tests
mvn test        # Alguns testes devem falhar (TODOs não implementados)
```

**TODOs a implementar**: 7 (Builder, testes unitários, testes de integração)

---

## 📚 Referências

| Recurso | Link |
|---------|------|
| JUnit 5 User Guide | https://junit.org/junit5/docs/current/user-guide/ |
| Mockito Documentation | https://javadoc.io/doc/org.mockito/mockito-core/latest/ |
| Testcontainers for Java | https://java.testcontainers.org/ |
| AssertJ Documentation | https://assertj.github.io/doc/ |
| Baeldung - Mockito Tutorial | https://www.baeldung.com/mockito-series |
| Baeldung - Testcontainers | https://www.baeldung.com/spring-boot-testcontainers |

---

## 🎯 Objetivos de Aprendizagem (Checklist)

Ao final deste dia, o aluno será capaz de:

- [ ] Explicar a Pirâmide de Testes e onde investir esforço
- [ ] Escrever testes unitários com JUnit 5 seguindo o padrão AAA
- [ ] Usar `@ParameterizedTest` + `@CsvSource` para múltiplos cenários
- [ ] Criar mocks com `@Mock` e `@InjectMocks` (Mockito)
- [ ] Capturar argumentos com `ArgumentCaptor`
- [ ] Verificar chamadas com `verify(...)`
- [ ] Configurar Testcontainers com PostgreSQL real
- [ ] Criar uma classe base `AbstractIntegrationTest` reutilizável
- [ ] Implementar Data Builders para massa de dados legível
- [ ] Atingir cobertura >80% na camada Service

---

## 📝 Preparação para o Dia 5

No próximo dia abordaremos **Comunicação entre Sistemas e Segurança**:

- [ ] Verificar que o Docker está funcionando (`docker run hello-world`)
- [ ] Pesquisar sobre Feign Client e REST Client declarativo
- [ ] Ler sobre JWT (JSON Web Token) — estrutura Header.Payload.Signature
- [ ] Conceito de CORS (Cross-Origin Resource Sharing)
- [ ] Acessar a documentação do SpringDoc/Swagger UI

---

## 📝 Notas para o Instrutor

### Dicas de Condução

1. **Testes ao vivo**: rodar `mvn test` no `04-testing-demo` e mostrar a saída verde
2. **Mostrar o container**: durante Testcontainers, abrir Docker Desktop e mostrar o PostgreSQL subindo
3. **Red → Green → Refactor**: demonstrar o ciclo TDD simplificado escrevendo um teste que falha, implementando e refatorando
4. **Cobertura**: rodar `mvn test jacoco:report` e abrir o relatório HTML para mostrar cobertura

### Erros Comuns dos Alunos
- Esquecer `@ExtendWith(MockitoExtension.class)` nos testes com Mockito
- Não fazer `deleteAll()` no `@BeforeEach` dos testes de integração
- Confundir `@Mock` com `@InjectMocks`
- Docker Desktop não estar rodando (Testcontainers falha silenciosamente)
- Usar `assertEquals` com `BigDecimal` sem considerar escala
