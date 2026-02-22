# 03-employee-api

API de Gestão de Funcionários — exercício prático para aplicar Clean Code, Arquitetura em Camadas/Hexagonal, Tratamento de Erros e Validação.

## 🎯 Objetivo

Você recebe uma API básica que retorna a Entity diretamente. Sua missão é **profissionalizar** o código aplicando os conceitos do Dia 3.

## 🚀 Como Rodar

```bash
mvn spring-boot:run
# Porta: 8084
# H2 Console: http://localhost:8084/h2-console
#   JDBC URL: jdbc:h2:mem:employeesdb
#   User: sa / Password: (vazio)
```

## 📋 TODOs

### TODO 1: Criar DTOs (EmployeeRequest e EmployeeResponse)
- `EmployeeRequest`: name, email, salary, cpf, departmentId
- `EmployeeResponse`: id, name, email, salary, cpf, departmentName, createdAt

### TODO 2: Criar EmployeeMapper
- `toEntity(EmployeeRequest)` → Employee
- `toResponse(Employee)` → EmployeeResponse

### TODO 3: Implementar EmployeeService
- Salário não pode ser menor que R$ 1.412,00
- Email deve ser único
- Nome deve ter pelo menos 3 caracteres

### TODO 4: Criar GlobalExceptionHandler
- `MethodArgumentNotValidException` → 400 com Problem Details
- `EmployeeNotFoundException` → 404
- `DuplicateEmailException` → 409

### TODO 5: Adicionar Bean Validation nos DTOs
- `@NotBlank` no nome, `@Email` no email, `@Positive` no salário

### TODO 6: Criar custom validator @ValidCpf

### TODO 7: Refatorar pacotes para estrutura hexagonal
- `domain/` → model, port/in, port/out, service, exception
- `adapter/in/web/` → controller, DTOs
- `adapter/out/persistence/` → repository, entity JPA

## 📝 Testar

Use o arquivo `api-requests.http` com VS Code REST Client ou Postman.
