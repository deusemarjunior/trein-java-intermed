# Dia 6 - Testes Automatizados

**Duração**: 5 horas  
**Objetivo**: Dominar testes unitários, integração e BDD para garantir qualidade do código

## 📋 Conteúdo Programático

### Manhã (3 horas)

#### 1. JUnit 5 - Testes Unitários (1.5h)

**Estrutura básica**
```java
@DisplayName("Product Service Tests")
class ProductServiceTest {
    
    @Test
    @DisplayName("Should create product when valid data is provided")
    void shouldCreateProductWhenValidData() {
        // Arrange (Given)
        CreateProductCommand command = new CreateProductCommand(
            "Laptop", 
            "Gaming Laptop", 
            new Money(BigDecimal.valueOf(3000))
        );
        
        // Act (When)
        ProductId productId = productService.createProduct(command);
        
        // Assert (Then)
        assertNotNull(productId);
        assertTrue(productId.value() > 0);
    }
    
    @Test
    @DisplayName("Should throw exception when product name is blank")
    void shouldThrowExceptionWhenNameIsBlank() {
        CreateProductCommand command = new CreateProductCommand(
            "", 
            "Description", 
            new Money(BigDecimal.valueOf(100))
        );
        
        assertThrows(InvalidProductException.class, 
            () -> productService.createProduct(command));
    }
}
```

**Assertions avançados**
```java
@Test
void testProductCalculations() {
    Product product = createProduct();
    
    // Assertions básicos
    assertEquals("Laptop", product.getName());
    assertTrue(product.getPrice().compareTo(BigDecimal.ZERO) > 0);
    assertNotNull(product.getCreatedAt());
    
    // AssertAll - múltiplas validações
    assertAll("Product validation",
        () -> assertEquals("Laptop", product.getName()),
        () -> assertTrue(product.isActive()),
        () -> assertNotNull(product.getId())
    );
    
    // Assertions com mensagens customizadas
    assertEquals(BigDecimal.valueOf(3000), product.getPrice().getAmount(), 
        () -> "Price should be 3000 but was " + product.getPrice().getAmount());
    
    // Assertions com timeout
    assertTimeout(Duration.ofMillis(100), 
        () -> productService.findById(product.getId()));
}
```

**Testes parametrizados**
```java
@ParameterizedTest
@ValueSource(strings = {"", "  ", "\t", "\n"})
@DisplayName("Should reject blank product names")
void shouldRejectBlankNames(String name) {
    assertThrows(InvalidProductException.class, 
        () -> new Product(name, "Description", Money.of(100)));
}

@ParameterizedTest
@CsvSource({
    "100.00, 0.10, 90.00",
    "200.00, 0.20, 160.00",
    "300.00, 0.30, 210.00"
})
void testDiscountCalculation(BigDecimal price, BigDecimal discountRate, BigDecimal expected) {
    Money originalPrice = new Money(price);
    Money discounted = priceService.applyDiscount(originalPrice, discountRate);
    assertEquals(expected, discounted.getAmount());
}

@ParameterizedTest
@MethodSource("provideProductData")
void testProductCreation(String name, BigDecimal price, boolean expectedValid) {
    if (expectedValid) {
        assertDoesNotThrow(() -> new Product(name, "Desc", new Money(price)));
    } else {
        assertThrows(Exception.class, () -> new Product(name, "Desc", new Money(price)));
    }
}

static Stream<Arguments> provideProductData() {
    return Stream.of(
        Arguments.of("Laptop", BigDecimal.valueOf(1000), true),
        Arguments.of("", BigDecimal.valueOf(1000), false),
        Arguments.of("Phone", BigDecimal.valueOf(-100), false)
    );
}
```

**Lifecycle e BeforeEach/AfterEach**
```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceTest {
    
    private OrderService orderService;
    private ProductRepository productRepository;
    
    @BeforeAll
    void setupOnce() {
        // Executado uma vez antes de todos os testes
        System.out.println("Setting up test suite");
    }
    
    @BeforeEach
    void setup() {
        // Executado antes de cada teste
        productRepository = new InMemoryProductRepository();
        orderService = new OrderService(productRepository);
    }
    
    @AfterEach
    void tearDown() {
        // Executado após cada teste
        productRepository.clear();
    }
    
    @AfterAll
    void tearDownOnce() {
        // Executado uma vez após todos os testes
        System.out.println("Cleaning up test suite");
    }
}
```

#### 2. Mockito - Mocks e Stubs (1.5h)

**Criando Mocks**
```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private PaymentGateway paymentGateway;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    void shouldCreateProduct() {
        // Arrange
        Product product = createProduct();
        when(productRepository.save(any(Product.class)))
            .thenReturn(product);
        
        // Act
        ProductId id = productService.createProduct(createCommand());
        
        // Assert
        assertNotNull(id);
        verify(productRepository).save(any(Product.class));
        verify(productRepository, times(1)).save(any());
    }
}
```

**Stubbing avançado**
```java
@Test
void testComplexMocking() {
    // Mock com retorno específico
    when(productRepository.findById(1L))
        .thenReturn(Optional.of(createProduct()));
    
    // Mock que lança exceção
    when(productRepository.findById(999L))
        .thenThrow(new ProductNotFoundException("Product not found"));
    
    // Mock com diferentes retornos
    when(paymentGateway.processPayment(any()))
        .thenReturn(PaymentStatus.APPROVED)
        .thenReturn(PaymentStatus.DECLINED)
        .thenThrow(new PaymentException("Gateway down"));
    
    // Mock com resposta baseada em argumento
    when(productRepository.findById(anyLong()))
        .thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(createProductWithId(id));
        });
    
    // Mock void methods
    doNothing().when(productRepository).delete(any());
    doThrow(new RuntimeException()).when(productRepository).deleteAll();
}
```

**Verificações avançadas**
```java
@Test
void testVerifications() {
    productService.createProduct(createCommand());
    
    // Verificar chamadas
    verify(productRepository).save(any(Product.class));
    verify(productRepository, times(1)).save(any());
    verify(productRepository, atLeastOnce()).save(any());
    verify(productRepository, never()).delete(any());
    
    // Verificar argumentos
    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(captor.capture());
    Product savedProduct = captor.getValue();
    assertEquals("Laptop", savedProduct.getName());
    
    // Verificar ordem de chamadas
    InOrder inOrder = inOrder(productRepository, paymentGateway);
    inOrder.verify(productRepository).save(any());
    inOrder.verify(paymentGateway).authorize(any());
    
    // Verificar que não houve mais interações
    verifyNoMoreInteractions(productRepository);
}
```

**Spy - Partial Mocking**
```java
@Test
void testWithSpy() {
    List<String> list = new ArrayList<>();
    List<String> spyList = spy(list);
    
    // Métodos reais são chamados
    spyList.add("one");
    spyList.add("two");
    assertEquals(2, spyList.size());
    
    // Mas podemos fazer stub de métodos específicos
    when(spyList.size()).thenReturn(100);
    assertEquals(100, spyList.size());
    
    // Verificar chamadas
    verify(spyList).add("one");
    verify(spyList, times(2)).add(anyString());
}
```

### Tarde (2 horas)

#### 3. Testes de Integração com Spring Boot Test (1h)

**Configuração**
```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    void shouldCreateProductViaApi() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
            "Laptop", 
            "Gaming Laptop", 
            BigDecimal.valueOf(3000)
        );
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Laptop"))
            .andExpect(jsonPath("$.price").value(3000));
    }
    
    @Test
    void shouldReturnProductById() throws Exception {
        Product product = productRepository.save(createProduct());
        
        mockMvc.perform(get("/api/products/{id}", product.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(product.getId()))
            .andExpect(jsonPath("$.name").value(product.getName()));
    }
}
```

**Testando repositories**
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void shouldFindProductByName() {
        Product product = createProduct();
        entityManager.persist(product);
        entityManager.flush();
        
        List<Product> found = productRepository.findByNameContaining("Laptop");
        
        assertFalse(found.isEmpty());
        assertEquals("Laptop", found.get(0).getName());
    }
}
```

**Testcontainers para testes realistas**
```java
@SpringBootTest
@Testcontainers
class ProductRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    void testWithRealDatabase() {
        Product product = createProduct();
        Product saved = productRepository.save(product);
        
        Optional<Product> found = productRepository.findById(saved.getId());
        assertTrue(found.isPresent());
    }
}
```

#### 4. BDD com Cucumber (1h)

**Feature file**
```gherkin
# features/product.feature
Feature: Product Management
  As a store manager
  I want to manage products
  So that customers can buy them

  Scenario: Create a new product
    Given I am authenticated as a manager
    When I create a product with name "Laptop" and price 3000
    Then the product should be created successfully
    And the product should be available in the catalog

  Scenario Outline: Create products with different prices
    Given I am authenticated as a manager
    When I create a product with name "<name>" and price <price>
    Then the product should be created successfully
    
    Examples:
      | name      | price |
      | Laptop    | 3000  |
      | Mouse     | 50    |
      | Keyboard  | 150   |

  Scenario: Cannot create product with invalid price
    Given I am authenticated as a manager
    When I try to create a product with name "Laptop" and price -100
    Then I should receive an error "Invalid price"
```

**Step Definitions**
```java
@SpringBootTest
@CucumberContextConfiguration
public class CucumberSteps {
    
    @Autowired
    private ProductService productService;
    
    private ProductId createdProductId;
    private Exception thrownException;
    
    @Given("I am authenticated as a manager")
    public void iAmAuthenticatedAsManager() {
        // Setup authentication
    }
    
    @When("I create a product with name {string} and price {int}")
    public void iCreateProduct(String name, int price) {
        CreateProductCommand command = new CreateProductCommand(
            name, 
            "Description", 
            new Money(BigDecimal.valueOf(price))
        );
        createdProductId = productService.createProduct(command);
    }
    
    @When("I try to create a product with name {string} and price {int}")
    public void iTryToCreateProduct(String name, int price) {
        try {
            CreateProductCommand command = new CreateProductCommand(
                name, 
                "Description", 
                new Money(BigDecimal.valueOf(price))
            );
            productService.createProduct(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
    
    @Then("the product should be created successfully")
    public void productShouldBeCreated() {
        assertNotNull(createdProductId);
    }
    
    @Then("I should receive an error {string}")
    public void iShouldReceiveError(String errorMessage) {
        assertNotNull(thrownException);
        assertTrue(thrownException.getMessage().contains(errorMessage));
    }
}
```

**Configuração Cucumber**
```java
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.steps",
    plugin = {"pretty", "html:target/cucumber-reports"},
    tags = "not @ignore"
)
public class CucumberTest {
}
```

## 💻 Exercícios Práticos

### Exercício 1: Testes Unitários (1h)
Crie testes para a classe `OrderService` que deve:
- Calcular total do pedido
- Aplicar descontos
- Validar estoque
- Processar pagamento

Use Mockito para mockar dependências.

### Exercício 2: Testes de Integração (1h)
Crie testes de integração para a API de Produtos:
- POST /api/products
- GET /api/products/{id}
- PUT /api/products/{id}
- DELETE /api/products/{id}

Use MockMvc e teste validações, status codes e respostas.

### Exercício 3: BDD (1h)
Crie features Cucumber para o fluxo de pedidos:
- Criar pedido
- Adicionar itens
- Calcular total
- Processar pagamento
- Atualizar status

## 📚 Material de Estudo

### Leitura Obrigatória
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Cucumber Java](https://cucumber.io/docs/cucumber/api/)

### Leitura Complementar
- [Test-Driven Development by Example](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)
- [Growing Object-Oriented Software, Guided by Tests](https://www.amazon.com/Growing-Object-Oriented-Software-Guided-Tests/dp/0321503627)

## 🎯 Objetivos de Aprendizagem

Ao final deste dia, você deve ser capaz de:

- ✅ Escrever testes unitários com JUnit 5
- ✅ Usar Mockito para isolar dependências
- ✅ Criar testes de integração com Spring Boot Test
- ✅ Implementar BDD com Cucumber
- ✅ Alcançar boa cobertura de código
- ✅ Aplicar boas práticas de testes

## 🏠 Tarefa de Casa

1. **Aumentar cobertura**:
   - Adicionar testes para todos os services
   - Meta: 80% de cobertura
   - Usar JaCoCo para medir

2. **Estudar**:
   - Testes de mutação (PIT)
   - Testes de contrato (Pact)
   - Performance testing

3. **Preparação para Dia 7**:
   - Revisar SQL avançado
   - Estudar sobre índices
   - Conhecer Redis básico

## 📝 Notas do Instrutor

```
Pontos de atenção:
- Enfatizar pirâmide de testes
- Mostrar relatórios de cobertura
- Demonstrar TDD na prática
- Explicar quando usar mocks vs testes reais
- Mostrar debugging de testes
- Discutir performance de testes
```

## 🔗 Links Úteis

- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org/)
- [Testcontainers](https://www.testcontainers.org/)
- [Cucumber](https://cucumber.io/)
- [JaCoCo](https://www.jacoco.org/)
