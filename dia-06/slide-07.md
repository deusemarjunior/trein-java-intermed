# Slide 7: Mensageria com RabbitMQ — Conceitos

**Horário:** 13:00 - 13:10

---

## O que é Mensageria?

**Mensageria** é um padrão de comunicação **assíncrona** entre serviços, mediada por um **message broker** (intermediário). O conceito central: **"fire and forget"** — publica e segue.

```mermaid
graph LR
    subgraph "❌ Síncrono (HTTP REST)"
        A["Serviço A"] -->|"HTTP Request<br/>⏳ Espera resposta<br/>Bloqueado!"| B["Serviço B"]
        B -->|"HTTP Response<br/>Pode demorar"| A
    end
```

```mermaid
graph LR
    subgraph "✅ Assíncrono (Mensageria)"
        C["Producer<br/>Serviço A"] -->|"Publica mensagem<br/>✅ Segue em frente<br/>Não espera!"| MQ["Message Broker<br/>(RabbitMQ)"]
        MQ -->|"Entrega quando<br/>consumer estiver pronto"| D["Consumer<br/>Serviço B"]
    end
```

> **Diferença fundamental**: no HTTP, o producer **espera** a resposta. Na mensageria, o producer **publica e segue** — o consumer processa quando puder.

### Síncrono vs. Assíncrono — Comparação Detalhada

```mermaid
graph TD
    subgraph "Síncrono (Request/Response)"
        direction LR
        S1["Client"] -->|"1. Request"| S2["API"]
        S2 -->|"2. Salvar no DB"| S3[("DB")]
        S2 -->|"3. Enviar email<br/>⏱️ 2 segundos"| S4["Email Service"]
        S2 -->|"4. Gerar PDF<br/>⏱️ 5 segundos"| S5["PDF Service"]
        S2 -->|"5. Response<br/>⏱️ Total: 7+ segundos"| S1
    end
```

```mermaid
graph TD
    subgraph "Assíncrono (Event-Driven)"
        direction LR
        A1["Client"] -->|"1. Request"| A2["API"]
        A2 -->|"2. Salvar no DB"| A3[("DB")]
        A2 -->|"3. Publicar evento<br/>⏱️ ~1ms"| A4["RabbitMQ"]
        A2 -->|"4. Response<br/>⏱️ Total: ~50ms ✅"| A1
        A4 -->|"Assíncrono"| A5["Email Consumer"]
        A4 -->|"Assíncrono"| A6["PDF Consumer"]
    end
```

| Aspecto | Síncrono (HTTP) | Assíncrono (RabbitMQ) |
|---------|:---:|:---:|
| Tempo de resposta | Soma de todos os passos | Apenas DB + publish |
| Acoplamento | Forte (depende de todos os serviços) | Fraco (producer não conhece consumers) |
| Se destino estiver fora | Request falha | Mensagem fica na fila |
| Escalabilidade | Vertical | Horizontal (mais consumers) |

---

## Quando usar Mensageria?

| Cenário | HTTP (síncrono) | RabbitMQ (assíncrono) |
|---------|:---:|:---:|
| Criar funcionário e retornar dados | ✅ | ❌ |
| Enviar email de boas-vindas após criar | ❌ Bloqueia a API | ✅ |
| Gerar relatório PDF pesado | ❌ Timeout | ✅ |
| Notificar outro serviço | ❌ Acoplamento | ✅ |
| Serviço destino fora do ar | ❌ Falha | ✅ Mensagem fica na fila |
| Processar pagamento em background | ❌ Timeout | ✅ |
| Atualizar cache distribuído | ❌ Acoplamento | ✅ |

### Exemplo Real — Fluxo Completo

```mermaid
sequenceDiagram
    participant Client
    participant API as Employee API
    participant DB as PostgreSQL
    participant MQ as RabbitMQ
    participant Email as Email Service
    participant Audit as Audit Service

    Client->>API: POST /employees
    API->>DB: INSERT employee
    DB-->>API: OK (id: 42)
    API->>MQ: publish(EmployeeCreatedEvent)
    API-->>Client: 201 Created (resposta em ~50ms!)

    Note over MQ,Audit: Assíncrono — múltiplos consumers
    par Consumer 1
        MQ->>Email: deliver(EmployeeCreatedEvent)
        Email->>Email: Enviar email de boas-vindas
    and Consumer 2
        MQ->>Audit: deliver(EmployeeCreatedEvent)
        Audit->>Audit: Registrar log de auditoria
    end
```

> **A API responde em 50ms** — o envio de email e auditoria acontecem depois, na fila.

---

## 📡 Protocolo AMQP

RabbitMQ usa o protocolo **AMQP** (Advanced Message Queuing Protocol) — um padrão aberto para mensageria.

```mermaid
graph LR
    subgraph "AMQP — Camadas"
        APP["Application Layer<br/>RabbitTemplate, @RabbitListener"]
        AMQP["AMQP Protocol<br/>Channels, Frames, Methods"]
        TCP["TCP/IP<br/>Porta 5672"]
    end

    APP --> AMQP --> TCP

    style APP fill:#6db33f,color:#fff
    style AMQP fill:#ff6600,color:#fff
    style TCP fill:#3498db,color:#fff
```

| Conceito AMQP | O que é | Spring AMQP |
|:---|:---|:---|
| **Connection** | Conexão TCP com o broker | `ConnectionFactory` (gerenciado pelo Spring) |
| **Channel** | Canal multiplexado dentro da conexão | Automático (1 por thread) |
| **Exchange** | Roteador de mensagens | `DirectExchange`, `TopicExchange`, `FanoutExchange` |
| **Queue** | Fila de mensagens | `Queue` bean |
| **Binding** | Regra Exchange → Queue | `BindingBuilder.bind(queue).to(exchange).with(key)` |
| **Message** | Payload + headers + properties | Serializado por `MessageConverter` |

---

## RabbitMQ — Conceitos Fundamentais

```mermaid
graph LR
    P["📤 Producer"] -->|"publish(exchange, routingKey, msg)"| E["🔀 Exchange"]
    E -->|"routing key match"| B["🔗 Binding"]
    B -->|"entrega"| Q["📬 Queue"]
    Q -->|"consume(msg)"| C["📥 Consumer"]

    style E fill:#ff6600,color:#fff
    style Q fill:#3498db,color:#fff
    style B fill:#9b59b6,color:#fff
```

| Componente | O que é | Analogia |
|-----------|---------|----------|
| **Producer** | Quem envia a mensagem | Remetente de uma carta |
| **Exchange** | Roteador de mensagens | Agência dos Correios |
| **Binding** | Regra de roteamento (Exchange → Queue) | Rota de entrega |
| **Queue** | Fila onde mensagens ficam armazenadas | Caixa de correio |
| **Consumer** | Quem processa a mensagem | Destinatário |
| **Routing Key** | "Endereço" da mensagem | CEP / endereço |
| **Message** | Dados serializados (JSON) | A carta em si |

---

## Tipos de Exchange — Detalhado

### 1. Direct Exchange (usado no nosso projeto)

```mermaid
graph TD
    P["Producer"] -->|"routing_key=<br/>employee.created"| DE["Direct Exchange<br/>(employee-events)"]
    DE -->|"🔑 employee.created"| Q1["Queue A<br/>(employee-notifications)<br/>Binding: employee.created ✅"]
    DE -->|"🔑 order.created"| Q2["Queue B<br/>(order-notifications)<br/>Binding: order.created"]
    DE -.->|"employee.created<br/>≠ order.created"| Q2

    style DE fill:#ff6600,color:#fff
    style Q1 fill:#2ecc71,color:#fff
```

> Entregue à queue com routing key **exata**. Perfeito para eventos específicos.

### 2. Topic Exchange

```mermaid
graph TD
    P["Producer"] -->|"routing_key=<br/>employee.created.engineering"| TE["Topic Exchange"]
    TE -->|"Padrão: employee.#"| Q1["Queue A<br/>(todas de employee) ✅"]
    TE -->|"Padrão: *.created.*"| Q2["Queue B<br/>(todos os created) ✅"]
    TE -->|"Padrão: order.#"| Q3["Queue C<br/>(todas de order) ❌"]

    style TE fill:#ff6600,color:#fff
    style Q1 fill:#2ecc71,color:#fff
    style Q2 fill:#2ecc71,color:#fff
    style Q3 fill:#95a5a6,color:#fff
```

> `*` = uma palavra, `#` = zero ou mais palavras. Para categorias e hierarquias.

### 3. Fanout Exchange

```mermaid
graph TD
    P["Producer"] -->|"qualquer routing_key"| FE["Fanout Exchange<br/>(broadcast)"]
    FE --> Q1["Queue A ✅"]
    FE --> Q2["Queue B ✅"]
    FE --> Q3["Queue C ✅"]

    style FE fill:#ff6600,color:#fff
    style Q1 fill:#2ecc71,color:#fff
    style Q2 fill:#2ecc71,color:#fff
    style Q3 fill:#2ecc71,color:#fff
```

> Ignora routing key — envia para **todas** as queues conectadas. Para broadcast.

### Resumo dos Tipos

| Tipo | Comportamento | Quando usar | Exemplo |
|------|-------------|-------------|---------|
| **Direct** | Routing key **exata** | Eventos específicos | `employee.created` |
| **Topic** | Padrão com `*` e `#` | Eventos categorizados | `employee.*.engineering` |
| **Fanout** | **Todas** as queues | Broadcast | Logs, notificações globais |
| **Headers** | Match por headers | Casos especiais | Roteamento complexo |

> **Para nosso caso**: usaremos **Direct Exchange** — cada evento vai para uma fila específica.

---

## 🔄 Ciclo de Vida da Mensagem

```mermaid
stateDiagram-v2
    [*] --> Publicada: Producer.convertAndSend()
    Publicada --> NaFila: Exchange roteia para Queue
    NaFila --> EmProcessamento: Consumer recebe
    EmProcessamento --> Acknowledged: Processada com sucesso
    EmProcessamento --> Rejected: Erro no processamento

    Acknowledged --> [*]: Removida da fila

    Rejected --> NaFila: nack + requeue
    Rejected --> DeadLetter: nack + no requeue
    DeadLetter --> [*]: Mensagem em DLQ para análise

    note right of NaFila
        Mensagem persiste na fila
        (durable = true)
        Sobrevive restart do RabbitMQ
    end note
```

### Garantias de Entrega

| Garantia | Como funciona | Config |
|----------|:---|:---|
| **At-most-once** | Pode perder mensagem | `autoAck = true` (padrão) |
| **At-least-once** | Pode processar 2x | `autoAck = false` + manual ack |
| **Exactly-once** | Nunca perde, nunca duplica | Idempotência no consumer |

> **Spring AMQP usa `autoAck = false` por padrão** — o consumer precisa confirmar (ack) que processou. Se não ack, a mensagem volta para a fila.

---

## RabbitMQ vs. Kafka

| Aspecto | RabbitMQ | Kafka |
|---------|----------|-------|
| **Modelo** | Filas (mensagem consumida = removida) | Log (mensagens retidas por tempo) |
| **Caso de uso** | Notificações, tarefas assíncronas | Event streaming, analytics, logs |
| **Garantia de ordem** | Por fila | Por partição |
| **Reprocessamento** | ❌ Mensagem consumida some | ✅ Replay from offset |
| **Complexidade** | 🟢 Simples (broker inteligente) | 🟠 Mais complexo (consumer inteligente) |
| **Throughput** | Milhares/seg | Milhões/seg |
| **Quando escolher** | Microsserviços, filas de trabalho | Big data, event sourcing, CQRS |
| **Spring Integration** | `spring-boot-starter-amqp` | `spring-kafka` |

> **Para microsserviços típicos**: RabbitMQ é mais simples e resolve 90% dos casos.

---

## 🎯 Resumo — Por que RabbitMQ?

- ✅ **Desacoplamento**: producer não depende do consumer
- ✅ **Resiliência**: mensagem persiste na fila se o consumer cair
- ✅ **Performance**: API responde rápido, processamento pesado vai para fila
- ✅ **Escalabilidade**: múltiplos consumers podem processar a mesma fila
- ✅ **Flexibilidade**: Exchange types permitem roteamento inteligente
- ✅ **Observabilidade**: Management UI mostra filas, mensagens, taxas

> **No próximo slide**: código Java com Producer e Consumer usando Spring AMQP.
