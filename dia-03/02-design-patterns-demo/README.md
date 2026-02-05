# 02 - Design Patterns Demo

Demonstração prática dos principais Design Patterns utilizados em aplicações Spring Boot.

## 🎨 Patterns Implementados

### 1. Strategy Pattern (Comportamental)
**Múltiplas estratégias de desconto**
- VIP Discount (20%)
- Regular Discount (10%)
- Premium Discount (30%)
- Black Friday Discount (50%)

### 2. Factory Pattern (Criacional)
**Sistema de Notificações**
- Email Notification
- SMS Notification
- Push Notification
- WhatsApp Notification

### 3. Builder Pattern (Criacional)
**Construção de objetos complexos**
- User Builder
- Order Builder
- Product Builder

### 4. Singleton Pattern (Criacional)
**Gerenciamento de configuração**
- Configuration Manager
- Database Connection Pool
- Logger

## 🚀 Como Executar

```bash
mvn clean install
mvn spring-boot:run
```

## 📖 Estrutura

```
src/main/java/com/example/patterns/
├── DesignPatternsDemoApplication.java
├── strategy/
│   ├── DiscountStrategy.java
│   ├── VipDiscountStrategy.java
│   ├── RegularDiscountStrategy.java
│   └── DiscountService.java
├── factory/
│   ├── Notification.java
│   ├── EmailNotification.java
│   ├── SmsNotification.java
│   └── NotificationFactory.java
├── builder/
│   ├── User.java
│   ├── Order.java
│   └── Product.java
└── singleton/
    ├── ConfigurationManager.java
    └── DatabaseConnectionPool.java
```

## 💡 Quando Usar Cada Pattern

### Strategy Pattern
- ✅ Múltiplas formas de fazer a mesma coisa
- ✅ Algoritmos intercambiáveis em runtime
- ✅ Evitar if/else ou switch grandes

### Factory Pattern
- ✅ Criação complexa de objetos
- ✅ Lógica de decisão centralizada
- ✅ Desacoplar criação do uso

### Builder Pattern
- ✅ Objetos com muitos parâmetros
- ✅ Parâmetros opcionais
- ✅ Construção fluente e legível

### Singleton Pattern
- ✅ Apenas uma instância no sistema
- ✅ Recursos compartilhados
- ✅ Spring já implementa (@Component, @Service)
