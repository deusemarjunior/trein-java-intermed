# Resumo das Alterações - Novos Tópicos Adicionados

## 📍 Localização
Os novos tópicos foram adicionados ao **Dia 4** do treinamento, que agora aborda:
- Arquitetura Hexagonal
- Clean Architecture
- **Transações ACID** (novo)
- **Padrão SAGA** (novo)
- **Orquestração vs Coreografia** (novo)

## 📄 Arquivos Criados

### 1. [dia-04/slide-01.md](dia-04/slide-01.md) - Transações ACID
**Conteúdo:**
- Conceitos fundamentais de ACID (Atomicity, Consistency, Isolation, Durability)
- Implementação em Spring Boot com `@Transactional`
- Níveis de isolamento de transações
- Locks otimistas e pessimistas
- Write-Ahead Logging (WAL)
- Trade-offs do ACID
- Quando NÃO usar ACID tradicional
- Exemplos práticos completos

**Destaques:**
- Diagramas Mermaid explicativos
- Código Java comentado
- Comparações visuais
- Best practices

### 2. [dia-04/slide-02.md](dia-04/slide-02.md) - Padrão SAGA
**Conteúdo:**
- O que é o Padrão SAGA
- Por que ACID não funciona em microservices
- Funcionamento do SAGA (cenários de sucesso e falha)
- Transações locais e compensatórias
- SAGA Orquestrada vs SAGA Coreografada
- Implementações completas em Java
- Desafios (idempotência, ordem de eventos, falhas parciais)
- Ferramentas (Spring Boot, Kafka, frameworks)
- Quando usar SAGA

**Destaques:**
- Comparação detalhada Orquestração vs Coreografia
- Código completo de implementação
- Sequence diagrams do fluxo
- Exemplos de compensação

### 3. [dia-04/slide-03.md](dia-04/slide-03.md) - Orquestração vs Coreografia
**Conteúdo:**
- Comparação aprofundada dos dois padrões
- Arquitetura de cada abordagem
- Implementações completas com Spring Boot
- Modelo de estado da SAGA
- Event handlers e publishers
- Tabela comparativa lado a lado
- Quando usar cada padrão
- Padrão híbrido (melhor dos dois mundos)
- Ferramentas e tecnologias recomendadas

**Destaques:**
- Implementações completas e funcionais
- Código de produção real
- Diagramas de arquitetura
- Guia de decisão
- Best practices para ambos os padrões

## 📝 Arquivo Atualizado

### [dia-04/README.md](dia-04/README.md)
**Alterações:**
- Título atualizado para incluir "Transações Distribuídas"
- Duração aumentada de 5h para 6h
- Seção "Transações ACID" adicionada (30min)
- Seção "Padrão SAGA" adicionada (1.5h)
- Comparação Orquestração vs Coreografia
- Objetivos de aprendizagem atualizados
- Tarefas de casa expandidas
- Links úteis adicionados
- Notas do instrutor atualizadas

## 🎯 Estrutura do Dia 4

```
Dia 4 - 6 horas
├── Manhã (3h)
│   ├── Transações ACID (30min)
│   ├── Arquitetura Hexagonal (1.5h)
│   └── Clean Architecture (1h)
└── Tarde (3h)
    ├── Padrão SAGA (1.5h)
    │   ├── SAGA Orquestrada
    │   ├── SAGA Coreografada
    │   └── Comparação
    └── Implementação Prática (1.5h)
```

## 📚 Tópicos Abordados

### Transações ACID
- ⚛️ Atomicity - Tudo ou nada
- 🔄 Consistency - Dados sempre válidos
- 🔒 Isolation - Transações não interferem
- 💾 Durability - Dados persistidos

### Padrão SAGA
- 🎯 Conceitos e motivação
- 🔄 Transações locais
- 🔙 Compensações
- 🎻 Orquestração (centralizada)
- 💃 Coreografia (distribuída)

### Orquestração vs Coreografia
- 📊 Comparação detalhada
- 💻 Implementações completas
- 🎯 Quando usar cada uma
- 🔀 Padrão híbrido

## 🎓 Conceitos Chave

1. **ACID tradicional funciona bem em monolitos** com um único banco de dados
2. **ACID não funciona em microservices** onde cada serviço tem seu próprio banco
3. **SAGA é a solução** para transações distribuídas
4. **Orquestração** é mais fácil de começar (coordenador central)
5. **Coreografia** é mais escalável (baseada em eventos)
6. **Híbrido** combina o melhor dos dois mundos

## ✅ Exemplos Práticos Incluídos

- ✅ Transferência bancária com `@Transactional`
- ✅ SAGA Orquestrada para criação de pedido
- ✅ SAGA Coreografada com eventos
- ✅ Tratamento de compensações
- ✅ Idempotência em event handlers
- ✅ Correlation IDs para rastreamento
- ✅ Retry e Dead Letter Queues

## 🛠️ Tecnologias Mencionadas

- Spring Boot / Spring Data JPA
- Apache Kafka
- RabbitMQ
- Camunda
- Temporal
- Netflix Conductor
- Spring Cloud Stream

## 💡 Por que Dia 4?

O Dia 4 já abordava **Arquitetura Hexagonal e Clean Architecture**, que são conceitos fundamentais para sistemas distribuídos. Adicionar **Transações Distribuídas e SAGA** complementa perfeitamente o conteúdo, pois:

1. Mostra a **evolução natural** de sistemas monolíticos para microservices
2. Apresenta os **desafios de consistência** em sistemas distribuídos
3. Oferece **soluções práticas** (SAGA) para esses desafios
4. Conecta com os conceitos de **arquitetura limpa** já abordados

## 📖 Fluxo de Aprendizagem

```
Dia 3: SOLID + Design Patterns + DDD
         ↓
Dia 4: Clean Architecture + Hexagonal
         ↓
     ACID (monolito) ❌ não funciona em distribuído
         ↓
     SAGA (microservices) ✅ solução
         ↓
     Orquestração vs Coreografia
```

## 🎯 Resultado Esperado

Ao final do Dia 4, os alunos serão capazes de:
- ✅ Explicar por que ACID não funciona em microservices
- ✅ Implementar SAGA Orquestrada
- ✅ Implementar SAGA Coreografada
- ✅ Escolher entre os dois padrões baseado em requisitos
- ✅ Lidar com compensações e falhas parciais
- ✅ Implementar idempotência e rastreamento
