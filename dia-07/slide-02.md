# Slide 2: Docker — Conceitos e Dockerfile

**Horário:** 09:15 - 09:45

---

## 🐳 O que é Podman?

Podman é uma plataforma de **containerização** que empacota sua aplicação + todas as dependências em um **container** — um ambiente isolado, leve e reproduzível.

```mermaid
graph LR
    subgraph "❌ Sem Podman"
        DEV["💻 Dev: Java 21<br/>PostgreSQL 16<br/>Redis 7<br/>Ubuntu 22.04"]
        PROD["🖥️ Prod: Java 17?<br/>PostgreSQL 14?<br/>Redis 6?<br/>CentOS 7?"]
        DEV -.->|"'Na minha máquina<br/>funciona!' 😅"| PROD
    end

    subgraph "✅ Com Podman"
        CONT1["📦 Container<br/>Java 21<br/>PostgreSQL 16<br/>Redis 7<br/>Alpine Linux"]
        CONT2["📦 Container<br/>Mesmo container<br/>IDÊNTICO em dev,<br/>staging e prod ✅"]
        CONT1 -->|"Mesmo container<br/>em qualquer lugar"| CONT2
    end

    style DEV fill:#e74c3c,color:#fff
    style PROD fill:#e74c3c,color:#fff
    style CONT1 fill:#2ecc71,color:#fff
    style CONT2 fill:#2ecc71,color:#fff
```

---

## Container vs. VM (Máquina Virtual)

```mermaid
graph TB
    subgraph "Máquina Virtual"
        VM_HW["Hardware"]
        VM_HOS["Host OS"]
        VM_HYP["Hypervisor (VMware, Hyper-V)"]
        VM_OS1["Guest OS 1<br/>(~2GB)"]
        VM_OS2["Guest OS 2<br/>(~2GB)"]
        VM_APP1["App 1"]
        VM_APP2["App 2"]

        VM_HW --> VM_HOS --> VM_HYP
        VM_HYP --> VM_OS1 --> VM_APP1
        VM_HYP --> VM_OS2 --> VM_APP2
    end

    subgraph "Container Podman"
        D_HW["Hardware"]
        D_HOS["Host OS"]
        D_ENG["Podman Engine"]
        D_C1["Container 1<br/>(~80MB)"]
        D_C2["Container 2<br/>(~80MB)"]

        D_HW --> D_HOS --> D_ENG
        D_ENG --> D_C1
        D_ENG --> D_C2
    end

    style VM_OS1 fill:#e74c3c,color:#fff
    style VM_OS2 fill:#e74c3c,color:#fff
    style D_C1 fill:#2ecc71,color:#fff
    style D_C2 fill:#2ecc71,color:#fff
```

| Aspecto | VM | Container |
|---------|-----|-----------|
| **Tamanho** | GBs (SO completo) | MBs (apenas libs necessárias) |
| **Boot** | Minutos | Segundos |
| **Isolamento** | Completo (SO separado) | Processo isolado (compartilha kernel) |
| **Performance** | Overhead do hypervisor | Quase nativo |
| **Uso** | Ambientes inteiros | Aplicações/serviços |

---

## Dockerfile — A Receita do Container

O `Dockerfile` é um arquivo de texto com instruções para **construir uma imagem Docker**. Cada instrução cria uma **camada** (layer).

### Dockerfile Básico (NÃO otimizado)

```dockerfile
# ❌ Dockerfile simples — imagem ~400MB
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia TUDO (incluindo .git, .idea, target...)
COPY . .

# Compila dentro do container
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

# Roda com JDK completo (desnecessário em runtime)
ENTRYPOINT ["java", "-jar", "target/app.jar"]
```

### Problemas deste Dockerfile

```mermaid
graph TD
    P1["🔴 Imagem ~400MB<br/>JDK completo (compilador)<br/>não é necessário em runtime"]
    P2["🔴 Copia arquivos desnecessários<br/>.git, .idea, target/ antigo"]
    P3["🔴 Sem cache de dependências<br/>Qualquer mudança no código<br/>baixa todas as deps de novo"]
    P4["🔴 Build e runtime juntos<br/>Ferramentas de compilação<br/>ficam na imagem final"]

    style P1 fill:#e74c3c,color:#fff
    style P2 fill:#e74c3c,color:#fff
    style P3 fill:#e74c3c,color:#fff
    style P4 fill:#e74c3c,color:#fff
```

---

## Camadas Docker — Como funciona o cache

Cada instrução do Dockerfile cria uma **camada (layer)**. O Docker **cacheia** cada camada. Se nada mudou naquela instrução, ele **reutiliza** o cache — sem re-executar.

### Build 1 — Primeira vez (tudo é construído do zero)

```mermaid
graph TD
    L1["FROM eclipse-temurin:21<br/>📥 Baixa imagem base"] --> L2
    L2["COPY pom.xml .<br/>📄 Copia POM"] --> L3
    L3["RUN mvn dependency:resolve<br/>📦 Baixa dependências — ⏱️ 2 min"] --> L4
    L4["COPY src ./src<br/>📄 Copia código fonte"] --> L5
    L5["RUN mvn package<br/>🔨 Compila — ⏱️ 30s"] --> L6
    L6["🐳 Imagem final"]

    style L1 fill:#3498db,color:#fff
    style L2 fill:#3498db,color:#fff
    style L3 fill:#3498db,color:#fff
    style L4 fill:#3498db,color:#fff
    style L5 fill:#3498db,color:#fff
    style L6 fill:#2c3e50,color:#fff
```

### Build 2 — Só mudou código Java (cache das dependências é reaproveitado)

```mermaid
graph TD
    C1["FROM eclipse-temurin:21<br/>✅ CACHE"] --> C2
    C2["COPY pom.xml .<br/>✅ CACHE — pom não mudou"] --> C3
    C3["RUN mvn dependency:resolve<br/>✅ CACHE — deps já baixadas"] --> C4
    C4["COPY src ./src<br/>❌ INVALIDADO — código mudou"] --> C5
    C5["RUN mvn package<br/>🔨 Recompila — ⏱️ 30s"] --> C6
    C6["🐳 Imagem final"]

    style C1 fill:#2ecc71,color:#fff
    style C2 fill:#2ecc71,color:#fff
    style C3 fill:#2ecc71,color:#fff
    style C4 fill:#e74c3c,color:#fff
    style C5 fill:#e74c3c,color:#fff
    style C6 fill:#2c3e50,color:#fff
```

> **Regra de ouro**: Coloque instruções que mudam **pouco** (dependências) **antes** das que mudam **muito** (código fonte). Assim o Docker reutiliza o cache das dependências e o build leva **30s em vez de 2+ minutos**.

---

## Instruções do Dockerfile — Referência

| Instrução | Função | Exemplo |
|-----------|--------|---------|
| `FROM` | Imagem base | `FROM eclipse-temurin:21-jre-alpine` |
| `WORKDIR` | Diretório de trabalho | `WORKDIR /app` |
| `COPY` | Copiar arquivos do host | `COPY target/*.jar app.jar` |
| `RUN` | Executar comando (build time) | `RUN mvn package` |
| `EXPOSE` | Documentar porta | `EXPOSE 8080` |
| `ENV` | Variável de ambiente | `ENV SPRING_PROFILES_ACTIVE=prod` |
| `ENTRYPOINT` | Comando ao iniciar container | `ENTRYPOINT ["java", "-jar", "app.jar"]` |
| `CMD` | Argumentos default | `CMD ["--server.port=8080"]` |
| `ARG` | Argumento de build | `ARG JAR_FILE=app.jar` |
| `HEALTHCHECK` | Health check do container | `HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health` |

---

## 🎯 Quiz Rápido

1. **Por que o Podman é melhor que instalar tudo manualmente?**
   - Ambiente idêntico em dev, staging e produção. "Funciona na minha máquina" deixa de existir.

2. **O que acontece se eu mudar apenas 1 linha de código Java?**
   - Com cache de layers otimizado: só recompila o código (30s). Sem cache: rebaixa tudo (2+ min).

3. **Qual a diferença entre `ENTRYPOINT` e `CMD`?**
   - `ENTRYPOINT` define o executável (não pode ser sobrescrito facilmente). `CMD` define argumentos padrão (pode ser sobrescrito no `docker run`).
