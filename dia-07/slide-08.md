# Slide 8: Observabilidade em Produção (Conceitual)

**Horário:** 13:00 - 13:15

---

## Os 3 Pilares da Observabilidade

```mermaid
graph TD
    OBS["🔭 Observabilidade"]

    OBS --> METRICS["📊 Métricas<br/>'Quantos requests/s?'<br/>'Quanto de memória está usando?'"]
    OBS --> LOGS["📋 Logs<br/>'O que aconteceu?'<br/>'Por que deu erro?'"]
    OBS --> TRACES["🔗 Traces<br/>'Qual caminho a requisição percorreu?'<br/>'Onde demorou mais?'"]

    METRICS -->|"Prometheus<br/>Datadog<br/>CloudWatch"| ALERT["🚨 Alertas<br/>'CPU > 90% por 5 min'"]
    LOGS -->|"ELK Stack<br/>Datadog<br/>Splunk"| SEARCH["🔍 Busca<br/>'Erros nas últimas 2h'"]
    TRACES -->|"Zipkin / Jaeger<br/>Datadog APM<br/>New Relic"| FLOW["🗺️ Fluxo<br/>'Request demorou 3s no DB'"]

    style METRICS fill:#3498db,color:#fff
    style LOGS fill:#2ecc71,color:#fff
    style TRACES fill:#9b59b6,color:#fff
```

---

## O que cada pilar resolve

| Pilar | Pergunta que responde | Exemplo | Ferramenta |
|-------|----------------------|---------|-----------|
| **Métricas** | "Está rápido?" "Está saudável?" | CPU 85%, Latência p99 = 500ms | Prometheus + Grafana |
| **Logs** | "O que aconteceu?" "Por que falhou?" | `ERROR: Connection refused to DB` | ELK Stack, Datadog |
| **Traces** | "Qual o caminho?" "Onde demorou?" | App → DB (200ms) → Redis (5ms) → RabbitMQ (10ms) | Zipkin, Jaeger |

---

## Distributed Tracing — Conceito

Em um sistema com **múltiplos microsserviços**, uma requisição percorre vários serviços. O **Distributed Tracing** rastreia esse caminho:

```mermaid
graph LR
    CLIENT["Cliente<br/>traceId: abc123"] -->|"GET /orders/42"| GW["API Gateway<br/>spanId: span-1"]
    GW -->|"GET /orders/42"| ORDER["Order Service<br/>spanId: span-2"]
    ORDER -->|"GET /products/5"| PRODUCT["Product Service<br/>spanId: span-3"]
    ORDER -->|"SELECT * FROM orders"| DB[("Database<br/>spanId: span-4")]
    PRODUCT -->|"GET cache"| REDIS[("Redis Cache<br/>spanId: span-5")]

    style CLIENT fill:#3498db,color:#fff
    style GW fill:#9b59b6,color:#fff
    style ORDER fill:#2ecc71,color:#fff
    style PRODUCT fill:#f39c12,color:#fff
```

### Conceitos chave

| Conceito | Descrição |
|----------|-----------|
| **Trace** | O caminho completo de uma requisição (do cliente até a resposta) |
| **Span** | Uma operação individual dentro do trace (chamada HTTP, query SQL, etc.) |
| **TraceId** | ID único do trace — compartilhado entre TODOS os serviços |
| **SpanId** | ID único de cada operação — identifica um passo específico |
| **Parent SpanId** | Quem chamou esta operação — cria a árvore de chamadas |

> **No Java**: O Micrometer (Spring Boot 3) + OpenTelemetry gera e propaga traceId/spanId automaticamente entre serviços.

---

## Stack de Observabilidade — Como as empresas montam

```mermaid
graph TB
    subgraph "1. Instrumentação (Dev)"
        ACTUATOR["Spring Actuator<br/>Métricas, Health"]
        LOGBACK["Logback + Logstash<br/>Logs JSON"]
        MICROMETER["Micrometer<br/>Traces"]
    end

    subgraph "2. Coleta"
        PROM["Prometheus<br/>(scrape métricas)"]
        LOGSTASH["Logstash / Fluentd<br/>(coleta logs)"]
        OTEL["OpenTelemetry Collector<br/>(coleta traces)"]
    end

    subgraph "3. Armazenamento"
        PROM_DB["Prometheus TSDB"]
        ELASTIC["Elasticsearch"]
        JAEGER_DB["Jaeger / Tempo"]
    end

    subgraph "4. Visualização"
        GRAFANA["📊 Grafana<br/>Dashboards"]
        KIBANA["🔍 Kibana<br/>Busca de logs"]
        JAEGER_UI["🗺️ Jaeger UI<br/>Trace explorer"]
    end

    subgraph "5. Alertas"
        ALERTM["AlertManager<br/>PagerDuty, Slack"]
    end

    ACTUATOR --> PROM --> PROM_DB --> GRAFANA
    LOGBACK --> LOGSTASH --> ELASTIC --> KIBANA
    MICROMETER --> OTEL --> JAEGER_DB --> JAEGER_UI
    GRAFANA --> ALERTM

    style ACTUATOR fill:#2ecc71,color:#fff
    style LOGBACK fill:#2ecc71,color:#fff
    style MICROMETER fill:#2ecc71,color:#fff
```

---

## O Papel do Desenvolvedor

```mermaid
graph LR
    subgraph "Responsabilidade do DEV"
        R1["✅ Instrumentar a aplicação<br/>Actuator, logs JSON, Micrometer"]
        R2["✅ Usar traceId nos logs<br/>MDC com correlação"]
        R3["✅ Definir health checks<br/>Custom Health Indicators"]
        R4["✅ Escolher níveis de log<br/>INFO para negócio, ERROR para falhas"]
    end

    subgraph "Responsabilidade da INFRA/DevOps"
        I1["Configurar Prometheus/Grafana"]
        I2["Configurar ELK Stack"]
        I3["Configurar alertas"]
        I4["Manter infraestrutura"]
    end

    R1 -->|"A infra CONSOME<br/>o que o dev PRODUZ"| I1

    style R1 fill:#2ecc71,color:#fff
    style R2 fill:#2ecc71,color:#fff
    style R3 fill:#2ecc71,color:#fff
    style R4 fill:#2ecc71,color:#fff
```

> **Mensagem central**: O dev instrumenta (Actuator, logs, traceId). A infra consome (Prometheus, ELK, alertas). **Sem instrumentação do dev, a infra não tem o que monitorar.**

---

## 🎯 Quiz Rápido

1. **Quais são os 3 pilares da observabilidade?**
   - Métricas, Logs e Traces.

2. **Qual a diferença entre traceId e spanId?**
   - `traceId` é compartilhado entre todos os serviços (identifica a requisição). `spanId` identifica uma operação específica dentro do trace.

3. **O que o dev precisa fazer para observabilidade?**
   - Adicionar Actuator, gerar logs JSON estruturados, usar MDC com traceId, criar Health Indicators.
