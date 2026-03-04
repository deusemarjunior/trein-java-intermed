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

## � Podman e Podman Compose

O projeto usa **Podman** como runtime de containers e **podman-compose** para orquestrar os serviços definidos no `docker-compose.yml`.

### Instalando o Podman

Baixe e instale o Podman Desktop a partir de: https://podman-desktop.io/

Após a instalação, verifique:

```bash
podman --version
```

### Instalando o podman-compose via Python (pip)

Caso o `podman-compose` não esteja disponível, instale via pip:

```bash
pip install podman-compose
```

Verifique a instalação:

```bash
podman-compose --version
```

### Subindo os containers

```bash
# Iniciar os serviços (PostgreSQL) em background
podman-compose up -d

# Verificar se o container está rodando
podman ps -a

# Ver logs do container
podman-compose logs -f

# Parar os serviços
podman-compose down
```

### Configuração do banco

O `docker-compose.yml` sobe um **PostgreSQL 16** com as seguintes credenciais:

| Variável            | Valor          |
|---------------------|----------------|
| `POSTGRES_DB`       | employeedb     |
| `POSTGRES_USER`     | employee       |
| `POSTGRES_PASSWORD` | employee123    |
| Porta               | 5432           |

## �📝 Testar

Use o arquivo `api-requests.http` com VS Code REST Client ou Postman.
