# 05 - Order System (Exercício Prático)

**🎯 Exercício de Refatoração - Aplicando SOLID + Design Patterns + DDD**

## Objetivo

Refatorar um sistema de pedidos problemático aplicando os conceitos aprendidos:
- ✅ Princípios SOLID
- ✅ Design Patterns (Strategy, Factory, Builder)
- ✅ Domain-Driven Design
- ✅ Arquitetura em Camadas

## 📝 Tarefa

### Código Problemático (fornecido)

O sistema atual tem múltiplos problemas:
- ❌ Violações de SRP, OCP, DIP
- ❌ Lógica espalhada
- ❌ Difícil de testar
- ❌ Acoplamento alto

### Sua Missão

1. **Identificar violações** dos princípios SOLID
2. **Refatorar o código** aplicando os padrões corretos
3. **Organizar em camadas** seguindo DDD
4. **Adicionar testes** unitários

## 🎓 Conceitos a Aplicar

### SRP - Single Responsibility
- Separar validação, persistência, notificação, etc.

### OCP - Open/Closed  
- Usar Strategy para descontos e pagamentos

### DIP - Dependency Inversion
- Usar interfaces e injeção de dependência

### Design Patterns
- **Strategy:** Descontos e métodos de pagamento
- **Factory:** Criação de notificações
- **Builder:** Construção de Order

### DDD
- **Entities:** Order, Customer
- **Value Objects:** Money, Email
- **Aggregates:** Order + OrderItems
- **Domain Services:** OrderPricingService

## 💡 Dica

Comece revisando os projetos:
1. `01-solid-principles-demo` - ver exemplos de refatoração
2. `02-design-patterns-demo` - ver implementação dos patterns
3. `03-ddd-demo` - ver estrutura DDD

## 📚 Referência

Consulte o **slide-19.md** para ver o código problemático completo e as instruções detalhadas de refatoração.

---

**Bom trabalho! 🚀**

Aplique tudo que aprendeu neste exercício final!
