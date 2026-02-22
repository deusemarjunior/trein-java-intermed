# Slide 12: Exercício — TODOs 3-4 (Projeção DTO e Paginação)

**Horário:** 14:30 - 15:10

---

## TODO 3: Criar Projeção DTO `EmployeeSummary`

**Arquivos**: `dto/EmployeeSummary.java`, `EmployeeRepository.java`

### Conceito — Por que projeção DTO?

```mermaid
graph LR
    subgraph "❌ Retornar Entity"
        E1["Employee"]
        E1 --> F1["id ✅"]
        E1 --> F2["name ✅"]
        E1 --> F3["email ⚠️ desnecessário"]
        E1 --> F4["cpf 🔴 dado sensível!"]
        E1 --> F5["salary 🔴 privado!"]
        E1 --> F6["department (objeto completo)"]
    end

    subgraph "✅ Retornar DTO"
        E2["EmployeeSummary"]
        E2 --> G1["id ✅"]
        E2 --> G2["name ✅"]
        E2 --> G3["departmentName ✅"]
    end

    style E1 fill:#e74c3c,color:#fff
    style E2 fill:#2ecc71,color:#fff
```

### O que criar

Um Record com apenas os campos necessários para a listagem:

```java
// TODO 3: Criar EmployeeSummary no pacote dto
public record EmployeeSummary(
    Long id,
    String name,
    String departmentName
) {}
```

> **Record**: classe imutável do Java 16+ com `equals()`, `hashCode()`, `toString()` automáticos. Perfeita para DTOs!

### Query JPQL com projeção

```java
// No EmployeeRepository
@Query("SELECT new com.example.employee.dto.EmployeeSummary(" +
       "e.id, e.name, d.name) " +
       "FROM Employee e JOIN e.department d")
Page<EmployeeSummary> findAllSummaries(Pageable pageable);
```

### Como funciona internamente

```mermaid
sequenceDiagram
    participant JPA as Spring Data JPA
    participant Hibernate
    participant DB as PostgreSQL

    JPA->>Hibernate: findAllSummaries(pageable)
    Hibernate->>DB: SELECT e.id, e.name, d.name<br/>FROM employees e JOIN departments d<br/>ON e.department_id = d.id<br/>LIMIT 5 OFFSET 0

    Note over DB: Apenas 3 colunas!<br/>(não carrega tudo)

    DB-->>Hibernate: ResultSet (id, name, dept_name)
    Hibernate->>Hibernate: new EmployeeSummary(id, name, deptName)

    Note over Hibernate: NÃO cria proxy!<br/>NÃO gerencia no EntityManager!<br/>Objeto leve direto ✅

    Hibernate-->>JPA: Page<EmployeeSummary>
```

### Diferença na resposta

```json
// ❌ Antes (Employee completo — dados sensíveis expostos!)
{
  "id": 1,
  "name": "Ana Silva",
  "email": "ana@email.com",        // desnecessário para listagem
  "cpf": "12345678901",            // 🔴 dado sensível!
  "salary": 5000.00,               // 🔴 privado!
  "department": { "id": 1, "name": "Engenharia", "code": "ENG" },
  "createdAt": "2026-02-22T10:00:00"
}

// ✅ Depois (EmployeeSummary — apenas dados necessários)
{
  "id": 1,
  "name": "Ana Silva",
  "departmentName": "Engenharia"
}
```

### Vantagens da Projeção

| Aspecto | Entity completa | DTO Projection |
|:---|:---:|:---:|
| Dados no response | Todos (inclusive sensíveis) | Só os necessários |
| SQL gerado | `SELECT *` | `SELECT id, name, dept_name` |
| Proxy Hibernate | Sim (pesado) | Não (leve) |
| Segurança | ❌ Expõe salary, CPF | ✅ Só dados públicos |
| Performance | Mais dados trafegam | Menos dados |

---

## TODO 4: Adicionar Paginação

**Arquivo**: `EmployeeController.java`

### Conceito — Por que paginar?

```mermaid
graph LR
    subgraph "❌ Sem paginação"
        A["GET /employees"] --> B["Retorna 10.000<br/>registros de uma vez<br/>💥 OutOfMemoryError"]
    end

    subgraph "✅ Com paginação"
        C["GET /employees?page=0&size=20"] --> D["Retorna 20 registros<br/>+ metadata<br/>✅ Rápido e seguro"]
    end

    style B fill:#e74c3c,color:#fff
    style D fill:#2ecc71,color:#fff
```

### O que implementar

Receber `Pageable` como parâmetro e retornar `Page<EmployeeSummary>`:

```java
// Controller
@GetMapping
public Page<EmployeeSummary> findAll(Pageable pageable) {
    return employeeService.findAllSummaries(pageable);
}
```

```java
// Service
public Page<EmployeeSummary> findAllSummaries(Pageable pageable) {
    return employeeRepository.findAllSummaries(pageable);
}
```

### Como Spring resolve o `Pageable` automaticamente

```mermaid
sequenceDiagram
    participant Client
    participant Spring as PageableHandlerMethodArgumentResolver
    participant Controller
    participant Service
    participant Repository

    Client->>Spring: GET /employees?page=0&size=5&sort=name,asc
    Spring->>Spring: Cria PageRequest(page=0, size=5, sort=name ASC)
    Spring->>Controller: findAll(pageable)
    Controller->>Service: findAllSummaries(pageable)
    Service->>Repository: findAllSummaries(pageable)

    Note over Repository: SQL automático:<br/>SELECT ... LIMIT 5 OFFSET 0<br/>ORDER BY e.name ASC

    Repository-->>Controller: Page<EmployeeSummary>
    Controller-->>Client: JSON com content + metadata
```

### Testar

```
GET /api/employees?page=0&size=5&sort=name,asc
```

Resposta esperada:
```json
{
  "content": [
    { "id": 3, "name": "Ana Silva", "departmentName": "Engenharia" },
    { "id": 7, "name": "Bruno Costa", "departmentName": "Marketing" },
    { "id": 1, "name": "Carlos Souza", "departmentName": "Financeiro" }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": { "orders": [{ "property": "name", "direction": "ASC" }] }
  },
  "totalElements": 15,
  "totalPages": 3,
  "number": 0,
  "size": 5,
  "first": true,
  "last": false
}
```

### Parâmetros para testar

| Request | Descrição | SQL gerado |
|:---|:---|:---|
| `?page=0&size=5&sort=name,asc` | 1ª página, 5 itens, por nome | `LIMIT 5 OFFSET 0 ORDER BY name ASC` |
| `?page=1&size=5&sort=name,asc` | 2ª página | `LIMIT 5 OFFSET 5 ORDER BY name ASC` |
| `?page=0&size=10&sort=departmentName,desc` | Ordenar por dept desc | `LIMIT 10 ORDER BY d.name DESC` |
| `?page=0&size=20` | Sem sort (default) | `LIMIT 20 OFFSET 0` |

### Anatomy do Response `Page<T>`

```mermaid
graph TD
    PAGE["Page&lt;EmployeeSummary&gt;"]
    PAGE --> CONTENT["content: List&lt;EmployeeSummary&gt;<br/>[{id, name, departmentName}, ...]"]
    PAGE --> META["Metadata de paginação"]
    META --> TE["totalElements: 15"]
    META --> TP["totalPages: 3"]
    META --> NUM["number: 0 (página atual)"]
    META --> SZ["size: 5"]
    META --> FIRST["first: true"]
    META --> LAST["last: false"]

    style CONTENT fill:#2ecc71,color:#fff
    style META fill:#3498db,color:#fff
```

---

## Checklist

- [ ] `EmployeeSummary` record criado com `id`, `name`, `departmentName`
- [ ] Query JPQL com `SELECT new ... EmployeeSummary(...)` no Repository
- [ ] Query retorna `Page<EmployeeSummary>` (não List)
- [ ] Controller recebe `Pageable` e retorna `Page<EmployeeSummary>`
- [ ] Paginação funciona com `page`, `size` e `sort`
- [ ] Resposta inclui metadata (`totalElements`, `totalPages`, `first`, `last`)
- [ ] Dados sensíveis (salary, CPF) **não aparecem** no response

---

## ⏰ Tempo estimado: 40 minutos

| Atividade | Tempo |
|-----------|-------|
| Criar `EmployeeSummary` Record | 5 min |
| Implementar query JPQL com projeção | 15 min |
| Adicionar `Pageable` no Controller/Service | 10 min |
| Testar paginação e sorting | 10 min |

> **Próximo**: Flyway Migrations — versionando o schema do banco!
