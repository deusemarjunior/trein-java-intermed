# Projetos do Dia 3 - Arquitetura e Design

Este diretório contém projetos práticos para demonstrar os conceitos de **Princípios SOLID**, **Design Patterns** e **Domain-Driven Design (DDD)**.

## 📦 Projetos Disponíveis

### 1. `01-solid-principles-demo`
**Demonstração dos 5 Princípios SOLID**

Exemplos práticos de cada princípio SOLID com código "antes" e "depois":
- ✅ **SRP** (Single Responsibility Principle)
- ✅ **OCP** (Open/Closed Principle)
- ✅ **LSP** (Liskov Substitution Principle)
- ✅ **ISP** (Interface Segregation Principle)
- ✅ **DIP** (Dependency Inversion Principle)

```bash
cd 01-solid-principles-demo
mvn clean install
mvn spring-boot:run
```

### 2. `02-design-patterns-demo`
**Design Patterns Essenciais**

Implementação dos principais Design Patterns:
- 🎯 **Strategy Pattern** - Estratégias de desconto
- 🏭 **Factory Pattern** - Sistema de notificações
- 🔨 **Builder Pattern** - Construção de objetos complexos
- 1️⃣ **Singleton Pattern** - Gerenciamento de configuração

```bash
cd 02-design-patterns-demo
mvn clean install
mvn spring-boot:run
```

### 3. `03-ddd-demo`
**Domain-Driven Design (DDD)**

Conceitos táticos do DDD:
- 📦 **Entities** - Order, Customer
- 💎 **Value Objects** - Money, Email, Address
- 🎯 **Aggregates** - Order com OrderItems
- 💾 **Repositories** - Interfaces do domínio
- ⚙️ **Domain Services** - Lógica de negócio

```bash
cd 03-ddd-demo
mvn clean install
mvn spring-boot:run
```

### 4. `04-layered-architecture-demo`
**Arquitetura em Camadas**

Exemplo de arquitetura tradicional em 3 camadas:
- 🎨 **Presentation Layer** - Controllers e DTOs
- 💼 **Business Layer** - Services e lógica de negócio
- 💾 **Data Access Layer** - Repositories e Entities

```bash
cd 04-layered-architecture-demo
mvn clean install
mvn spring-boot:run
```

### 5. `05-order-system`
**Exercício Prático - Sistema de Pedidos**

Projeto completo integrando todos os conceitos:
- ✅ Aplicação dos princípios SOLID
- ✅ Uso de Design Patterns
- ✅ Modelagem DDD
- ✅ Arquitetura em camadas

**Objetivo:** Refatorar código problemático aplicando boas práticas

```bash
cd 05-order-system
mvn clean install
mvn spring-boot:run
```

## 🚀 Como Executar os Projetos

### Pré-requisitos
```bash
# Verificar instalações
java -version    # Java 17 ou 21
mvn -version     # Maven 3.8+
```

### Executar um projeto
```bash
# 1. Navegar para o projeto
cd dia-03/<nome-do-projeto>

# 2. Compilar
mvn clean install

# 3. Executar
mvn spring-boot:run

# 4. Testar (opcional)
mvn test
```

## 📚 Estrutura Comum dos Projetos

```
<projeto>/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── domain/          # Modelos de domínio
│   │   │   ├── service/         # Serviços
│   │   │   ├── repository/      # Repositórios
│   │   │   ├── controller/      # REST Controllers
│   │   │   └── config/          # Configurações
│   │   └── resources/
│   │       └── application.yml  # Configuração Spring
│   └── test/
│       └── java/                # Testes unitários
├── pom.xml                      # Dependências Maven
└── README.md                    # Documentação do projeto
```

## 🎯 Objetivos de Aprendizagem

Ao final deste dia, você será capaz de:

✅ Aplicar os 5 princípios SOLID em código real  
✅ Identificar quando usar cada Design Pattern  
✅ Modelar domínios usando conceitos de DDD  
✅ Estruturar aplicações em camadas  
✅ Escrever código mais limpo, testável e manutenível  

## 📖 Material de Referência

- [Slides do Dia 3](./README.md)
- [Princípios SOLID](./slide-03.md)
- [Design Patterns](./slide-09.md)
- [Domain-Driven Design](./slide-14.md)
- [Arquitetura em Camadas](./slide-17.md)

## 💡 Dicas

1. **Comece pelos exemplos básicos** (01-solid-principles-demo)
2. **Teste cada projeto** para entender o funcionamento
3. **Compare código "antes" e "depois"** nas refatorações
4. **Experimente adicionar novos recursos** aplicando os padrões
5. **Use o projeto 05 para praticar** tudo que aprendeu

## 🤝 Exercícios Sugeridos

1. No projeto 02-design-patterns-demo:
   - Adicione uma nova estratégia de desconto
   - Crie um novo tipo de notificação

2. No projeto 03-ddd-demo:
   - Adicione validações no aggregate Order
   - Crie um novo Value Object

3. No projeto 05-order-system:
   - Complete a refatoração aplicando SOLID
   - Implemente os testes unitários

## ❓ Dúvidas?

Consulte os slides ou peça ajuda ao instrutor!
