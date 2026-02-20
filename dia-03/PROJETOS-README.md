# 📚 Projetos Java - Dia 03

## 📖 Ordem de Estudo Recomendada

### 1️⃣ **03-clean-architecture-demo** (Projeto Completo - Demonstração)
**Objetivo**: Projeto completo demonstrando Clean Code, Arquitetura em Camadas e Hexagonal, tratamento de erros global e validação.

**Conceitos**:
- Arquitetura em camadas: `ProductController` → `ProductService` → `ProductRepository`
- Pacotes hexagonais: `domain/`, `adapter/in/web/`, `adapter/out/persistence/`
- DTOs com `ProductRequest` / `ProductResponse` e mapeamento via `ProductMapper`
- `GlobalExceptionHandler` retornando Problem Details (RFC 7807)
- Custom exceptions: `ProductNotFoundException`, `DuplicateSkuException`
- Validadores: `@Valid` nos DTOs + custom validator `@ValidSku`
- CRUD completo com endpoints REST

**Porta**: 8083  
**Arquivo de Testes**: `api-requests.http`

```bash
cd 03-clean-architecture-demo
mvn spring-boot:run
```

---

### 2️⃣ **03-employee-api** (Exercício: DTOs, Validação e Hexagonal)
**Objetivo**: Construir uma API de Gestão de Funcionários aplicando todos os conceitos do dia.

**Conceitos**:
- Criação de DTOs (EmployeeRequest, EmployeeResponse)
- Mapeamento Entity ↔ DTO com EmployeeMapper
- Regras de negócio no Service (salário mínimo, email único)
- GlobalExceptionHandler com Problem Details
- Bean Validation (`@NotBlank`, `@Email`, `@Positive`)
- Custom Validator `@ValidCpf`
- Refatoração para estrutura hexagonal

**Porta**: 8084  
**Arquivo de Testes**: `api-requests.http`

```bash
cd 03-employee-api
mvn spring-boot:run
```

**TODOs a implementar**: 7 (DTOs, Mapper, Service, ExceptionHandler, Validation, CustomValidator, Hexagonal)

---

### 3️⃣ **03-bad-practices-lab** (Exercício: Refatoração)
**Objetivo**: Código propositalmente ruim que o aluno deve refatorar aplicando Clean Code e boas práticas.

**Conceitos**:
- God Method → Extract Method
- God Class → Single Responsibility (SRP)
- Nomes sem significado → Nomes descritivos
- Código duplicado → DRY
- Entity no Controller → DTOs
- if/else encadeado → Strategy Pattern
- try/catch genérico → Exceptions específicas
- Números mágicos → Constantes

**Porta**: 8085  
**Arquivo de Testes**: `api-requests.http`

```bash
cd 03-bad-practices-lab
mvn test   # Rodar ANTES de refatorar — devem passar
mvn spring-boot:run
```

**TODOs a refatorar**: 9 (todos os testes devem continuar passando)

---

## 🚀 Como Usar

### 1. **Estude primeiro o projeto completo** (03-clean-architecture-demo)
   - Execute: `cd 03-clean-architecture-demo && mvn spring-boot:run`
   - Teste todos os endpoints usando `api-requests.http`
   - Observe a estrutura de pacotes hexagonal
   - Analise os DTOs, Mapper e ExceptionHandler

### 2. **Pratique com os exercícios**:

   **Exercício 1 — Employee API (Construção)**
   ```bash
   cd 03-employee-api
   mvn spring-boot:run
   ```
   - Implemente os TODOs 1-7 na ordem
   - Teste cada TODO com `api-requests.http`
   - Valide que erros retornam Problem Details

   **Exercício 2 — Bad Practices Lab (Refatoração)**
   ```bash
   cd 03-bad-practices-lab
   mvn test   # green ✅
   # refatore...
   mvn test   # green ✅
   ```
   - Rode os testes ANTES de refatorar
   - Refatore um TODO por vez
   - Rode os testes DEPOIS de cada refatoração
   - Ciclo: green → refactor → green

---

## 🔑 Dicas

- Use os atalhos da IDE para refatorar: `Ctrl+Alt+M` (Extract Method), `Shift+F6` (Rename)
- Na dúvida, olhe o `03-clean-architecture-demo` como referência
- `Problem Details` é nativo no Spring Boot 3.x — use `ProblemDetail.forStatusAndDetail()`
- Custom Validators precisam de DUAS classes: a anotação e o validator
