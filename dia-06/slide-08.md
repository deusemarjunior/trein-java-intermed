# Slide 8: Mensageria com RabbitMQ — Producer e Consumer

**Horário:** 13:10 - 13:20

---

## Visão Geral — O que vamos configurar

```mermaid
graph TD
    subgraph "Spring Boot Application"
        CONFIG["🔧 RabbitMQConfig<br/>(Beans: Exchange, Queue, Binding)"]
        PUB["📤 EmployeeEventPublisher<br/>(RabbitTemplate)"]
        CON["📥 EmployeeNotificationConsumer<br/>(@RabbitListener)"]
        SVC["📋 EmployeeService<br/>(chama o publisher)"]
        DTO["📦 EmployeeCreatedEvent<br/>(Record)"]
    end

    subgraph "RabbitMQ Broker"
        EX["Exchange<br/>employee-events"]
        QU["Queue<br/>employee-notifications"]
    end

    CONFIG -->|"declara"| EX
    CONFIG -->|"declara"| QU
    SVC -->|"chama"| PUB
    PUB -->|"convertAndSend()"| EX
    EX -->|"routing_key"| QU
    QU -->|"deliver"| CON
    PUB -.->|"serializa"| DTO
    CON -.->|"deserializa"| DTO

    style CONFIG fill:#9b59b6,color:#fff
    style EX fill:#ff6600,color:#fff
    style QU fill:#3498db,color:#fff
```

---

## 1. Configuração do Spring AMQP

### Dependência (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

> Inclui: `spring-amqp`, `spring-rabbit`, `amqp-client` (driver Java do RabbitMQ).

### application.yml

```yaml
spring:
  rabbitmq:
    host: localhost       # hostname do container RabbitMQ
    port: 5672            # porta AMQP (não confundir com 15672 = Management UI)
    username: guest       # usuário padrão
    password: guest       # senha padrão
```

### O que cada porta faz

| Porta | Protocolo | Uso |
|:---:|:---|:---|
| **5672** | AMQP | Comunicação producer/consumer (protocolo binário) |
| **15672** | HTTP | Management UI (browser) — monitorar filas, exchanges |
| **25672** | Erlang Distribution | Clustering entre nodes RabbitMQ (não usamos) |

---

## 2. Configuração das Filas (RabbitMQConfig)

```java
@Configuration
public class RabbitMQConfig {

    // Constantes para reuso entre Producer e Consumer
    public static final String EXCHANGE_NAME = "employee-events";
    public static final String QUEUE_NAME = "employee-notifications";
    public static final String ROUTING_KEY = "employee.created";

    // 1. Fila onde mensagens serão armazenadas
    @Bean
    public Queue employeeNotificationsQueue() {
        return new Queue(QUEUE_NAME, true); // durable = true (sobrevive restart)
    }

    // 2. Exchange que roteia mensagens
    @Bean
    public DirectExchange employeeExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // 3. Binding = regra: Exchange + RoutingKey → Queue
    @Bean
    public Binding binding(Queue employeeNotificationsQueue,
                           DirectExchange employeeExchange) {
        return BindingBuilder
                .bind(employeeNotificationsQueue)       // qual fila
                .to(employeeExchange)                   // qual exchange
                .with(ROUTING_KEY);                     // com qual routing key
    }

    // 4. Conversor de mensagem: Java Object → JSON (e vice-versa)
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 5. RabbitTemplate com conversor JSON configurado
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
```

### Como os Beans se conectam

```mermaid
graph TD
    subgraph "Declaração no RabbitMQ"
        EX["DirectExchange<br/>employee-events"]
        QU["Queue<br/>employee-notifications<br/>(durable=true)"]
        BIND["Binding<br/>employee.created"]
    end

    subgraph "Serialização"
        CONV["Jackson2JsonMessageConverter<br/>Object ↔ JSON"]
        RT["RabbitTemplate<br/>(com converter)"]
    end

    EX --> BIND
    QU --> BIND
    CONV --> RT

    style EX fill:#ff6600,color:#fff
    style QU fill:#3498db,color:#fff
    style BIND fill:#9b59b6,color:#fff
    style RT fill:#2ecc71,color:#fff
```

### Parâmetro `durable`

| durable | Comportamento | Quando usar |
|:---:|:---|:---|
| `true` | Queue e mensagens sobrevivem ao restart do RabbitMQ | ✅ Produção — sempre |
| `false` | Queue e mensagens são perdidas ao restart | ❌ Nunca em produção |

---

## 3. O Evento (DTO da Mensagem)

```java
public record EmployeeCreatedEvent(
    Long employeeId,
    String name,
    String email,
    String departmentName
) {}
```

### Serialização — O que acontece internamente

```mermaid
sequenceDiagram
    participant Publisher
    participant Converter as Jackson2JsonMessageConverter
    participant MQ as RabbitMQ

    Publisher->>Converter: convertAndSend(event)
    Note over Converter: Serializa Record → JSON
    Converter->>Converter: {"employeeId":42,"name":"Ana",<br/>"email":"ana@email.com",<br/>"departmentName":"Engineering"}
    Converter->>MQ: Message(body=JSON, headers={content_type: application/json, __TypeId__: ...})
```

> **Records** são perfeitos para eventos:
> - ✅ **Imutáveis** — ninguém altera os dados do evento
> - ✅ **Serializáveis** — Jackson serializa/deserializa automaticamente
> - ✅ **Autodocumentados** — nome dos campos = documentação

### ⚠️ Nunca envie a Entity JPA como mensagem!

```mermaid
graph LR
    subgraph "❌ Errado"
        E1["Entity Employee<br/>(com proxy Hibernate,<br/>lazy collections,<br/>referências circulares)"]
    end

    subgraph "✅ Correto"
        E2["Record EmployeeCreatedEvent<br/>(só dados necessários,<br/>imutável, sem proxy)"]
    end

    style E1 fill:#e74c3c,color:#fff
    style E2 fill:#2ecc71,color:#fff
```

| Problema de enviar Entity | Consequência |
|:---|:---|
| Lazy collections não inicializadas | `LazyInitializationException` ao serializar |
| Referências circulares | `StackOverflowError` ou JSON infinito |
| Dados desnecessários | Mensagem pesada, lenta |
| Acoplamento com schema do DB | Consumer precisa conhecer a Entity |

---

## 4. Producer — Publicando Mensagens

```java
@Service
@Slf4j
public class EmployeeEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public EmployeeEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEmployeeCreated(Employee employee) {
        // 1. Converter Entity → Event (DTO leve)
        var event = new EmployeeCreatedEvent(
            employee.getId(),
            employee.getName(),
            employee.getEmail(),
            employee.getDepartment().getName()
        );

        // 2. Publicar: exchange + routing key + mensagem
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,    // exchange = "employee-events"
            RabbitMQConfig.ROUTING_KEY,      // routing key = "employee.created"
            event                            // mensagem (serializada para JSON)
        );

        log.info("📤 Evento publicado: EmployeeCreatedEvent (id={}, name={})",
                employee.getId(), employee.getName());
    }
}
```

### Anatomia do `convertAndSend()`

```mermaid
sequenceDiagram
    participant Code as EmployeeEventPublisher
    participant RT as RabbitTemplate
    participant Conv as MessageConverter
    participant CH as AMQP Channel
    participant EX as Exchange (employee-events)

    Code->>RT: convertAndSend(exchange, routingKey, event)
    RT->>Conv: toMessage(event)
    Conv-->>RT: Message(JSON bytes + headers)
    RT->>CH: basicPublish(exchange, routingKey, message)
    CH->>EX: Entrega ao Exchange
    Note over EX: Exchange roteia pela routingKey<br/>para a Queue correta
```

### Uso no Service

```java
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeEventPublisher eventPublisher;

    public EmployeeResponse create(EmployeeRequest request) {
        // 1. Salvar no banco
        Employee employee = employeeRepository.save(toEntity(request));

        // 2. Publicar evento (assíncrono — não bloqueia a resposta)
        eventPublisher.publishEmployeeCreated(employee);

        // 3. Retornar resposta imediatamente
        return toResponse(employee);
    }
}
```

> ⚠️ **Atenção**: `convertAndSend()` é síncrono até o broker confirmar o recebimento, mas o **processamento** pelo consumer é assíncrono.

---

## 5. Consumer — Consumindo Mensagens

```java
@Component
@Slf4j
public class EmployeeNotificationConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("📧 Enviando email de boas-vindas para {} ({})",
                event.name(), event.email());

        // Aqui você faria:
        // - Enviar email real via SendGrid/SES
        // - Criar notificação in-app
        // - Atualizar sistema de RH
        // - Disparar workflow de onboarding

        log.info("✅ Notificação processada para funcionário id={}",
                event.employeeId());
    }
}
```

### Como `@RabbitListener` funciona internamente

```mermaid
sequenceDiagram
    participant MQ as RabbitMQ
    participant Container as SimpleMessageListenerContainer
    participant Conv as MessageConverter
    participant Handler as handleEmployeeCreated()

    Note over Container: Spring cria um Container<br/>que faz polling na Queue
    MQ->>Container: basicDeliver(message)
    Container->>Conv: fromMessage(JSON bytes)
    Conv-->>Container: EmployeeCreatedEvent object
    Container->>Handler: handleEmployeeCreated(event)

    alt Sucesso
        Handler-->>Container: return (sem exceção)
        Container->>MQ: basicAck(deliveryTag) ✅
        Note over MQ: Mensagem removida da fila
    else Exceção
        Handler-->>Container: throw Exception
        Container->>MQ: basicNack(deliveryTag, requeue=true) ❌
        Note over MQ: Mensagem volta para a fila<br/>(retry automático)
    end
```

### Opções do `@RabbitListener`

| Parâmetro | Uso | Exemplo |
|:---|:---|:---|
| `queues` | Nome da fila | `@RabbitListener(queues = "employee-notifications")` |
| `concurrency` | Consumers paralelos | `@RabbitListener(concurrency = "3-10")` |
| `ackMode` | Modo de acknowledgment | `@RabbitListener(ackMode = "MANUAL")` |
| `containerFactory` | Container customizado | Para configurações avançadas |

---

## 6. Fluxo Completo — End to End

```mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant Service
    participant Repo as Repository
    participant Pub as EventPublisher
    participant RT as RabbitTemplate
    participant EX as Exchange
    participant QU as Queue
    participant Consumer

    Client->>Controller: POST /api/employees
    Controller->>Service: create(request)
    Service->>Repo: save(employee)
    Repo-->>Service: employee (id=42)

    Service->>Pub: publishEmployeeCreated(employee)
    Pub->>Pub: Entity → Record (criar DTO)
    Pub->>RT: convertAndSend(exchange, key, event)
    RT->>EX: publish(JSON message)
    EX->>QU: route by "employee.created"

    Pub-->>Service: void (retorna)
    Service-->>Controller: EmployeeResponse
    Controller-->>Client: 201 Created ✅

    Note over QU,Consumer: Assíncrono — Thread separada
    QU->>Consumer: deliver(EmployeeCreatedEvent)
    Consumer->>Consumer: log("📧 Enviando email...")
    Consumer->>QU: ack ✅
```

---

## 7. Verificando no RabbitMQ Management UI

Após publicar uma mensagem:

1. Abra **http://localhost:15672** (guest/guest)
2. Vá em **Queues** → `employee-notifications`
3. Veja:
   - **Messages Ready**: mensagens aguardando consumo
   - **Messages Unacked**: mensagens sendo processadas
   - **Message rates**: taxa de publicação/consumo

```mermaid
graph LR
    subgraph "Management UI — O que observar"
        OVER["Overview<br/>Total connections,<br/>channels, exchanges,<br/>queues"]
        CONN["Connections<br/>Cada aplicação Spring<br/>= 1 connection"]
        EXCH["Exchanges<br/>Verificar employee-events<br/>existe com tipo Direct"]
        QUES["Queues<br/>employee-notifications<br/>Ready / Unacked / Total"]
    end

    OVER --> CONN --> EXCH --> QUES

    style QUES fill:#3498db,color:#fff
```

> **Se o consumer estiver rodando**: as mensagens são consumidas imediatamente e a fila fica vazia. Se parar o consumer, as mensagens **acumulam na fila** até ele voltar.

---

## 8. Tratamento de Erros

### O que acontece quando o Consumer falha?

```mermaid
stateDiagram-v2
    [*] --> Delivery: RabbitMQ entrega mensagem
    Delivery --> Processing: Consumer recebe
    Processing --> Success: Sem exceção
    Processing --> Failure: Exceção lançada

    Success --> Acked: basicAck()
    Acked --> Removed: Mensagem removida da fila ✅

    Failure --> Retry1: Tentativa 1 (imediata)
    Retry1 --> Retry2: Tentativa 2 (1s delay)
    Retry2 --> Retry3: Tentativa 3 (2s delay)
    Retry3 --> Rejected: Todas as tentativas falharam
    Rejected --> DLQ: Dead Letter Queue (se configurada)
    Rejected --> Dropped: Mensagem descartada ❌

    Removed --> [*]
    DLQ --> [*]
    Dropped --> [*]
```

### Configurando Retry no Spring

```yaml
spring:
  rabbitmq:
    listener:
      simple:
        retry:
          enabled: true           # habilitar retry automático
          initial-interval: 1000  # 1 segundo entre tentativas
          max-attempts: 3         # máximo 3 tentativas
          multiplier: 2.0         # backoff: 1s, 2s, 4s
```

### Idempotência — Regra de Ouro

> Se a mensagem for processada **2 vezes**, o resultado deve ser o **mesmo**.

```java
@RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
public void handleEmployeeCreated(EmployeeCreatedEvent event) {
    // ✅ Idempotente — verificar se já processou
    if (notificationRepository.existsByEmployeeId(event.employeeId())) {
        log.warn("⚠️ Notificação já enviada para employee={}", event.employeeId());
        return;
    }

    // Processar normalmente...
    sendWelcomeEmail(event);
    notificationRepository.save(new Notification(event.employeeId()));
}
```

---

## ⚠️ Boas Práticas — Checklist

| Prática | Por quê | Impacto |
|---------|---------|---------|
| Mensagem = Record/DTO leve | Não envie a Entity JPA inteira (proxy, lazy) | 🔴 Crítico |
| Serialização JSON | Mais interoperável que Java serialization | 🟡 Importante |
| Exchange + Routing Key | Desacopla producer da queue (pode rotear para múltiplas filas) | 🟡 Importante |
| Queue durable = true | Mensagens sobrevivem ao restart do RabbitMQ | 🔴 Crítico |
| Idempotência no consumer | Se mensagem for processada 2x, resultado é o mesmo | 🔴 Crítico |
| Não fazer operações longas | Consumer thread é blocking — manter processamento leve | 🟡 Importante |
| Logging: publicação + consumo | Visibilidade para debugging e monitoramento | 🟢 Bom ter |
| Retry com backoff | Evitar flood de retries em caso de erro temporário | 🟡 Importante |

> **Próximo**: Cache com Redis — acelerando consultas frequentes!
