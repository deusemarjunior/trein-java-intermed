# 📚 Projetos Java - Dia 04

## 📖 Ordem de Estudo Recomendada

### 1️⃣ **04-testing-demo** (Projeto Completo - Demonstração)
**Objetivo**: Projeto completo demonstrando testes unitários com JUnit 5 + Mockito e testes de integração com Testcontainers.

**Conceitos**:
- Testes unitários com `@Mock`, `@InjectMocks`, `ArgumentCaptor`
- `@ParameterizedTest` com `@CsvSource` para validações múltiplas
- Testes de integração com Testcontainers (PostgreSQL real)
- `AbstractIntegrationTest`: classe base reutilizável
- `ProductBuilder`: Data Builder fluente para massa de dados legível
- Cobertura >80% na camada Service

**Porta**: 8086  
**Arquivo de Testes**: `api-requests.http`

```bash
cd 04-testing-demo
mvn test                 # Roda todos os testes (unitários + integração)
mvn spring-boot:run      # Roda a aplicação
```

**Pré-requisito**: Docker Desktop rodando (para Testcontainers)

---

### 2️⃣ **04-employee-api-tests** (Exercício: Testes Unitários e de Integração)
**Objetivo**: Adicionar testes completos à API de Funcionários, cobrindo testes unitários com Mockito e testes de integração com Testcontainers.

**Conceitos**:
- Criar Data Builder (`EmployeeBuilder`) com valores default sensatos
- Testes unitários para `EmployeeService` (criação, validações, exceções)
- Testes parametrizados para validação de CPF
- Testes de integração com PostgreSQL real (Testcontainers)
- Verificação de constraints no banco (`@Unique` no email)
- Cobertura >80% na camada Service

**Porta**: 8087  
**Arquivo de Testes**: `api-requests.http`

```bash
cd 04-employee-api-tests
mvn test                 # Alguns testes falham (TODOs não implementados)
mvn spring-boot:run      # Roda a aplicação
```

**TODOs a implementar**: 7 (Builder, testes unitários, testes parametrizados, testes de integração)

**Pré-requisito**: Docker Desktop rodando (para Testcontainers)

---

## 🚀 Como Usar

### 1. **Verifique que o Docker está rodando**
   ```bash
   docker run hello-world   # Deve exibir "Hello from Docker!"
   ```

### 2. **Estude primeiro o projeto completo** (04-testing-demo)
   - Execute os testes: `cd 04-testing-demo && mvn test`
   - Observe a saída verde no terminal
   - Analise a estrutura das classes de teste
   - Entenda o padrão AAA (Arrange, Act, Assert) em cada teste
   - Veja como o `ProductBuilder` simplifica a criação de dados
   - Observe como o `AbstractIntegrationTest` configura o PostgreSQL

### 3. **Pratique com o exercício** (04-employee-api-tests)
   ```bash
   cd 04-employee-api-tests
   mvn test   # ver o que falha
   ```
   - Implemente os TODOs 1-7 na ordem
   - Após cada TODO, rode `mvn test` para verificar
   - Meta: todos os testes passando + cobertura >80% no Service

---

## ⚠️ Pré-requisitos

| Requisito | Verificação |
|-----------|-------------|
| Docker Desktop | `docker --version` |
| Docker rodando | `docker run hello-world` |
| JDK 21 | `java --version` |
| Maven 3.8+ | `mvn --version` |

> **IMPORTANTE**: Se o Docker não estiver rodando, os testes de integração (Testcontainers) vão falhar. Certifique-se de iniciar o Docker Desktop antes de rodar os testes.
