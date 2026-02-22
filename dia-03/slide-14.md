# Slide 14: Exercício — 03-employee-api

**Horário:** 14:00 - 15:30

---

## ✏️ Exercício: API de Gestão de Funcionários

Você recebe uma API básica que retorna a Entity diretamente. Sua missão é **profissionalizar** o código aplicando tudo que aprendemos.

```bash
cd 03-employee-api
mvn spring-boot:run
# Porta: 8084
```

---

## 📊 Visão Geral do Exercício

```mermaid
graph LR
    subgraph "📦 O que você recebe"
        A["Entity Employee<br/>Entity Department"]
        B["Repository básico"]
        C["Controller que<br/>retorna Entity ❌"]
        D["application.yml"]
    end

    subgraph "🎯 O que você deve construir"
        E["DTOs<br/>Request + Response"]
        F["Mapper<br/>Entity ↔ DTO"]
        G["Service com<br/>regras de negócio"]
        H["GlobalExceptionHandler<br/>+ ProblemDetail"]
        I["Bean Validation<br/>@Valid no Controller"]
        J["Custom @ValidCpf"]
        K["Reestruturar para<br/>Hexagonal"]
    end

    A --> E
    B --> K
    C --> G
    C --> I

    style C fill:#e74c3c,color:#fff
    style E fill:#2ecc71,color:#fff
    style F fill:#2ecc71,color:#fff
    style G fill:#2ecc71,color:#fff
    style H fill:#2ecc71,color:#fff
    style I fill:#2ecc71,color:#fff
    style J fill:#e67e22,color:#fff
    style K fill:#3498db,color:#fff
```

---

## O que já vem pronto

- ✅ Entidades `Employee` e `Department` (com `@ManyToOne`)
- ✅ `EmployeeRepository` e `DepartmentRepository`
- ✅ `application.yml` configurado (H2, porta 8084)
- ✅ Dependências no `pom.xml`
- ✅ `EmployeeController` básico (retorna Entity diretamente — com 7 TODOs)
- ✅ `api-requests.http` com requisições para testar

---

## TODOs (implemente na ordem!)

### Fluxo de Implementação

```mermaid
flowchart TD
    T1["TODO 1: Criar DTOs<br/>EmployeeRequest + EmployeeResponse<br/>⭐ Fácil — 10min"]
    T2["TODO 2: Criar EmployeeMapper<br/>⭐ Fácil — 10min"]
    T3["TODO 3: Implementar EmployeeService<br/>⭐⭐ Médio — 15min"]
    T4["TODO 4: GlobalExceptionHandler<br/>⭐⭐ Médio — 15min"]
    T5["TODO 5: Bean Validation nos DTOs<br/>⭐ Fácil — 10min"]
    T6["TODO 6: Custom @ValidCpf<br/>⭐⭐⭐ Difícil — 15min"]
    T7["TODO 7: Reestruturar para Hexagonal<br/>⭐⭐ Médio — 15min"]

    T1 --> T2 --> T3 --> T4 --> T5 --> T6 --> T7

    style T1 fill:#2ecc71,color:#fff
    style T2 fill:#2ecc71,color:#fff
    style T3 fill:#f39c12,color:#fff
    style T4 fill:#f39c12,color:#fff
    style T5 fill:#2ecc71,color:#fff
    style T6 fill:#e74c3c,color:#fff
    style T7 fill:#3498db,color:#fff
```

---

### TODO 1: Criar DTOs

```java
// EmployeeRequest (entrada — o que o cliente envia)
public record EmployeeRequest(
    String name,          // TODO 5: adicionar @NotBlank @Size(min=3, max=100)
    String email,         // TODO 5: adicionar @NotBlank @Email
    BigDecimal salary,    // TODO 5: adicionar @NotNull @Positive
    String cpf,           // TODO 6: adicionar @ValidCpf
    Long departmentId     // TODO 5: adicionar @NotNull
) {}

// EmployeeResponse (saída — o que o cliente recebe)
public record EmployeeResponse(
    Long id,
    String name,
    String email,
    BigDecimal salary,
    String cpf,
    String departmentName,   // ← nome do departamento, não o ID!
    LocalDateTime createdAt
) {}
```

### TODO 2: Criar EmployeeMapper

```java
public class EmployeeMapper {
    public static Employee toEntity(EmployeeRequest request, Department department) {
        Employee employee = new Employee();
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setSalary(request.salary());
        employee.setCpf(request.cpf());
        employee.setDepartment(department);  // ← buscar pelo departmentId
        return employee;
    }

    public static EmployeeResponse toResponse(Employee entity) {
        return new EmployeeResponse(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getSalary(),
            entity.getCpf(),
            entity.getDepartment().getName(),  // ← nome, não o ID!
            entity.getCreatedAt()
        );
    }
}
```

### TODO 3: Implementar EmployeeService

```mermaid
flowchart TD
    A["create(EmployeeRequest)"] --> B{"Email já existe?"}
    B -->|"Sim"| C["throw DuplicateEmailException<br/>409 Conflict"]
    B -->|"Não"| D{"Salário >= R$ 1.412?"}
    D -->|"Não"| E["throw MinimumSalaryException<br/>422 Unprocessable"]
    D -->|"Sim"| F{"Department existe?"}
    F -->|"Não"| G["throw DepartmentNotFoundException<br/>404 Not Found"]
    F -->|"Sim"| H["Salvar Employee"]
    H --> I["Retornar EmployeeResponse"]

    style C fill:#e74c3c,color:#fff
    style E fill:#e67e22,color:#fff
    style G fill:#e74c3c,color:#fff
    style I fill:#2ecc71,color:#fff
```

```java
// Regras de negócio do Service:
// 1. Salário não pode ser menor que R$ 1.412,00 (salário mínimo)
// 2. Email deve ser único (verificar no banco)
// 3. Department deve existir (buscar por departmentId)
private static final BigDecimal MINIMUM_SALARY = new BigDecimal("1412.00");
```

### TODO 4: GlobalExceptionHandler com Problem Details

```java
// Exceções para tratar:
// - MethodArgumentNotValidException → 400 (validação de campos)
// - EmployeeNotFoundException → 404
// - DepartmentNotFoundException → 404
// - DuplicateEmailException → 409
// - MinimumSalaryException → 422
// - Exception (catch-all) → 500
```

### TODO 5: Bean Validation nos DTOs

```java
// Voltar no EmployeeRequest e adicionar:
// @NotBlank @Size(min=3, max=100) no name
// @NotBlank @Email no email
// @NotNull @Positive no salary
// @NotNull no departmentId
```

### TODO 6: Custom Validator @ValidCpf

```java
// 1. Criar @interface ValidCpf com @Constraint(validatedBy = CpfValidator.class)
// 2. Criar CpfValidator implements ConstraintValidator<ValidCpf, String>
//    - Remover pontuação
//    - 11 dígitos
//    - Não todos iguais
//    - Algoritmo dos dígitos verificadores
// 3. Usar @ValidCpf no campo cpf do EmployeeRequest
```

### TODO 7: Refatorar para estrutura hexagonal

```
// ANTES (flat):
// com.example.employeeapi/
//   ├── Employee.java, Department.java
//   ├── EmployeeRepository.java
//   ├── EmployeeService.java
//   └── EmployeeController.java

// DEPOIS (hexagonal):
// com.example.employeeapi/
//   ├── domain/
//   │   ├── model/ → Employee.java, Department.java
//   │   ├── port/in/ → EmployeeUseCase.java
//   │   ├── port/out/ → EmployeeRepositoryPort.java
//   │   ├── service/ → EmployeeService.java
//   │   └── exception/ → *Exception.java
//   ├── adapter/
//   │   ├── in/web/ → Controller, DTOs, Mapper, Handler
//   │   └── out/persistence/ → JpaEmployeeRepository, Entity
//   └── config/
//       └── BeanConfig.java
```

---

## ⏱️ Tempo sugerido por TODO

| TODO | Tarefa | Tempo | Dificuldade | Conceito aplicado |
|:----:|--------|:-----:|:-----------:|-------------------|
| 1 | DTOs (Request + Response) | 10min | ⭐ | Records, separação Entity/DTO |
| 2 | Mapper | 10min | ⭐ | Conversão Entity ↔ DTO |
| 3 | Service com regras | 15min | ⭐⭐ | Lógica de negócio, Custom Exceptions |
| 4 | ExceptionHandler | 15min | ⭐⭐ | @ControllerAdvice, ProblemDetail |
| 5 | Validation | 10min | ⭐ | @Valid, @NotBlank, @Email, @Positive |
| 6 | Custom @ValidCpf | 15min | ⭐⭐⭐ | Custom Validator, @Constraint |
| 7 | Hexagonal | 15min | ⭐⭐ | Ports & Adapters, mover pacotes |
| **Total** | | **90min** | | |

---

## 🧪 Como validar seu trabalho

```mermaid
flowchart LR
    A["Implementar TODO"] --> B["mvn spring-boot:run"]
    B --> C["Testar com api-requests.http"]
    C --> D{"Funciona?"}
    D -->|"Sim ✅"| E["Próximo TODO"]
    D -->|"Não ❌"| F["Verificar erro no console<br/>Comparar com 03-clean-architecture-demo"]
    F --> A
```

### Testes para cada TODO completo:

| Após TODO | Testar | Resultado esperado |
|:---------:|--------|-------------------|
| 1-2 | POST employee | Retorna EmployeeResponse (sem campos internos) |
| 3 | POST com email duplicado | 409 Conflict |
| 3 | POST com salário R$ 500 | 422 Unprocessable Entity |
| 4 | GET /employees/999 | 404 ProblemDetail JSON |
| 5 | POST com name="" | 400 ProblemDetail + errors map |
| 6 | POST com cpf="111.111.111-11" | 400 {cpf: "CPF inválido"} |

---

## 💡 Dica

Use o `03-clean-architecture-demo` como referência! A estrutura é a mesma, só muda o domínio (Products → Employees).

> **Se travar:** Abra o arquivo correspondente no demo e adapte para Employee/Department. Não reinvente — **adapte!**
