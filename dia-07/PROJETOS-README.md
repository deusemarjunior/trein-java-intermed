# 📚 Projetos Java - Dia 07

## 📖 Ordem de Estudo Recomendada

### 1️⃣ **07-docker-actuator-demo** (Projeto Completo - Demonstração)
**Objetivo**: Projeto completo demonstrando Docker (multi-stage build), Docker Compose (app + banco + cache + fila com health checks), Spring Actuator, logs estruturados em JSON com Logback e MDC.

**Conceitos**:
- `Dockerfile` multi-stage build: JDK Alpine (build) → JRE Alpine (runtime), ~80MB
- `.dockerignore` configurado para builds rápidos
- `docker-compose.yml` com 4 serviços: app, PostgreSQL, RabbitMQ, Redis — todos com health checks
- Spring Actuator: `/health` (com detalhes), `/metrics`, `/info`, `/env`, `/flyway`
- Custom Health Indicator: verifica conectividade com RabbitMQ
- `logback-spring.xml` com dois profiles: `dev` (texto legível) e `prod` (JSON com LogstashEncoder)
- MDC Filter: injeta `traceId`, `method`, `uri` automaticamente em todos os logs
- Service com logging contextual usando MDC e SLF4J

**Porta**: 8080  
**Arquivo de Testes**: `api-requests.http`

```bash
# Opção 1: Docker Compose completo (recomendado)
cd 07-docker-actuator-demo
docker compose up --build -d

# Opção 2: Desenvolvimento local
cd 07-docker-actuator-demo
docker compose up -d postgres rabbitmq redis   # infra
mvn spring-boot:run                             # app
```

**Verificação**:
- App: http://localhost:8080/api/products
- Actuator: http://localhost:8080/actuator/health
- RabbitMQ UI: http://localhost:15672 (guest/guest)

---

### 2️⃣ **07-employee-api-production** (Exercício: Docker + Observabilidade)
**Objetivo**: Dockerizar a API de Funcionários, adicionar observabilidade com Spring Actuator e logs estruturados, tornando-a production-ready.

**Conceitos**:
- Otimizar Dockerfile com multi-stage build (TODO 1)
- Criar `.dockerignore` para excluir arquivos desnecessários (TODO 2)
- Completar `docker-compose.yml` com RabbitMQ, Redis, app + health checks (TODO 3)
- Configurar Spring Actuator com endpoints expostos (TODO 4)
- Implementar Custom HealthIndicator para RabbitMQ (TODO 5)
- Configurar logs JSON com LogstashEncoder + implementar MdcFilter (TODO 6)
- Adicionar logging contextual com MDC nos services (TODO 7)

**Porta**: 8092  
**Arquivo de Testes**: `api-requests.http`

```bash
# 1. Subir dependências (PostgreSQL já configurado no Compose)
cd 07-employee-api-production
docker compose up -d

# 2. Rodar a aplicação
mvn spring-boot:run
```

**TODOs a implementar**: 7 (Dockerfile, .dockerignore, Docker Compose, Actuator, HealthIndicator, Logs JSON + MDC, Logging Contextual)

---

## 🚀 Como Usar

### 1. **Estude primeiro o projeto completo** (07-docker-actuator-demo)
   - Suba tudo com Docker Compose: `cd 07-docker-actuator-demo && docker compose up --build -d`
   - Acesse http://localhost:8080/api/products — API funcionando
   - Acesse http://localhost:8080/actuator/health — veja os componentes (db, redis, rabbitMQ)
   - Veja os logs JSON: `docker compose logs -f app`
   - Crie um produto via POST e observe o `traceId` nos logs
   - Analise o `Dockerfile` multi-stage e o `docker-compose.yml` com health checks
   - Use o `api-requests.http` para testar os endpoints

### 2. **Pratique com o exercício** (07-employee-api-production)
   ```bash
   cd 07-employee-api-production
   docker compose up -d   # sobe PostgreSQL (inicialmente)
   mvn spring-boot:run    # verificar que sobe
   ```
   - Implemente os TODOs 1-7 na ordem
   - Após TODO 3, teste com `docker compose up --build -d` (stack completa)
   - Use `docker compose logs -f app` para verificar os logs JSON (após TODO 6)
   - Acesse `/actuator/health` para validar o health check customizado (após TODO 5)
   - Meta: imagem < 100MB, Actuator respondendo, logs em JSON com `traceId`

---

## ⚠️ Pré-requisitos

| Requisito | Verificação |
|-----------|-------------|
| JDK 21 | `java --version` |
| Maven 3.8+ | `mvn --version` |
| Docker Desktop | `docker --version` |
| Docker Compose | `docker compose version` |

> **IMPORTANTE**: Os projetos deste dia utilizam Docker extensivamente. Certifique-se de que o Docker Desktop está rodando antes de iniciar.

> **DICA**: Use o arquivo `api-requests.http` de cada projeto para testar os endpoints diretamente no VS Code (extensão REST Client).

---

## 🐳 Portas Utilizadas

| Projeto | App | PostgreSQL | RabbitMQ | RabbitMQ UI | Redis |
|---------|-----|-----------|----------|-------------|-------|
| 07-docker-actuator-demo | 8080 | 5432 | 5672 | 15672 | 6379 |
| 07-employee-api-production | 8092 | 5434 | 5674 | 15674 | 6381 |

> **ATENÇÃO**: Não rode os dois projetos ao mesmo tempo para evitar conflito de portas, ou certifique-se de que estejam usando portas diferentes (já configuradas).
