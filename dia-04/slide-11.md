# Slide 11: Exercício — 04-employee-api-tests (TODOs 5-7)

**Horário:** 15:30 - 16:30

---

## TODO 5: @ParameterizedTest para CPF Inválido ⭐⭐

**Arquivo**: `src/test/java/.../service/EmployeeServiceTest.java`

**Tempo estimado**: 15 minutos

```java
// TODO 5: Criar @ParameterizedTest para CPF inválido:
//   - @CsvSource com: "123", "00000000000", "1234567890", "abc"
//   - Esperar InvalidCpfException para cada valor
```

### Conceito: 1 Método, N Execuções

```mermaid
flowchart TD
    subgraph "@ParameterizedTest — CPF Inválido"
        M["shouldRejectInvalidCpf(cpf, reason)"]
        
        E1["[1] '123'<br/>CPF muito curto ✅"]
        E2["[2] '00000000000'<br/>Dígitos iguais ✅"]
        E3["[3] '1234567890'<br/>10 dígitos ✅"]
        E4["[4] 'abc'<br/>Letras ✅"]
        E5["[5] '12345678901234'<br/>Mais de 11 dígitos ✅"]
    end

    M --> E1
    M --> E2
    M --> E3
    M --> E4
    M --> E5

    style M fill:#54a0ff,color:#fff
    style E1 fill:#1dd1a1,color:#fff
    style E2 fill:#1dd1a1,color:#fff
    style E3 fill:#1dd1a1,color:#fff
    style E4 fill:#1dd1a1,color:#fff
    style E5 fill:#1dd1a1,color:#fff
```

### Implementação — Gabarito

```java
@ParameterizedTest
@CsvSource({
    "123,            CPF muito curto",
    "00000000000,    Todos dígitos iguais",
    "1234567890,     CPF com 10 dígitos",
    "abc,            CPF com letras",
    "12345678901234, CPF com mais de 11 dígitos"
})
@DisplayName("Deve rejeitar CPF inválido")
void shouldRejectInvalidCpf(String cpf, String reason) {
    var request = new EmployeeRequest("João Silva", "joao@email.com",
            new BigDecimal("3000.00"), cpf, 1L);

    assertThrows(InvalidCpfException.class,
            () -> employeeService.create(request),
            "Deveria rejeitar CPF: " + reason);
}
```

### Saída Esperada no Console

```
✅ Deve rejeitar CPF inválido
   ✅ [1] 123, CPF muito curto
   ✅ [2] 00000000000, Todos dígitos iguais
   ✅ [3] 1234567890, CPF com 10 dígitos
   ✅ [4] abc, CPF com letras
   ✅ [5] 12345678901234, CPF com mais de 11 dígitos
```

### Comparação: Sem vs Com @ParameterizedTest

```mermaid
flowchart LR
    subgraph "❌ SEM — 5 métodos repetidos"
        A1["testRejectCpf123()"]
        A2["testRejectCpf00000()"]
        A3["testRejectCpf1234567890()"]
        A4["testRejectCpfAbc()"]
        A5["testRejectCpfLong()"]
    end

    subgraph "✅ COM — 1 método parameterizado"
        B["shouldRejectInvalidCpf(cpf, reason)"]
    end

    style A1 fill:#ff6b6b,color:#fff
    style A2 fill:#ff6b6b,color:#fff
    style A3 fill:#ff6b6b,color:#fff
    style A4 fill:#ff6b6b,color:#fff
    style A5 fill:#ff6b6b,color:#fff
    style B fill:#1dd1a1,color:#fff
```

> **Regra**: Se testar a **mesma lógica** com **dados diferentes**, use `@ParameterizedTest`.

---

## TODO 6: Testes de Integração com Testcontainers ⭐⭐⭐

**Arquivo**: `src/test/java/.../repository/EmployeeRepositoryIT.java`

**Tempo estimado**: 25 minutos

```java
// TODO 6: Em EmployeeRepositoryIT — testar persistência real:
//   - Salvar Employee e buscar por ID — comparar campos
//   - Testar busca paginada (Pageable) com 15 registros
//   - Testar filtro por departamento contra banco PostgreSQL real
```

### Pré-requisito: Podman Desktop rodando!

```bash
podman --version   # deve retornar a versão
podman ps          # deve funcionar sem erros
```

```mermaid
flowchart TD
    subgraph "Checklist pré-TODO 6"
        D1["Podman Desktop<br/>rodando? 🐳"] -->|"✅ Sim"| D2["podman ps<br/>funciona?"]
        D2 -->|"✅ Sim"| D3["Pronto para<br/>Testcontainers!"]
        D1 -->|"❌ Não"| D4["Abra o Podman Desktop<br/>e aguarde inicializar"]
        D2 -->|"❌ Não"| D5["Reinicie o<br/>Podman Desktop"]
    end

    style D3 fill:#1dd1a1,color:#fff
    style D4 fill:#ff6b6b,color:#fff
    style D5 fill:#ff6b6b,color:#fff
```

### Ciclo de Vida do Teste de Integração

```mermaid
sequenceDiagram
    participant JUnit as JUnit 5
    participant TC as Testcontainers
    participant Podman as Podman
    participant PG as PostgreSQL

    JUnit->>TC: @Testcontainers
    TC->>Podman: podman run postgres:16-alpine
    Podman->>PG: Container UP (porta aleatória) 🐘
    
    loop Para cada @Test
        JUnit->>JUnit: @BeforeEach → deleteAll()
        JUnit->>PG: INSERT/SELECT/UPDATE (SQL real)
        PG-->>JUnit: Resultado real
        JUnit->>JUnit: assertThat(...) ✅
    end

    JUnit->>TC: Testes concluídos
    TC->>Podman: podman stop + rm 🗑️
```

### 6.1 — Salvar e Buscar por ID

```java
@Test
@DisplayName("Deve salvar funcionário e buscar por ID")
void shouldSaveAndFindById() {
    // Arrange — criar dados reais no PostgreSQL
    var department = departmentRepository.save(new Department(null, "Tecnologia"));

    var employee = new Employee();
    employee.setName("João Silva");
    employee.setEmail("joao@email.com");
    employee.setCpf("529.982.247-25");
    employee.setSalary(new BigDecimal("3000.00"));
    employee.setDepartment(department);

    // Act — INSERT real no PostgreSQL
    Employee saved = employeeRepository.save(employee);
    Optional<Employee> found = employeeRepository.findById(saved.getId());

    // Assert — SELECT real do PostgreSQL
    assertThat(found).isPresent();
    assertAll(
        () -> assertThat(found.get().getName()).isEqualTo("João Silva"),
        () -> assertThat(found.get().getEmail()).isEqualTo("joao@email.com"),
        () -> assertThat(found.get().getSalary())
                .isEqualByComparingTo(new BigDecimal("3000.00")),
        () -> assertThat(found.get().getId()).isNotNull()
    );
}
```

### 6.2 — Busca Paginada com 15 Registros

```java
@Test
@DisplayName("Deve retornar página de funcionários com 15 registros")
void shouldReturnPageOfEmployees() {
    var department = departmentRepository.save(new Department(null, "TI"));

    // Criar 15 funcionários
    for (int i = 1; i <= 15; i++) {
        var employee = new Employee();
        employee.setName("Funcionário " + i);
        employee.setEmail("func" + i + "@email.com");
        employee.setCpf(String.format("%011d", i));
        employee.setSalary(new BigDecimal("3000.00"));
        employee.setDepartment(department);
        employeeRepository.save(employee);
    }

    // Buscar página 0 com 10 itens
    Page<Employee> page = employeeRepository.findAll(PageRequest.of(0, 10));

    assertAll(
        () -> assertThat(page.getContent()).hasSize(10),
        () -> assertThat(page.getTotalElements()).isEqualTo(15),
        () -> assertThat(page.getTotalPages()).isEqualTo(2)
    );
}
```

```mermaid
flowchart LR
    subgraph "Paginação — 15 registros"
        DB["PostgreSQL<br/>15 employees"]
        P0["Página 0<br/>10 itens"]
        P1["Página 1<br/>5 itens"]
    end

    DB -->|"PageRequest.of(0, 10)"| P0
    DB -->|"PageRequest.of(1, 10)"| P1

    style P0 fill:#54a0ff,color:#fff
    style P1 fill:#feca57,color:#333
```

### 6.3 — Filtro por Departamento

```java
@Test
@DisplayName("Deve buscar funcionários por departamento")
void shouldFindByDepartment() {
    var ti = departmentRepository.save(new Department(null, "TI"));
    var rh = departmentRepository.save(new Department(null, "RH"));

    // 3 de TI, 2 de RH
    for (int i = 1; i <= 3; i++) {
        var employee = new Employee();
        employee.setName("TI " + i);
        employee.setEmail("ti" + i + "@email.com");
        employee.setCpf(String.format("%011d", i));
        employee.setSalary(new BigDecimal("3000.00"));
        employee.setDepartment(ti);
        employeeRepository.save(employee);
    }

    for (int i = 1; i <= 2; i++) {
        var employee = new Employee();
        employee.setName("RH " + i);
        employee.setEmail("rh" + i + "@email.com");
        employee.setCpf(String.format("%011d", i + 10));
        employee.setSalary(new BigDecimal("3000.00"));
        employee.setDepartment(rh);
        employeeRepository.save(employee);
    }

    // Filtrar apenas TI
    List<Employee> tiEmployees = employeeRepository.findByDepartmentId(ti.getId());

    assertThat(tiEmployees).hasSize(3);
    assertThat(tiEmployees).allMatch(
        e -> e.getDepartment().getId().equals(ti.getId())
    );
}
```

```mermaid
flowchart TD
    subgraph "Banco PostgreSQL"
        TI["Dept: TI<br/>TI 1, TI 2, TI 3"]
        RH["Dept: RH<br/>RH 1, RH 2"]
    end

    subgraph "Query"
        Q["findByDepartmentId(ti.getId())"]
    end

    subgraph "Resultado"
        R["[TI 1, TI 2, TI 3]<br/>size = 3 ✅"]
    end

    TI --> Q
    RH -.->|"filtrado"| Q
    Q --> R

    style TI fill:#54a0ff,color:#fff
    style RH fill:#dfe6e9
    style R fill:#1dd1a1,color:#fff
```

---

## TODO 7: Testar Constraint UNIQUE no Banco ⭐⭐

**Arquivo**: `src/test/java/.../repository/EmployeeRepositoryIT.java`

**Tempo estimado**: 10 minutos

```java
// TODO 7: Testar constraint de email único no banco:
//   - Salvar dois employees com mesmo email → esperar DataIntegrityViolationException
```

### Conceito: Por que este teste é diferente do TODO 4?

```mermaid
flowchart LR
    subgraph "TODO 4 — Teste Unitário (Mockito)"
        T4["existsByEmail → true<br/>━━━━━━━━━━━━━━━<br/>Regra no SERVICE<br/>Verificação manual<br/>Mock simula o banco"]
    end

    subgraph "TODO 7 — Teste de Integração (Testcontainers)"
        T7["INSERT duplicado no PG<br/>━━━━━━━━━━━━━━━<br/>Regra no BANCO<br/>Constraint UNIQUE<br/>PostgreSQL real valida"]
    end

    style T4 fill:#54a0ff,color:#fff
    style T7 fill:#1dd1a1,color:#fff
```

| Aspecto | TODO 4 (Unitário) | TODO 7 (Integração) |
|---------|-------------------|---------------------|
| Quem valida? | `EmployeeService` | PostgreSQL (constraint UNIQUE) |
| Mock ou real? | Mock do repository | Banco real com Testcontainers |
| Exceção | `DuplicateEmailException` | `DataIntegrityViolationException` |
| O que testa? | Lógica de negócio | Schema do banco |

### Implementação — Gabarito

```java
@Test
@DisplayName("Deve lançar exceção ao salvar email duplicado no banco")
void shouldThrowOnDuplicateEmail() {
    var department = departmentRepository.save(new Department(null, "TI"));

    // Primeiro: salvar com sucesso
    var employee1 = new Employee();
    employee1.setName("João");
    employee1.setEmail("mesmo@email.com");
    employee1.setCpf("52998224725");
    employee1.setSalary(new BigDecimal("3000.00"));
    employee1.setDepartment(department);
    employeeRepository.saveAndFlush(employee1);  // ← flush!

    // Segundo: mesmo email → deve falhar
    var employee2 = new Employee();
    employee2.setName("Maria");
    employee2.setEmail("mesmo@email.com");  // mesmo email!
    employee2.setCpf("98765432100");
    employee2.setSalary(new BigDecimal("4000.00"));
    employee2.setDepartment(department);

    // ❗ Constraint UNIQUE do PostgreSQL real
    assertThrows(DataIntegrityViolationException.class,
            () -> employeeRepository.saveAndFlush(employee2));
}
```

### ⚠️ Lembre: saveAndFlush() é obrigatório!

```mermaid
flowchart LR
    subgraph "❌ save() — pode NÃO detectar"
        S1["save(emp2)"] --> C1["Cache JPA<br/>(SQL não executa)"] --> R1["Sem exceção 😱"]
    end

    subgraph "✅ saveAndFlush() — detecta imediatamente"
        S2["saveAndFlush(emp2)"] --> C2["INSERT INTO ...<br/>(SQL executa!)"] --> R2["DataIntegrity<br/>ViolationException ✅"]
    end

    style R1 fill:#ff6b6b,color:#fff
    style R2 fill:#1dd1a1,color:#fff
```

---

## ⏱️ Checkpoint Final — 16:30

### Validação Completa

```mermaid
flowchart TD
    subgraph "Todos os TODOs"
        T1["✅ TODO 1 — EmployeeBuilder"]
        T2["✅ TODO 2 — Criar com sucesso"]
        T3["✅ TODO 3 — Salário mínimo"]
        T4["✅ TODO 4 — Email duplicado"]
        T5["✅ TODO 5 — @ParameterizedTest CPF"]
        T6["✅ TODO 6 — Integração (3 testes)"]
        T7["✅ TODO 7 — UNIQUE constraint"]
    end

    style T1 fill:#1dd1a1,color:#fff
    style T2 fill:#1dd1a1,color:#fff
    style T3 fill:#1dd1a1,color:#fff
    style T4 fill:#1dd1a1,color:#fff
    style T5 fill:#1dd1a1,color:#fff
    style T6 fill:#1dd1a1,color:#fff
    style T7 fill:#1dd1a1,color:#fff
```

```bash
# Rodar TODOS os testes (Podman precisa estar rodando!)
mvn test

# Resultado esperado:
# Tests run: XX, Failures: 0, Errors: 0, Skipped: 0
# BUILD SUCCESS ✅
```

---

## 📊 Cobertura Esperada

```mermaid
flowchart LR
    subgraph "Cobertura dos Testes"
        ES["EmployeeService<br/>>80% ✅"]
        ER["EmployeeRepository<br/>>70% ✅"]
        EM["EmployeeMapper<br/>Coberto indiretamente"]
        EC["EmployeeController<br/>Não testado"]
    end

    style ES fill:#1dd1a1,color:#fff
    style ER fill:#1dd1a1,color:#fff
    style EM fill:#feca57,color:#333
    style EC fill:#dfe6e9
```

| Classe | Cobertura | Como |
|--------|-----------|------|
| `EmployeeService` | >80% ✅ | Testes unitários (TODO 2-5) |
| `EmployeeRepository` | >70% ✅ | Testes de integração (TODO 6-7) |
| `EmployeeMapper` | Indireta | Coberto pelos testes de Service |
| `EmployeeController` | Não testado | Use `@WebMvcTest` se desejar (extra) |

---

## 🏆 Desafios Extras (Opcional)

Se terminou antes do tempo:

```mermaid
flowchart TD
    subgraph "Desafios Extras ⭐⭐⭐"
        D1["1. Teste de update()<br/>sucesso + ID inexistente"]
        D2["2. Teste de delete()<br/>com verify()"]
        D3["3. EmployeeRequestBuilder<br/>para DTOs de entrada"]
        D4["4. @Nested classes<br/>organizar por funcionalidade"]
        D5["5. Teste do Mapper<br/>unitário puro sem mocks"]
    end

    style D1 fill:#54a0ff,color:#fff
    style D2 fill:#54a0ff,color:#fff
    style D3 fill:#feca57,color:#333
    style D4 fill:#feca57,color:#333
    style D5 fill:#feca57,color:#333
```

1. **Teste de `update()`** — cenário de sucesso com `ArgumentCaptor` e cenário de ID inexistente
2. **Teste de `delete()`** — verificar com `verify()` que `repository.delete()` foi chamado
3. **`EmployeeRequestBuilder`** — builder fluente para o DTO de entrada
4. **`@Nested` classes** — organizar: `CreateEmployee`, `UpdateEmployee`, `DeleteEmployee`, `FindEmployee`
5. **Teste do Mapper** — unitário puro: `EmployeeMapper.toEntity()` e `EmployeeMapper.toResponse()`

---

## 💡 Dica do Instrutor

> Os testes de integração (TODO 6-7) são os mais desafiadores. Se algum aluno travar, verifique:
> 1. Podman Desktop está rodando?
> 2. `@BeforeEach` tem `deleteAll()`?
> 3. Está usando `saveAndFlush()` para o teste de constraint?
> 4. Está salvando o `Department` antes do `Employee`? (FK)

> **Para a turma**: "A diferença entre TODO 4 e TODO 7 é fundamental — um testa a regra de negócio do Service, o outro testa a constraint do banco. Ambos são necessários porque são **camadas diferentes de proteção**."
