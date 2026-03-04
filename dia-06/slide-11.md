# Slide 11: Exercício — TODOs 1-2 (N+1)

**Horário:** 13:50 - 14:30

---

## 🎯 Exercício: `06-employee-api-advanced`

> Otimizar persistência, adicionar migrations, mensageria e cache à API de Funcionários.

### Visão Geral dos 8 TODOs

```mermaid
graph LR
    subgraph "📋 Todos os TODOs"
        T1["TODO 1<br/>Identificar N+1"]
        T2["TODO 2<br/>Corrigir N+1"]
        T3["TODO 3<br/>DTO Projection"]
        T4["TODO 4<br/>Paginação"]
        T5["TODO 5<br/>Flyway"]
        T6["TODO 6<br/>RabbitMQ Config"]
        T7["TODO 7<br/>Producer/Consumer"]
        T8["TODO 8<br/>Redis Cache"]
    end

    T1 --> T2
    T2 --> T3
    T3 --> T4
    T4 --> T5
    T5 --> T6
    T6 --> T7
    T7 --> T8

    style T1 fill:#e74c3c,color:#fff
    style T2 fill:#e74c3c,color:#fff
    style T3 fill:#3498db,color:#fff
    style T4 fill:#3498db,color:#fff
    style T5 fill:#f39c12,color:#fff
    style T6 fill:#ff6600,color:#fff
    style T7 fill:#ff6600,color:#fff
    style T8 fill:#dc382d,color:#fff
```

> Neste slide: **TODO 1 e TODO 2** (N+1 Problem)

### Setup Inicial

```bash
cd 06-employee-api-advanced
podman compose up -d        # Suba PostgreSQL + RabbitMQ + Redis
podman compose ps           # Verifique 3 containers healthy
mvn spring-boot:run          # Inicie a aplicação (porta 8091)
```

---

## TODO 1: Identificar o N+1

**Arquivo**: `EmployeeController.java` / `EmployeeRepository.java`

### Modelo de dados

```mermaid
classDiagram
    class Employee {
        Long id
        String name
        String email
        BigDecimal salary
        Department department
    }
    class Department {
        Long id
        String name
        String code
    }
    Employee "*" --> "1" Department : ManyToOne(LAZY)
```

### O que fazer

1. Certifique-se de que `show-sql: true` está no `application.yml`
2. Acesse `GET /api/employees` (usando `api-requests.http` ou Postman)
3. **Observe os logs SQL** no console da aplicação
4. **Conte quantas queries** são geradas

### O que esperar nos logs

```sql
-- 1ª query: buscar todos os funcionários
SELECT e.id, e.name, e.email, e.salary, e.department_id FROM employees e;

-- Para CADA funcionário: buscar o departamento (lazy loading)
SELECT d.id, d.name, d.code FROM departments d WHERE d.id = 1;
SELECT d.id, d.name, d.code FROM departments d WHERE d.id = 2;
SELECT d.id, d.name, d.code FROM departments d WHERE d.id = 1;  -- mesmo dept, outra query!
-- ... +N queries (uma por funcionário)
```

### Visualização do Problema

```mermaid
sequenceDiagram
    participant App as Spring Boot
    participant DB as PostgreSQL

    App->>DB: SELECT * FROM employees (1 query)
    DB-->>App: 10 employees

    loop Para cada employee.getDepartment().getName()
        App->>DB: SELECT * FROM departments WHERE id = ?
        DB-->>App: department
    end

    Note over App,DB: Total: 1 + 10 = 11 queries! 😱
```

### Dica

- A aplicação já tem dados iniciais (Flyway seed data ou `data.sql`)
- Use o `api-requests.http` para fazer as requisições
- **Anote quantas queries você viu** — vamos comparar depois da correção

<details>
<summary>💡 Pergunta: Se tiver 100 funcionários em 5 departamentos, quantas queries?</summary>

**101 queries!** (1 + 100). O Hibernate faz 1 query por employee, mesmo que vários compartilhem o mesmo departamento — porque o proxy é por instância do Employee, não por Department.

> Com `@BatchSize(size=10)` seriam 1 + 10 = 11 queries.  
> Com `JOIN FETCH` seria apenas **1 query**.

</details>

---

## TODO 2: Corrigir o N+1

**Arquivo**: `EmployeeRepository.java`

### O que implementar

Corrija o N+1 de **duas formas** — uma por método:

#### Opção A: JOIN FETCH na JPQL

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // TODO 2A: Corrigir N+1 com JOIN FETCH
    @Query("SELECT e FROM Employee e JOIN FETCH e.department")
    List<Employee> findAllWithDepartment();
}
```

```mermaid
graph LR
    subgraph "JOIN FETCH"
        Q["@Query JPQL"]
        Q -->|"gera"| SQL["SELECT e.*, d.*<br/>FROM employees e<br/>INNER JOIN departments d<br/>ON e.department_id = d.id"]
    end

    style SQL fill:#2ecc71,color:#fff
```

#### Opção B: @EntityGraph

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // TODO 2B: Corrigir N+1 com @EntityGraph
    @EntityGraph(attributePaths = {"department"})
    @Override
    List<Employee> findAll();
}
```

```mermaid
graph LR
    subgraph "@EntityGraph"
        Q["attributePaths = department"]
        Q -->|"gera"| SQL["SELECT e.*, d.*<br/>FROM employees e<br/>LEFT JOIN departments d<br/>ON e.department_id = d.id"]
    end

    style SQL fill:#2ecc71,color:#fff
```

### Diferença: INNER vs LEFT JOIN

| Aspecto | JOIN FETCH | @EntityGraph |
|:---|:---:|:---:|
| Tipo de JOIN | **INNER JOIN** | **LEFT JOIN** |
| Employee sem Department | ❌ Não retorna | ✅ Retorna com department null |
| Personalização | ✅ JPQL livre | ✅ Só marca atributos |
| Uso com `findAll()` | Precisa de `@Query` | Override direto |

### Verificação — Antes e Depois

```mermaid
graph LR
    subgraph "❌ ANTES (N+1)"
        A["11 queries<br/>~100ms"]
    end
    subgraph "✅ DEPOIS (JOIN FETCH)"
        B["1 query com JOIN<br/>~5ms"]
    end

    A --->|"20x mais rápido!"| B

    style A fill:#e74c3c,color:#fff
    style B fill:#2ecc71,color:#fff
```

Acesse `GET /api/employees` novamente e observe os logs:

```sql
-- ANTES (N+1): 1 + N queries
-- DEPOIS: 1 query com JOIN ✅
SELECT e.id, e.name, e.email, e.salary,
       d.id, d.name, d.code
FROM employees e
INNER JOIN departments d ON e.department_id = d.id;
```

### Checklist

- [ ] Consegui identificar o N+1 nos logs SQL
- [ ] Contei corretamente o número de queries N+1
- [ ] Implementei `findAllWithDepartment()` com `JOIN FETCH`
- [ ] Implementei `findAll()` com `@EntityGraph`
- [ ] Logs SQL mostram **1 query com JOIN** (não N+1)
- [ ] Endpoint `GET /api/employees` funciona e retorna dados corretos

---

## ⏰ Tempo estimado: 40 minutos

| Atividade | Tempo |
|-----------|-------|
| Setup + subir Podman Compose | 5 min |
| Analisar logs SQL e identificar N+1 | 10 min |
| Implementar JOIN FETCH | 10 min |
| Implementar @EntityGraph | 10 min |
| Testar e comparar logs | 5 min |

> **Dúvida?** Levante a mão. O N+1 é o conceito mais importante do dia — vale investir tempo aqui.
