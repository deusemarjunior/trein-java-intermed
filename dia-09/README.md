# Dia 9 - Finalização, Apresentação e Soft Skills

**Duração**: 5 horas  
**Objetivo**: Consolidar o aprendizado, finalizar o microsserviço `08-movie-service` individualmente e se preparar para a realidade da consultoria com apresentação técnica e soft skills.

> **Pré-requisito**: Dia 8 concluído (ou com progresso significativo no `08-movie-service`).

> **⚠️ Importante**: O desafio continua sendo **individual**. Cada aluno finaliza seu projeto, apresenta sua solução e recebe feedback personalizado — seguindo todos os ritos como se fosse em um time de consultoria.

---

## 🎯 Agenda do Dia

| Horário | Duração | Tópico | Tipo |
|---------|---------|--------|------|
| 09:00 - 09:15 | 15min | Daily Scrum: status dos projetos, impedimentos | Discussão |
| 09:15 - 10:45 | 1h30 | Finalização individual do `08-movie-service` + últimos ajustes | Hands-on |
| 10:45 - 11:00 | 15min | ☕ Coffee Break | - |
| 11:00 - 11:45 | 45min | Refactoring ao vivo — código dos alunos (antes/depois) | Demo |
| 11:45 - 12:00 | 15min | Preparação das apresentações | Organização |
| 12:00 - 13:00 | 1h | 🍽️ Almoço | - |
| 13:00 - 13:45 | 45min | Soft Skills para Consultoria | Teórico |
| 13:45 - 15:15 | 1h30 | Apresentações individuais (10-15 min/aluno) | Apresentação |
| 15:15 - 15:45 | 30min | Feedback dos instrutores + Code Review final | Discussão |
| 15:45 - 16:15 | 30min | Retrospectiva e próximos passos da carreira | Discussão |
| 16:15 - 17:00 | 45min | Encerramento e entrega de certificados | Encerramento |

---

## 📦 Material Necessário (Checklist Instrutor)

### Software
- [ ] JDK 21 instalado
- [ ] Maven 3.8+
- [ ] IDE com suporte a Java (IntelliJ ou VS Code)
- [ ] Docker Desktop rodando
- [ ] Projetor/tela para apresentações

### Preparação
- [ ] Selecionar trechos de código dos alunos (com permissão) para refactoring ao vivo
- [ ] Preparar checklist de Code Review para feedback
- [ ] Certificados prontos para entrega

---

## 📋 Conteúdo Programático

---

### 1. Finalização do `08-movie-service` (Individual)

Cada aluno finaliza seu projeto individualmente:

- Completar os TODOs restantes do Dia 8
- Garantir que o frontend **TheMovie Web** consome corretamente os endpoints
- Ajustar testes, tratamento de erros e documentação
- Abrir/atualizar o Pull Request com commits semânticos
- Solicitar Code Review de pelo menos um colega

> **Dica**: Priorize os endpoints que o frontend consome (busca, detalhes, favoritos). Se não der tempo de tudo, entregue o que funciona com qualidade.

---

### 2. Refactoring ao Vivo

Os instrutores selecionam trechos de código dos alunos (com permissão) e refatoram ao vivo:

- **Antes/depois na tela**: mostrando o impacto das mudanças
- Exemplos comuns: God Method no Controller, tratamento de erros genérico, falta de DTOs
- Discussão aberta: por que essa abordagem é melhor?
- Aprendizado coletivo: todos ganham vendo os erros e acertos dos colegas

---

### 3. Soft Skills para Consultoria

- **Como se destacar nos primeiros meses**: proatividade, documentação e comunicação
- **Lidar com diferentes clientes**: adaptar linguagem técnica ao nível do interlocutor
- **Especialista vs. generalista**: vantagens, desvantagens, quando escolher cada caminho
- **Certificações, comunidades, open source e networking**

---

### 4. Como Apresentar Soluções Técnicas

Cada aluno apresentará individualmente seu projeto seguindo esta estrutura:

- **Estrutura da apresentação** (10-15 min):
  1. Contexto do problema e contrato recebido
  2. Demo ao vivo com o **TheMovie Web** consumindo os endpoints
  3. Decisões arquiteturais: por que hexagonal, como organizou os adapters
  4. Código relevante: padrão aplicado, teste que prova, resiliência
  5. Desafios encontrados e como resolveu
  6. Aprendizados do treinamento

- **Dicas**:
  - Mostrar código relevante (não todo): arquitetura, padrão aplicado, teste que prova
  - Lidar com perguntas técnicas: "não sei, vou verificar" é melhor que inventar
  - Respeitar o timebox — praticar antes

---

### 5. Carreira e Próximos Passos

- **Roadmap do desenvolvedor Java**: Spring → Cloud → Microsserviços → Arquitetura
- **Certificações relevantes**: Oracle Java, Spring Professional, AWS/Azure
- **Comunidades**: JUG (Java User Group), meetups, conferências (TDC, QCon)
- **Open Source**: como contribuir e por que isso importa no currículo

---

## 🔄 Continuação do Projeto: `08-movie-service`

**Atividades do dia (individual):**

- **Finalização**: cada aluno completa os TODOs restantes individualmente e faz últimos ajustes
- **Refactoring ao vivo**: instrutores selecionam trechos de código dos alunos (com permissão) e refatoram ao vivo — antes/depois na tela
- **Apresentação Individual**: cada aluno apresenta o seu `08-movie-service` (10-15 min por aluno):
  - Demo ao vivo com o **TheMovie Web** (frontend React) consumindo os endpoints
  - Decisões arquiteturais: por que hexagonal, como organizou os adapters
  - Desafios encontrados: integração com TheMovieDB, resiliência, testes
  - Aprendizados do treinamento
- **Feedback dos instrutores**: pontos fortes, oportunidades de melhoria, dicas para o próximo nível
- **Retrospectiva**: o que funcionou, o que melhorar (formato ágil)
- **Encerramento do treinamento e entrega de certificados**

---

## 📦 Entregáveis Finais

- [ ] `08-movie-service` finalizado e funcionando com o frontend **TheMovie Web** via Docker
- [ ] Apresentação técnica do projeto (demo ao vivo + decisões arquiteturais)
- [ ] Pull Request revisado e aprovado
- [ ] Code Review realizado em pelo menos 1 projeto de colega

---

## 📊 Critérios de Avaliação do Projeto

| Critério | Peso | Descrição |
|----------|------|-----------|
| Funcionalidade | 30% | Endpoints funcionando com o frontend TheMovie Web |
| Arquitetura | 20% | Estrutura hexagonal, separação de responsabilidades |
| Qualidade de código | 15% | Clean Code, nomenclatura, tratamento de erros |
| Testes | 15% | Testes unitários e de integração passando |
| Git/PR | 10% | Commits semânticos, branches, PR organizado |
| Apresentação | 10% | Clareza, demo funcional, respostas às perguntas |
