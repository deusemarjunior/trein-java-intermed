# 07-podman-actuator-demo

Projeto completo de demonstração do **Dia 7** — Podman, Actuator, Logs Estruturados e Observabilidade.

## Objetivo

Demonstrar a containerização completa de uma aplicação Spring Boot com todos os conceitos do dia:

1. **Containerfile multi-stage** — Build com JDK + Runtime com JRE Alpine (~200MB)
2. **.containerignore** — Excluir arquivos desnecessários do contexto Podman
3. **Podman Compose** — Orquestrar app + PostgreSQL + RabbitMQ + Redis com health checks
4. **Spring Actuator** — Endpoints `/health`, `/metrics`, `/info` + custom HealthIndicator
5. **Logs estruturados** — Logback com LogstashEncoder (JSON no profile `prod`)
6. **MDC (traceId)** — Correlacionar logs de uma mesma requisição

## Pré-requisitos

- Java 21
- Maven 3.9+
- Podman Desktop

## Como executar

### Opção 1: Podman Compose (tudo containerizado)

```bash
# Build e subir todos os containers
podman compose up --build -d

# Verificar status
podman compose ps

# Verificar logs da aplicação (JSON estruturado)
podman compose logs app --tail=20

# Parar tudo
podman compose down
```

A API sobe na porta **8080**.

### Opção 2: Local (apenas infraestrutura no Podman)

```bash
# Subir apenas os serviços de infraestrutura
podman compose up postgres rabbitmq redis -d

# Rodar a aplicação localmente (profile dev — logs texto)
mvn spring-boot:run
```

## Estrutura do projeto

```
07-podman-actuator-demo/
├── .containerignore                         ← Exclusões do build Podman
├── .vscode/
│   ├── launch.json                       ← Configs de execução VS Code
│   └── tasks.json                        ← Tasks Maven e Podman
├── api-requests.http                     ← Testes de API (REST Client)
├── podman-compose.yml                    ← Orquestração completa
├── Containerfile                            ← Multi-stage build
├── pom.xml                               ← Dependências (Actuator + Logstash)
└── src/main/
    ├── java/com/example/demo/
    │   ├── PodmanActuatorDemoApplication.java
    │   ├── config/
    │   │   ├── CacheConfig.java          ← Redis cache config
    │   │   ├── MdcFilter.java            ← Filtro MDC (traceId, method, uri)
    │   │   └── RabbitMQConfig.java       ← Exchange, Queue, Binding
    │   ├── controller/
    │   │   └── ProductController.java    ← REST endpoints
    │   ├── dto/
    │   │   ├── ProductCreatedEvent.java  ← Evento RabbitMQ
    │   │   ├── ProductRequest.java       ← DTO entrada
    │   │   └── ProductResponse.java      ← DTO saída
    │   ├── exception/
    │   │   ├── GlobalExceptionHandler.java
    │   │   └── ResourceNotFoundException.java
    │   ├── health/
    │   │   └── RabbitMQHealthIndicator.java  ← Custom health check
    │   ├── messaging/
    │   │   ├── ProductEventPublisher.java
    │   │   └── ProductNotificationConsumer.java
    │   ├── model/
    │   │   └── Product.java              ← Entidade JPA
    │   ├── repository/
    │   │   └── ProductRepository.java
    │   └── service/
    │       └── ProductService.java       ← Lógica + logging contextual
    └── resources/
        ├── application.yml               ← Configuração com env vars
        ├── logback-spring.xml            ← Logs por profile (dev=text, prod=JSON)
        └── db/migration/
            ├── V1__create_products_table.sql
            └── V2__seed_data.sql

```

## Verificações

### Podman
```bash
# Tamanho da imagem
podman images | grep podman-actuator-demo

# Containers rodando
podman compose ps

# Logs JSON (profile prod ativo no Podman)
podman compose logs app --tail=5
```

### Actuator
```bash
# Health com detalhes de todos os componentes
curl http://localhost:8080/actuator/health | jq .

# Métricas da JVM
curl http://localhost:8080/actuator/metrics/jvm.memory.used | jq .

# Info da aplicação
curl http://localhost:8080/actuator/info | jq .
```

### API
```bash
# Listar produtos
curl http://localhost:8080/api/products | jq .

# Criar produto (observe o evento no RabbitMQ)
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Teste","description":"Desc","price":99.90,"stock":10,"category":"Test"}'
```

### RabbitMQ Management UI
- URL: http://localhost:15672
- Login: guest / guest
