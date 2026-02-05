# Dia 9 - Observabilidade e Produção

**Duração**: 5 horas  
**Objetivo**: Implementar observabilidade completa e preparar aplicação para produção

## 📋 Conteúdo Programático

### Manhã (3 horas)

#### 1. Logs Estruturados (1h)

**Configuração Logback**
```xml
<!-- src/main/resources/logback-spring.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    
    <springProperty scope="context" name="springAppName" source="spring.application.name"/>
    
    <!-- Console Appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"app":"${springAppName}"}</customFields>
            <includeMdcKeyName>traceId</includeMdcKeyName>
            <includeMdcKeyName>spanId</includeMdcKeyName>
            <includeMdcKeyName>userId</includeMdcKeyName>
        </encoder>
    </appender>
    
    <!-- File Appender -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application-%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
    
    <logger name="com.example" level="DEBUG"/>
    <logger name="org.springframework.web" level="INFO"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>
    <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="TRACE"/>
    
</configuration>
```

**Logs estruturados no código**
```java
@Slf4j
@Service
public class OrderService {
    
    public OrderId createOrder(CreateOrderCommand command) {
        // Structured logging
        log.info("Creating order for customer: {}", command.customerId());
        
        try {
            Order order = processOrder(command);
            
            log.info("Order created successfully",
                kv("orderId", order.getId()),
                kv("customerId", command.customerId()),
                kv("totalAmount", order.getTotal()),
                kv("itemsCount", order.getItems().size()));
            
            return order.getId();
            
        } catch (Exception e) {
            log.error("Failed to create order",
                kv("customerId", command.customerId()),
                kv("errorType", e.getClass().getSimpleName()),
                kv("errorMessage", e.getMessage()),
                e);
            throw e;
        }
    }
    
    // Helper method for structured logging
    private static Marker kv(String key, Object value) {
        return append(key, value);
    }
}
```

**MDC (Mapped Diagnostic Context)**
```java
@Component
public class LoggingFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Add context to all logs
            MDC.put("requestId", UUID.randomUUID().toString());
            MDC.put("userId", getUserId(request));
            MDC.put("clientIp", request.getRemoteAddr());
            MDC.put("method", request.getMethod());
            MDC.put("uri", request.getRequestURI());
            
            filterChain.doFilter(request, response);
            
        } finally {
            MDC.clear();
        }
    }
}
```

#### 2. Métricas com Micrometer e Actuator (1h)

**Configuração Spring Boot Actuator**
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: always
      probes:
        enabled: true
    metrics:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      environment: ${spring.profiles.active}
    distribution:
      percentiles-histogram:
        http.server.requests: true
  health:
    livenessState:
      enabled: true
    readinessState:
      enabled: true

info:
  app:
    name: '@project.name@'
    version: '@project.version@'
    description: '@project.description@'
```

**Custom Metrics**
```java
@Service
public class OrderMetricsService {
    
    private final MeterRegistry meterRegistry;
    private final Counter orderCreatedCounter;
    private final Timer orderProcessingTimer;
    private final Gauge activeOrdersGauge;
    private final AtomicInteger activeOrders = new AtomicInteger(0);
    
    public OrderMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Counter
        this.orderCreatedCounter = Counter.builder("orders.created")
            .description("Total number of orders created")
            .tag("type", "ecommerce")
            .register(meterRegistry);
        
        // Timer
        this.orderProcessingTimer = Timer.builder("orders.processing.time")
            .description("Time taken to process orders")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);
        
        // Gauge
        this.activeOrdersGauge = Gauge.builder("orders.active", activeOrders, AtomicInteger::get)
            .description("Number of active orders")
            .register(meterRegistry);
    }
    
    public void recordOrderCreated(OrderStatus status) {
        orderCreatedCounter.increment();
        
        meterRegistry.counter("orders.by.status",
            "status", status.name()).increment();
    }
    
    public void recordOrderProcessingTime(Runnable task) {
        orderProcessingTimer.record(task);
    }
    
    public void incrementActiveOrders() {
        activeOrders.incrementAndGet();
    }
    
    public void decrementActiveOrders() {
        activeOrders.decrementAndGet();
    }
}

@Aspect
@Component
public class MetricsAspect {
    
    private final MeterRegistry meterRegistry;
    
    @Around("@annotation(Timed)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Object result = joinPoint.proceed();
            sample.stop(Timer.builder("method.execution")
                .tag("class", joinPoint.getTarget().getClass().getSimpleName())
                .tag("method", joinPoint.getSignature().getName())
                .register(meterRegistry));
            return result;
        } catch (Exception e) {
            sample.stop(Timer.builder("method.execution")
                .tag("class", joinPoint.getTarget().getClass().getSimpleName())
                .tag("method", joinPoint.getSignature().getName())
                .tag("exception", e.getClass().getSimpleName())
                .register(meterRegistry));
            throw e;
        }
    }
}
```

**Health Indicators customizados**
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private final DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("validationTime", "2s")
                    .build();
            }
        } catch (SQLException e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
        
        return Health.down().build();
    }
}

@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {
    
    private final PaymentClient paymentClient;
    
    @Override
    public Health health() {
        try {
            HealthResponse response = paymentClient.checkHealth();
            
            return Health.up()
                .withDetail("service", "payment-gateway")
                .withDetail("status", response.getStatus())
                .withDetail("latency", response.getLatency())
                .build();
                
        } catch (Exception e) {
            return Health.down()
                .withDetail("service", "payment-gateway")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

#### 3. Tracing Distribuído (1h)

**OpenTelemetry configuração**
```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0 # 100% em dev, 0.1 (10%) em prod
  otlp:
    tracing:
      endpoint: http://localhost:4318/v1/traces
```

**Configuração programática**
```java
@Configuration
public class ObservabilityConfig {
    
    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
            .merge(Resource.create(Attributes.of(
                ResourceAttributes.SERVICE_NAME, "ecommerce-api",
                ResourceAttributes.SERVICE_VERSION, "1.0.0"
            )));
        
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(
                OtlpGrpcSpanExporter.builder().build()
            ).build())
            .setResource(resource)
            .build();
        
        return OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .buildAndRegisterGlobal();
    }
}
```

**Custom Spans**
```java
@Service
public class OrderService {
    
    private final Tracer tracer;
    
    public OrderId createOrder(CreateOrderCommand command) {
        Span span = tracer.spanBuilder("createOrder")
            .setAttribute("order.customerId", command.customerId())
            .setAttribute("order.itemsCount", command.items().size())
            .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            
            // Child span
            Span validationSpan = tracer.spanBuilder("validateOrder")
                .startSpan();
            try {
                validateOrder(command);
                validationSpan.setStatus(StatusCode.OK);
            } finally {
                validationSpan.end();
            }
            
            // Another child span
            Span paymentSpan = tracer.spanBuilder("processPayment")
                .setAttribute("payment.amount", command.total())
                .startSpan();
            try {
                processPayment(command);
                paymentSpan.setStatus(StatusCode.OK);
            } catch (PaymentException e) {
                paymentSpan.setStatus(StatusCode.ERROR, e.getMessage());
                paymentSpan.recordException(e);
                throw e;
            } finally {
                paymentSpan.end();
            }
            
            Order order = saveOrder(command);
            span.setAttribute("order.id", order.getId().toString());
            span.setStatus(StatusCode.OK);
            
            return order.getId();
            
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

### Tarde (2 horas)

#### 4. Feature Flags/Toggles (45min)

**Implementação simples**
```java
@Configuration
@ConfigurationProperties(prefix = "features")
public class FeatureFlags {
    
    private boolean newPaymentFlow;
    private boolean enhancedSearch;
    private boolean aiRecommendations;
    
    // Getters and setters
}

@Service
public class OrderService {
    
    private final FeatureFlags features;
    
    public void processOrder(Order order) {
        if (features.isNewPaymentFlow()) {
            processPaymentV2(order);
        } else {
            processPaymentV1(order);
        }
    }
}
```

**Feature flags avançado (Togglz)**
```java
@Configuration
public class FeatureConfig implements TogglzConfig {
    
    @Override
    public Class<? extends Feature> getFeatureClass() {
        return AppFeatures.class;
    }
    
    @Override
    public StateRepository getStateRepository() {
        return new FileBasedStateRepository(new File("/tmp/features.properties"));
    }
}

public enum AppFeatures implements Feature {
    
    @Label("New Payment Flow")
    @DefaultActivationStrategy(id = GradualActivationStrategy.ID, parameters = {
        @ActivationParameter(name = GradualActivationStrategy.PARAM_PERCENTAGE, value = "50")
    })
    NEW_PAYMENT_FLOW,
    
    @Label("AI Recommendations")
    @EnabledByDefault
    AI_RECOMMENDATIONS,
    
    @Label("Enhanced Search")
    ENHANCED_SEARCH;
    
    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}

@RestController
public class FeatureFlagsController {
    
    @GetMapping("/api/admin/features")
    public List<FeatureInfo> listFeatures() {
        return Arrays.stream(AppFeatures.values())
            .map(feature -> new FeatureInfo(
                feature.name(),
                feature.isActive()
            ))
            .toList();
    }
    
    @PostMapping("/api/admin/features/{feature}/toggle")
    public void toggleFeature(@PathVariable String feature) {
        Feature f = AppFeatures.valueOf(feature);
        FeatureContext.getFeatureManager().setFeatureState(
            new FeatureState(f, !f.isActive())
        );
    }
}
```

#### 5. Monitoramento com Grafana/Elastic (45min)

**Prometheus + Grafana**
```yaml
# docker-compose.yml (adicionar)
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus-data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
    networks:
      - app-network

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana-data:/var/lib/grafana
      - ./grafana/dashboards:/etc/grafana/provisioning/dashboards
      - ./grafana/datasources:/etc/grafana/provisioning/datasources
    depends_on:
      - prometheus
    networks:
      - app-network
```

```yaml
# prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app:8080']
        labels:
          application: 'ecommerce-api'
          environment: 'dev'
```

**ELK Stack (Elasticsearch + Logstash + Kibana)**
```yaml
# docker-compose.yml
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
    volumes:
      - elastic-data:/usr/share/elasticsearch/data
    networks:
      - app-network

  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    ports:
      - "5601:5601"
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    depends_on:
      - elasticsearch
    networks:
      - app-network

  logstash:
    image: docker.elastic.co/logstash/logstash:8.11.0
    ports:
      - "5000:5000"
    volumes:
      - ./logstash/pipeline:/usr/share/logstash/pipeline
    depends_on:
      - elasticsearch
    networks:
      - app-network
```

#### 6. Boas Práticas de Produção (30min)

**Configurações de produção**
```yaml
# application-prod.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      leak-detection-threshold: 60000
  
  jpa:
    show-sql: false
    properties:
      hibernate:
        generate_statistics: false
        jdbc:
          batch_size: 20

server:
  tomcat:
    threads:
      max: 200
      min-spare: 10
    connection-timeout: 20000
    accept-count: 100
  
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
  
  shutdown: graceful

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    root: INFO
    com.example: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

**Graceful shutdown**
```java
@Component
public class GracefulShutdown implements ApplicationListener<ContextClosedEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(GracefulShutdown.class);
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("Received shutdown signal, starting graceful shutdown...");
        
        // Finish processing current requests
        try {
            Thread.sleep(30000); // 30 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("Graceful shutdown completed");
    }
}
```

## 💻 Exercícios Práticos

### Exercício 1: Observabilidade Completa (2h)
Implementar na aplicação:
- Logs estruturados
- Métricas customizadas
- Tracing distribuído
- Health checks

### Exercício 2: Dashboard (1h)
Criar dashboard Grafana com:
- Taxa de requisições
- Latência (p50, p95, p99)
- Taxa de erro
- Métricas de negócio

## 📚 Material de Estudo

### Leitura Obrigatória
- [Observability Engineering](https://www.oreilly.com/library/view/observability-engineering/9781492076438/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [OpenTelemetry](https://opentelemetry.io/docs/instrumentation/java/)

## 🎯 Objetivos de Aprendizagem

- ✅ Implementar observabilidade completa
- ✅ Criar dashboards de monitoramento
- ✅ Configurar alertas
- ✅ Preparar aplicação para produção

## 🏆 Projeto Final

**Sistema E-commerce Completo** com:
- ✅ APIs RESTful documentadas
- ✅ Arquitetura Hexagonal
- ✅ Testes (unitários, integração, BDD)
- ✅ Persistência SQL + NoSQL + Cache
- ✅ CI/CD pipeline
- ✅ Containerização
- ✅ Observabilidade completa

## 📝 Avaliação Final

- Código no GitHub
- README completo
- Pipelines funcionando
- Deploy em Azure
- Apresentação (15 min)

## 🎓 Certificação

Após conclusão e aprovação:
- Certificado de conclusão
- Badge digital
- Portfolio profissional

---

**Parabéns por concluir o treinamento!** 🎉
