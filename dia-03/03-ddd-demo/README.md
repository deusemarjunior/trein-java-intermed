# 03 - DDD Demo

Demonstração prática dos conceitos táticos de Domain-Driven Design (DDD).

## 📦 Conceitos Implementados

### Entities (Entidades)
- **Order** - Pedido com identidade única
- **Customer** - Cliente com histórico

### Value Objects
- **Money** - Valor monetário imutável
- **Email** - Email validado
- **Address** - Endereço completo

### Aggregates
- **Order Aggregate** - Order como raiz com OrderItems

### Repositories
- **OrderRepository** - Interface do domínio
- Implementações podem variar (memória, MySQL, MongoDB)

### Domain Services
- **OrderPricingService** - Cálculo de preços
- Lógica que não pertence a uma entidade específica

## 🚀 Executar

```bash
mvn clean install && mvn spring-boot:run
```

## 📖 Estrutura DDD

```
domain/
├── entity/           # Entidades com identidade
│   ├── Order.java
│   └── Customer.java
├── valueobject/      # Objetos de valor imutáveis
│   ├── Money.java
│   ├── Email.java
│   └── Address.java
├── aggregate/        # Agregados e raízes
│   ├── Order.java    # Aggregate Root
│   └── OrderItem.java
├── repository/       # Interfaces do domínio
│   └── OrderRepository.java
└── service/          # Serviços de domínio
    └── OrderPricingService.java
```
