# Slide 6: Migrations com Flyway

**Horário:** 11:30 - 12:00

---

## O Problema: `ddl-auto: update` em Produção

```yaml
# ❌ Nunca em produção!
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

### O que pode dar errado?

```mermaid
graph TD
    subgraph "Cenários perigosos com ddl-auto: update"
        A["Renomear coluna<br/>name → full_name"] -->|"Hibernate"| A1["🔴 DROPA name<br/>CRIA full_name<br/>Dados PERDIDOS"]
        B["Remover campo da Entity"] -->|"Hibernate"| B1["🟡 Coluna orfã<br/>fica no banco"]
        C["Alterar tipo String → Integer"] -->|"Hibernate"| C1["🔴 ALTER TABLE falha<br/>se tiver dados"]
        D["2 devs fazem mudanças"] -->|"Ambientes diferentes"| D1["🟡 'Na minha máquina<br/>funciona'"]
    end

    style A1 fill:#e74c3c,color:#fff
    style C1 fill:#e74c3c,color:#fff
```

| Cenário | O que o Hibernate faz | Risco |
|---------|----------------------|-------|
| Renomear coluna `name` → `full_name` | **Dropa** `name` e **cria** `full_name` | 🔴 Perda de dados |
| Remover campo da Entity | Não remove a coluna | 🟡 Colunas órfãs |
| Alterar tipo de `String` → `Integer` | Tenta `ALTER TABLE` | 🔴 Falha se tiver dados |
| Dois devs fazem mudanças diferentes | Cada ambiente fica diferente | 🟡 "Na minha máquina funciona" |

### Valores de ddl-auto — Quando usar cada um?

| Valor | O que faz | Quando usar |
|-------|----------|-------------|
| `none` | Nada | Produção com Flyway |
| `validate` | Verifica se entities = schema | **Recomendado com Flyway** |
| `update` | Cria/altera tabelas automaticamente | Prototipação rápida (nunca prod) |
| `create` | Dropa e recria tudo a cada start | Testes com banco in-memory |
| `create-drop` | Cria no start, dropa no shutdown | Testes unitários |

> **Em consultorias e fintechs**: `ddl-auto: update` é **proibido** em qualquer ambiente que não seja local de desenvolvimento.

---

## Flyway — Git para o Banco de Dados

O **Flyway** versiona o schema do banco usando **scripts SQL incrementais**. Cada alteração é um arquivo `.sql` com versão.

```mermaid
flowchart LR
    V1["V1__create_departments.sql<br/><i>CREATE TABLE departments</i>"]
    V2["V2__create_employees.sql<br/><i>CREATE TABLE employees</i>"]
    V3["V3__add_status_column.sql<br/><i>ALTER TABLE employees<br/>ADD status VARCHAR(20)</i>"]
    V4["V4__insert_initial_data.sql<br/><i>INSERT INTO departments...</i>"]

    V1 --> V2 --> V3 --> V4

    style V1 fill:#3498db,color:#fff
    style V2 fill:#3498db,color:#fff
    style V3 fill:#2ecc71,color:#fff
    style V4 fill:#9b59b6,color:#fff
```

### Analogia: Flyway é como Git para o banco

```mermaid
graph LR
    subgraph "Git (código)"
        C1["Commit 1"] --> C2["Commit 2"] --> C3["Commit 3"]
    end

    subgraph "Flyway (banco)"
        M1["V1__create.sql"] --> M2["V2__alter.sql"] --> M3["V3__insert.sql"]
    end

    style C1 fill:#f05032,color:#fff
    style C2 fill:#f05032,color:#fff
    style C3 fill:#f05032,color:#fff
    style M1 fill:#3498db,color:#fff
    style M2 fill:#3498db,color:#fff
    style M3 fill:#3498db,color:#fff
```

| Git | Flyway |
|-----|--------|
| Commits são incrementais | Migrations são incrementais |
| Cada commit tem um hash único | Cada migration tem versão + checksum |
| `git log` mostra histórico | `flyway_schema_history` mostra histórico |
| Não se altera commit já pushado | Não se altera migration já aplicada |

### Como funciona — Fluxo de Execução

```mermaid
sequenceDiagram
    participant App as Spring Boot
    participant FW as Flyway
    participant DB as PostgreSQL

    App->>FW: Aplicação iniciando...
    FW->>DB: Tabela flyway_schema_history existe?
    alt Primeira execução
        DB-->>FW: Não existe
        FW->>DB: CREATE TABLE flyway_schema_history
    else Já existe
        DB-->>FW: Sim, retorna migrations aplicadas
    end

    FW->>FW: Escanear db/migration/
    FW->>FW: Comparar: migrations no disco vs aplicadas

    loop Para cada migration pendente (em ordem)
        FW->>DB: Verificar checksum (não foi alterada?)
        FW->>DB: Executar SQL (V{n}__descricao.sql)
        FW->>DB: INSERT INTO flyway_schema_history
    end

    FW-->>App: ✅ Migrations aplicadas com sucesso
    App->>App: Hibernate valida schema (ddl-auto: validate)
```

1. Flyway examina a pasta `db/migration/`
2. Verifica a tabela `flyway_schema_history` no banco
3. Executa **apenas** as migrations ainda não aplicadas
4. Registra cada migration executada com sucesso

```sql
-- Tabela flyway_schema_history (criada automaticamente)
SELECT version, description, checksum, installed_on, execution_time, success
FROM flyway_schema_history;

-- Resultado:
-- 1 | create departments | -12345678 | 2026-02-22 09:00:00 | 45  | true
-- 2 | create employees   | -87654321 | 2026-02-22 09:00:01 | 120 | true
-- 3 | add status column  | -11223344 | 2026-02-22 09:00:02 | 30  | true
```

---

## Convenção de Nomes

```
V{versão}__{descrição}.sql
 ↑            ↑
 |            └── Descrição com underscores (obrigatório)
 └── Número da versão (sequencial)

⚠️ DOIS underscores entre versão e descrição!
```

```mermaid
graph LR
    subgraph "✅ Nomes Válidos"
        N1["V1__create_departments.sql"]
        N2["V2__create_employees.sql"]
        N3["V10__add_index.sql"]
        N4["V2.1__hotfix_column.sql"]
    end

    subgraph "❌ Nomes Inválidos"
        I1["V1_create.sql<br/>(1 underscore)"]
        I2["create.sql<br/>(sem versão)"]
        I3["V1__Create Deps.sql<br/>(espaços)"]
    end

    style N1 fill:#2ecc71,color:#fff
    style N2 fill:#2ecc71,color:#fff
    style N3 fill:#2ecc71,color:#fff
    style N4 fill:#2ecc71,color:#fff
    style I1 fill:#e74c3c,color:#fff
    style I2 fill:#e74c3c,color:#fff
    style I3 fill:#e74c3c,color:#fff
```

| Arquivo | Válido? | Motivo |
|---------|:---:|--------|
| `V1__create_departments.sql` | ✅ | Formato correto |
| `V2__create_employees.sql` | ✅ | Formato correto |
| `V10__add_index.sql` | ✅ | Versão pode ter múltiplos dígitos |
| `V1_create_departments.sql` | ❌ | Apenas UM underscore |
| `create_departments.sql` | ❌ | Sem prefixo de versão |
| `V1__Create Departments.sql` | ❌ | Espaços no nome |

### Tipos de Migration

| Prefixo | Tipo | Uso |
|---------|------|-----|
| `V` | **Versioned** | DDL (CREATE, ALTER) — executada uma vez |
| `U` | **Undo** (Pro) | Rollback de uma versioned — apenas Flyway Teams |
| `R` | **Repeatable** | Re-executada quando alterada (views, procedures) |

---

## Estrutura de Pastas

```
src/main/resources/
└── db/
    └── migration/
        ├── V1__create_departments.sql     ← DDL: estrutura
        ├── V2__create_employees.sql       ← DDL: estrutura + índices
        ├── V3__add_status_column.sql      ← DDL: evolução do schema
        └── V4__insert_initial_data.sql    ← DML: dados iniciais (seed)
```

---

## Exemplos de Migrations

### V1 — Criar tabela de departamentos

```sql
-- V1__create_departments.sql
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### V2 — Criar tabela de funcionários

```sql
-- V2__create_employees.sql
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    salary DECIMAL(10,2) NOT NULL,
    department_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- Índices para queries frequentes
CREATE INDEX idx_employee_email ON employees(email);
CREATE INDEX idx_employee_department ON employees(department_id);
```

> **Boas práticas SQL**: Sempre crie índices para colunas usadas em WHERE, JOIN e ORDER BY. Foreign keys não criam índice automaticamente no PostgreSQL.

### V3 — Adicionar coluna (evolução do schema)

```sql
-- V3__add_status_column.sql
ALTER TABLE employees
    ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL;
```

### V4 — Dados iniciais (seed data)

```sql
-- V4__insert_initial_data.sql
INSERT INTO departments (name, code) VALUES
    ('Engenharia', 'ENG'),
    ('Marketing', 'MKT'),
    ('Recursos Humanos', 'RH'),
    ('Financeiro', 'FIN');
```

---

## Configuração no Spring Boot

### pom.xml

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

### application.yml

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true    # Cria baseline se banco não está vazio

  jpa:
    hibernate:
      ddl-auto: validate          # Apenas VALIDA, não altera schema
```

> **`ddl-auto: validate`**: o Hibernate verifica se as entities Java batem com o schema no banco. Se não bater, a aplicação **não sobe** — evitando surpresas.

---

## Regras de Ouro do Flyway

```mermaid
graph TD
    subgraph "✅ Pode"
        P1["Criar novas migrations<br/>V5__add_phone.sql"]
        P2["Adicionar colunas com DEFAULT"]
        P3["Criar índices"]
        P4["Inserir dados iniciais"]
    end

    subgraph "❌ Não pode"
        N1["🔴 Alterar migration já aplicada<br/>(checksum mismatch)"]
        N2["🔴 Deletar migration<br/>(Flyway percebe que falta)"]
        N3["🔴 Pular versão<br/>V1, V3 sem V2"]
    end

    style N1 fill:#e74c3c,color:#fff
    style N2 fill:#e74c3c,color:#fff
    style N3 fill:#e74c3c,color:#fff
    style P1 fill:#2ecc71,color:#fff
```

### O que acontece ao violar as regras?

```mermaid
sequenceDiagram
    participant Dev as Desenvolvedor
    participant FW as Flyway
    participant DB as PostgreSQL

    Dev->>Dev: Edita V2__create_employees.sql (já aplicada!)
    Dev->>FW: mvn spring-boot:run
    FW->>DB: Buscar checksum de V2 na flyway_schema_history
    DB-->>FW: checksum = -87654321
    FW->>FW: Calcular checksum do arquivo V2
    FW->>FW: checksum = -99999999 (DIFERENTE!)
    FW-->>Dev: ❌ FlywayValidateException: checksum mismatch!
    Note over Dev,DB: Aplicação NÃO sobe. Solução: reverter a alteração<br/>ou flyway repair (cuidado!)
```

### Rollback

```sql
-- Para reverter uma migration, crie uma NOVA migration corretiva:
-- V6__rollback_status_column.sql
ALTER TABLE employees DROP COLUMN status;
```

> **Rollback no Flyway Community é manual** — você cria um novo script SQL que desfaz a alteração. Flyway Teams (pago) suporta `U` (Undo) migrations.

---

## Flyway vs. Liquibase

| Aspecto | Flyway | Liquibase |
|---------|--------|-----------|
| **Formato** | SQL puro | XML, YAML, JSON ou SQL |
| **Curva de aprendizado** | 🟢 Fácil (é só SQL) | 🟠 Moderada (changelog XML) |
| **Rollback** | Manual (nova migration) | Automático (rollback tag) |
| **Database diff** | ❌ Não | ✅ Sim |
| **Popularidade Spring** | ⭐ Mais usado | ⭐ Muito usado também |

> **Flyway é a escolha natural para Spring Boot** — mais simples, SQL puro, e integração out-of-the-box.

---

## 🎯 Resumo — Flyway

| Conceito | Detalhe |
|----------|---------|
| **O que é** | Versionamento de schema via scripts SQL |
| **Onde ficam** | `src/main/resources/db/migration/` |
| **Nomenclatura** | `V{n}__{descrição}.sql` (dois underscores) |
| **Execução** | Automática ao subir a aplicação |
| **Histórico** | Tabela `flyway_schema_history` (checksum!) |
| **ddl-auto** | Usar `validate` (nunca `update` em prod) |
| **Regra de ouro** | Nunca alterar migration já aplicada |
| **Rollback** | Criar nova migration corretiva |

> **Almoço!** 🍽️ Voltem prontos para mensageria com RabbitMQ e cache com Redis.
