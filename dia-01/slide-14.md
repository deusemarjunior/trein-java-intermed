# Slide 14: Profiles - Dev vs Prod

**Horário:** 15:00 - 15:15

---

## Múltiplos Ambientes

```
application.yml           # Configurações comuns
application-dev.yml       # Desenvolvimento
application-test.yml      # Testes
application-prod.yml      # Produção
```

---

## Configuração por Ambiente

```yaml
# application.yml (comum)
spring:
  application:
    name: products-api

---
# application-dev.yml (desenvolvimento)
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  h2:
    console:
      enabled: true
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop

logging:
  level:
    root: DEBUG

---
# application-prod.yml (produção)
spring:
  datasource:
    url: jdbc:postgresql://prod-server:5432/products_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate  # NUNCA use create-drop em prod!

logging:
  level:
    root: INFO
```

---

## Ativar profile

```bash
# Opção 1: Linha de comando
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Opção 2: application.yml
spring:
  profiles:
    active: dev

# Opção 3: Variável de ambiente
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run

# Opção 4: IDE (IntelliJ)
Run → Edit Configurations → Active Profiles: dev
```

---

## Beans específicos por profile

```java
@Configuration
public class AppConfig {
    
    @Bean
    @Profile("dev")
    public CommandLineRunner loadData(ProductRepository repo) {
        return args -> {
            // Carregar dados de teste apenas em dev
            repo.save(new Product("Laptop", "Test", BigDecimal.valueOf(1000), "Electronics"));
            repo.save(new Product("Mouse", "Test", BigDecimal.valueOf(50), "Electronics"));
            System.out.println("✅ Test data loaded!");
        };
    }
    
    @Bean
    @Profile("prod")
    public SomeService prodService() {
        return new ProductionService();
    }
}
```
