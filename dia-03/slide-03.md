# Slide 3: Introdução aos Princípios SOLID

**Horário:** 09:15 - 10:45

---

## 🎯 O que é SOLID?

**SOLID** é um acrônimo de 5 princípios de design orientado a objetos criados por Robert C. Martin (Uncle Bob)

```mermaid
graph LR
    A[Código Limpo] --> B[SOLID]
    B --> C[Fácil Manutenção]
    B --> D[Fácil Teste]
    B --> E[Baixo Acoplamento]
    B --> F[Alta Coesão]
    
    style B fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## 📚 Os 5 Princípios

```mermaid
mindmap
  root((SOLID))
    S
      Single Responsibility
      Uma razão para mudar
    O
      Open/Closed
      Aberto para extensão
      Fechado para modificação
    L
      Liskov Substitution
      Substituibilidade de tipos
    I
      Interface Segregation
      Interfaces específicas
    D
      Dependency Inversion
      Depender de abstrações
```

---

## 🎨 Por que SOLID?

### Sem SOLID ❌
```mermaid
graph TD
    A[Código Espaguete] --> B[Alta Complexidade]
    B --> C[Difícil Manutenção]
    C --> D[Bugs Frequentes]
    D --> E[Equipe Desmotivada]
    E --> F[Projeto Fracassa]
    
    style F fill:#f44336,stroke:#c62828,color:#fff
```

### Com SOLID ✅
```mermaid
graph TD
    A[Código Limpo] --> B[Baixa Complexidade]
    B --> C[Fácil Manutenção]
    C --> D[Poucos Bugs]
    D --> E[Equipe Produtiva]
    E --> F[Projeto Sucesso]
    
    style F fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## 📊 Impacto de SOLID

```mermaid
quadrantChart
    title Qualidade do Código vs Esforço de Manutenção
    x-axis Baixo Esforço --> Alto Esforço
    y-axis Baixa Qualidade --> Alta Qualidade
    quadrant-1 Ideal (SOLID)
    quadrant-2 Sobre-engenharia
    quadrant-3 Código Legado
    quadrant-4 Código Técnico
    
    Código com SOLID: [0.25, 0.85]
    Código sem SOLID: [0.75, 0.25]
    Código Refatorado: [0.45, 0.70]
```

---

## 💡 Dica do Instrutor

```
⚠️ Lembre-se:
- SOLID não é tudo ou nada
- Use com bom senso
- Simplicidade primeiro
- Refatore quando necessário
- Não force abstrações prematuras
```
