# 01 - SOLID Principles Demo

Demonstração prática dos 5 Princípios SOLID com exemplos de código "antes" e "depois".

## 📚 Princípios SOLID

### S - Single Responsibility Principle (SRP)
**Uma classe deve ter apenas uma razão para mudar**

- ❌ `before.srp` - UserService com múltiplas responsabilidades
- ✅ `after.srp` - Responsabilidades separadas em classes distintas

### O - Open/Closed Principle (OCP)
**Aberto para extensão, fechado para modificação**

- ❌ `before.ocp` - DiscountService com if/else
- ✅ `after.ocp` - Strategy Pattern para descontos

### L - Liskov Substitution Principle (LSP)
**Objetos devem ser substituíveis por suas subclasses**

- ❌ `before.lsp` - Square quebra contrato de Rectangle
- ✅ `after.lsp` - Interface Shape com implementações corretas

### I - Interface Segregation Principle (ISP)
**Interfaces específicas ao invés de interfaces gerais**

- ❌ `before.isp` - Interface Worker com muitos métodos
- ✅ `after.isp` - Interfaces segregadas por responsabilidade

### D - Dependency Inversion Principle (DIP)
**Depender de abstrações, não de implementações**

- ❌ `before.dip` - OrderService acoplado ao MySQL
- ✅ `after.dip` - OrderService depende de interface

## 🚀 Como Executar

```bash
# Compilar
mvn clean install

# Executar
mvn spring-boot:run

# Testar
mvn test
```

## 📖 Estrutura

```
src/main/java/com/example/solid/
├── SolidDemoApplication.java
├── srp/
│   ├── before/     # ❌ Violação do SRP
│   └── after/      # ✅ SRP aplicado
├── ocp/
│   ├── before/     # ❌ Violação do OCP
│   └── after/      # ✅ OCP aplicado
├── lsp/
│   ├── before/     # ❌ Violação do LSP
│   └── after/      # ✅ LSP aplicado
├── isp/
│   ├── before/     # ❌ Violação do ISP
│   └── after/      # ✅ ISP aplicado
└── dip/
    ├── before/     # ❌ Violação do DIP
    └── after/      # ✅ DIP aplicado
```

## 💡 O que você vai aprender

- ✅ Identificar violações dos princípios SOLID
- ✅ Refatorar código aplicando cada princípio
- ✅ Entender o impacto de cada princípio na manutenibilidade
- ✅ Aplicar SOLID no dia a dia com Spring Boot
