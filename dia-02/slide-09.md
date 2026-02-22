# Slide 9: Review Spring Boot & Setup Persistência

**Horário:** 11:15 - 11:30

---

## 📝 Review Rápido

### O que vimos até agora?

```mermaid
graph TD
    A[Dia 1] --> B[Java Moderno]
    A --> C[Servlet + JDBC]
    B --> D[Records]
    B --> E[Sealed Classes]
    B --> F[Text Blocks]
    B --> G[Pattern Matching]
    B --> H[Stream API]
    C --> I[HTTP/REST]
    C --> J[DAO Pattern]
    C --> K[CRUD com JDBC]
    
    L[Dia 2 - Manhã] --> M[Spring Boot]
    M --> N[IoC/DI]
    M --> O[Auto-configuração]
    M --> P[Starters]
    M --> Q[Primeira API REST]
```

---

## ✅ Checklist - Todos conseguem?

```
[ ] Criar Records com validação
[ ] Usar Stream API para filtrar/transformar listas
[ ] Criar projeto Spring Boot no Initializr
[ ] Desenvolver Controller → Service → Repository
[ ] Testar API com Postman
[ ] Entender a diferença entre @Service e @Repository
```

**🤔 Dúvidas pendentes?**

---

## 🎯 Hoje vamos aprofundar

### De onde viemos → Para onde vamos

```mermaid
flowchart LR
    A[Dia 1<br/>API em Memória] --> B[Dia 2<br/>API com Banco de Dados]
    
    A1[List em memória] -.-> B1[PostgreSQL/H2]
    A2[DTOs simples] -.-> B2[DTOs validados]
    A3[CRUD básico] -.-> B3[Queries complexas]
    A4[Exceptions simples] -.-> B4[Global Exception Handler]
    
    style B fill:#90EE90
    style B1 fill:#87CEEB
    style B2 fill:#87CEEB
    style B3 fill:#87CEEB
    style B4 fill:#87CEEB
```

---

## 🔧 Setup do Dia

### 1. Verificar PostgreSQL

```bash
# Opção 1: PostgreSQL instalado
psql --version
psql -U postgres -c "SELECT version();"

# Opção 2: Podman
podman run --name postgres-dev \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=java_training \
  -p 5432:5432 \
  -d docker.io/library/postgres:15

# Testar conexão
podman exec -it postgres-dev psql -U postgres
```

---

### 2. Configurar DBeaver/pgAdmin

```
Host: localhost
Port: 5432
Database: java_training
User: postgres
Password: postgres
```

---

### 3. Dependências necessárias (pom.xml)

```xml
<!-- JPA + PostgreSQL -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- H2 para testes -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>

<!-- Validação -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## 💡 Dica do Instrutor

Hoje trabalharemos com **dados persistentes**. Cada alteração no código pode afetar o banco de dados. Use sempre:
- `ddl-auto: validate` em produção
- `ddl-auto: update` em desenvolvimento (com cuidado)
- `ddl-auto: create-drop` apenas para testes
