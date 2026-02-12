# Dia 8 - Evolução do Deploy Java: Desktop, Servidores de Aplicação e Containers

## 📜 Histórico da Evolução do Deploy Java

### 1. Era Desktop (1995-2000)

#### Características
- Aplicações Java executadas localmente na máquina do usuário
- Distribuição via JAR (Java Archive) executável
- Interface gráfica com AWT/Swing
- Modelo cliente-servidor com banco de dados

#### Arquitetura

```mermaid
graph TB
    subgraph "Máquina do Usuário"
        A[Aplicação Desktop<br/>JAR Executável] --> B[JRE Instalada]
        B --> C[Interface Gráfica<br/>Swing/AWT]
    end
    
    A -->|JDBC| D[(Banco de Dados<br/>Oracle/MySQL)]
    
    style A fill:#4CAF50
    style B fill:#2196F3
    style C fill:#FF9800
    style D fill:#9C27B0
```

#### Deploy Desktop

```mermaid
flowchart LR
    A[Código Java] --> B[Compilação<br/>javac]
    B --> C[Empacotamento<br/>JAR]
    C --> D[Distribuição<br/>CD-ROM/Download]
    D --> E[Instalação Manual<br/>em cada PC]
    E --> F[Execução<br/>java -jar app.jar]
    
    style A fill:#E3F2FD
    style B fill:#BBDEFB
    style C fill:#90CAF9
    style D fill:#64B5F6
    style E fill:#42A5F5
    style F fill:#2196F3
```

#### Exemplo de Distribuição
```bash
# Build da aplicação
javac -d bin src/**/*.java
jar cvfm app.jar MANIFEST.MF -C bin .

# Distribuição
# - Gravar em CD-ROM
# - Disponibilizar para download
# - Instalar manualmente em cada estação

# Execução no cliente
java -jar app.jar
```

**Vantagens:**
- Simplicidade no desenvolvimento
- Performance local (sem latência de rede)
- Offline-first

**Desvantagens:**
- Atualização complexa (reinstalação em todos os PCs)
- Dependência de JRE instalada
- Difícil manutenção e suporte
- Segurança descentralizada

---

### 2. Era dos Servidores de Aplicação (2000-2015)

#### Características
- Aplicações web centralizadas
- Modelo cliente-servidor com browser
- Java EE (Enterprise Edition)
- Múltiplos clientes acessando servidor único

#### Arquitetura Tradicional

```mermaid
graph TB
    subgraph "Clientes"
        A1[Browser 1]
        A2[Browser 2]
        A3[Browser 3]
    end
    
    subgraph "Servidor de Aplicação"
        B[Load Balancer<br/>Apache/Nginx]
        
        subgraph "Cluster"
            C1[Tomcat/JBoss<br/>Instância 1]
            C2[Tomcat/JBoss<br/>Instância 2]
        end
        
        D[WAR/EAR<br/>Deployment]
    end
    
    subgraph "Camada de Dados"
        E[(Banco de Dados<br/>Centralizado)]
        F[(Cache<br/>Memcached)]
    end
    
    A1 -->|HTTP| B
    A2 -->|HTTP| B
    A3 -->|HTTP| B
    
    B --> C1
    B --> C2
    
    D -.->|Deploy| C1
    D -.->|Deploy| C2
    
    C1 -->|JDBC| E
    C2 -->|JDBC| E
    C1 --> F
    C2 --> F
    
    style B fill:#FF5722
    style C1 fill:#FF9800
    style C2 fill:#FF9800
    style D fill:#4CAF50
    style E fill:#9C27B0
    style F fill:#00BCD4
```

#### Pipeline de Deploy Tradicional

```mermaid
flowchart TD
    A[Código Java] --> B[Build<br/>Maven/Ant]
    B --> C[Testes<br/>JUnit]
    C --> D[Empacotamento<br/>WAR/EAR]
    D --> E{Ambiente}
    
    E -->|Dev| F1[Servidor Dev<br/>Tomcat]
    E -->|QA| F2[Servidor QA<br/>JBoss]
    E -->|Prod| F3[Cluster Prod<br/>WebLogic]
    
    F1 --> G1[Deploy Manual<br/>Manager Console]
    F2 --> G2[Deploy Manual<br/>Admin Console]
    F3 --> G3[Deploy Manual<br/>WebLogic Console]
    
    G1 --> H1[Reiniciar Servidor]
    G2 --> H2[Reiniciar Servidor]
    G3 --> H3[Rolling Restart]
    
    style A fill:#E8F5E9
    style D fill:#4CAF50
    style F1 fill:#FFF3E0
    style F2 fill:#FFE0B2
    style F3 fill:#FF9800
    style H3 fill:#F44336
```

#### Servidores de Aplicação Populares

```mermaid
timeline
    title Linha do Tempo dos Servidores de Aplicação Java
    
    1999 : Tomcat 3.0
         : Servlet Container
    
    2001 : JBoss AS 2.0
         : Full Java EE
    
    2002 : WebLogic 7.0
         : Oracle Enterprise
    
    2004 : WebSphere 6.0
         : IBM Enterprise
    
    2009 : GlassFish 3.0
         : Reference Implementation
    
    2014 : WildFly 8
         : JBoss Evolution
```

#### Exemplo de Deploy em Tomcat
```bash
# Build da aplicação
mvn clean package

# Deploy manual
cp target/myapp.war /opt/tomcat/webapps/

# Ou via Manager
curl -T target/myapp.war \
  "http://manager:password@localhost:8080/manager/text/deploy?path=/myapp"

# Restart do servidor
/opt/tomcat/bin/shutdown.sh
/opt/tomcat/bin/startup.sh

# Verificação
curl http://localhost:8080/myapp/health
```

#### Estrutura de um WAR

```mermaid
graph TD
    A[myapp.war] --> B[WEB-INF/]
    A --> C[META-INF/]
    A --> D[Static Files<br/>HTML, CSS, JS]
    
    B --> E[web.xml<br/>Configuração]
    B --> F[classes/<br/>Java Classes]
    B --> G[lib/<br/>JARs Dependencies]
    
    C --> H[MANIFEST.MF]
    
    style A fill:#4CAF50
    style B fill:#FF9800
    style E fill:#2196F3
    style F fill:#9C27B0
    style G fill:#00BCD4
```

**Vantagens:**
- Centralização da aplicação
- Atualização simplificada (deploy único)
- Gerenciamento de sessões e recursos
- Segurança centralizada

**Desvantagens:**
- Acoplamento ao servidor de aplicação
- Startup lento (30s a 5min)
- Overhead de memória
- Complexidade de configuração
- Difícil escalabilidade

---

### 3. Era dos Containers (2015-Presente)

#### Características
- Aplicações auto-contidas
- Imutabilidade de infraestrutura
- Microsserviços
- Cloud-native

#### Arquitetura com Containers

```mermaid
graph TB
    subgraph "Clientes"
        A[Browser/Mobile]
    end
    
    subgraph "Orquestrador - Kubernetes"
        B[Ingress Controller<br/>NGINX/Traefik]
        
        subgraph "Pods"
            C1[Container 1<br/>Spring Boot App]
            C2[Container 2<br/>Spring Boot App]
            C3[Container 3<br/>Spring Boot App]
        end
        
        D[Service<br/>Load Balancer]
    end
    
    subgraph "Dados"
        E[(PostgreSQL<br/>Container)]
        F[(Redis<br/>Container)]
        G[Volume<br/>Persistente]
    end
    
    A -->|HTTPS| B
    B --> D
    D --> C1
    D --> C2
    D --> C3
    
    C1 --> E
    C2 --> E
    C3 --> E
    
    C1 --> F
    C2 --> F
    C3 --> F
    
    E --> G
    
    style B fill:#326CE5
    style C1 fill:#4CAF50
    style C2 fill:#4CAF50
    style C3 fill:#4CAF50
    style D fill:#2196F3
    style E fill:#9C27B0
    style F fill:#FF5722
```

#### Pipeline CI/CD Moderno

```mermaid
flowchart TD
    A[Git Push] --> B[CI/CD Trigger<br/>GitHub Actions]
    
    B --> C[Build Stage]
    C --> C1[Compile Code]
    C1 --> C2[Run Tests]
    C2 --> C3[Code Coverage]
    C3 --> C4[Security Scan]
    
    C4 --> D[Docker Build]
    D --> D1[Multi-stage Build]
    D1 --> D2[Create Image]
    D2 --> D3[Scan Image]
    
    D3 --> E[Push to Registry]
    E --> E1[Tag Image<br/>SHA + Version]
    E1 --> E2[ACR/ECR/GCR]
    
    E2 --> F{Deploy Strategy}
    
    F -->|Rolling| G1[Rolling Update]
    F -->|Blue-Green| G2[Blue-Green]
    F -->|Canary| G3[Canary Release]
    
    G1 --> H[Health Check]
    G2 --> H
    G3 --> H
    
    H --> I{OK?}
    I -->|Yes| J[Complete]
    I -->|No| K[Rollback]
    K --> L[Previous Version]
    
    style A fill:#E8F5E9
    style B fill:#C8E6C9
    style C fill:#A5D6A7
    style D fill:#81C784
    style E fill:#66BB6A
    style F fill:#4CAF50
    style J fill:#2E7D32
    style K fill:#F44336
```

#### Container vs Servidor de Aplicação

```mermaid
graph LR
    subgraph "Servidor de Aplicação Tradicional"
        A1[Servidor Físico/VM] --> A2[SO Completo<br/>Linux/Windows]
        A2 --> A3[JRE/JDK]
        A3 --> A4[Tomcat/JBoss<br/>500MB+]
        A4 --> A5[App 1<br/>WAR]
        A4 --> A6[App 2<br/>WAR]
        A4 --> A7[App 3<br/>WAR]
    end
    
    subgraph "Container Moderno"
        B1[Host] --> B2[Docker Engine]
        B2 --> B3[Container 1<br/>150MB]
        B2 --> B4[Container 2<br/>150MB]
        B2 --> B5[Container 3<br/>150MB]
        
        B3 --> B3A[Alpine Linux<br/>JRE<br/>Spring Boot App]
        B4 --> B4A[Alpine Linux<br/>JRE<br/>Spring Boot App]
        B5 --> B5A[Alpine Linux<br/>JRE<br/>Spring Boot App]
    end
    
    style A4 fill:#FF9800
    style A5 fill:#FFB74D
    style A6 fill:#FFB74D
    style A7 fill:#FFB74D
    style B3 fill:#4CAF50
    style B4 fill:#4CAF50
    style B5 fill:#4CAF50
```

#### Dockerfile Multi-stage

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Segurança: usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR
COPY --from=build /app/target/*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Variáveis de ambiente
ENV JAVA_OPTS="-Xmx512m -Xms256m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### Comparação de Tamanho

```mermaid
%%{init: {'theme':'base'}}%%
pie title Tamanho das Imagens
    "Servidor Tradicional (Tomcat)" : 850
    "Container Fat JAR" : 300
    "Container JRE Alpine" : 150
    "Container GraalVM Native" : 50
```

#### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ecommerce-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ecommerce-api
  template:
    metadata:
      labels:
        app: ecommerce-api
    spec:
      containers:
      - name: api
        image: myregistry.azurecr.io/ecommerce-api:1.0.0
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
```

**Vantagens dos Containers:**
- Portabilidade (funciona em qualquer lugar)
- Startup rápido (2-10 segundos)
- Imutabilidade (mesma imagem em todos ambientes)
- Isolamento de processos
- Escalabilidade horizontal fácil
- Versionamento de infraestrutura
- Rollback instantâneo

**Desvantagens:**
- Curva de aprendizado
- Complexidade de orquestração
- Overhead de gerenciamento

---

### 4. Comparativo Geral

```mermaid
%%{init: {'theme':'base'}}%%
quadrantChart
    title Evolução do Deploy Java
    x-axis Baixa Complexidade --> Alta Complexidade
    y-axis Baixa Escalabilidade --> Alta Escalabilidade
    
    Desktop Apps: [0.2, 0.2]
    Servidor Único: [0.4, 0.3]
    Cluster App Server: [0.6, 0.5]
    Containers: [0.7, 0.8]
    Kubernetes: [0.85, 0.95]
```

#### Linha do Tempo Tecnológica

```mermaid
timeline
    title Evolução do Deploy Java (1995-2025)
    
    section Desktop Era
        1995 : Java 1.0 Release
             : Applets e Desktop Apps
        1998 : Swing GUI Framework
             : JAR Executáveis
    
    section Application Server Era
        1999 : Servlet API
             : Tomcat 3.0
        2001 : Java EE
             : JBoss, WebLogic
        2006 : Spring Framework Popularização
             : Tomcat se torna padrão
    
    section Container Era
        2013 : Docker Release
             : Início da Revolução
        2014 : Kubernetes Launch
             : Orquestração de Containers
        2015 : Spring Boot 1.0
             : Embedded Servers
        2018 : Cloud Native
             : Serverless, Functions
        2025 : GraalVM Native
             : Zero Cold Start
```

#### Métricas de Comparação

| Aspecto | Desktop | App Server | Container |
|---------|---------|------------|-----------|
| **Deploy Time** | 15-30 min | 5-10 min | 30-60 seg |
| **Startup Time** | 5-10 seg | 1-5 min | 2-10 seg |
| **Memory Footprint** | 100-500 MB | 512 MB - 2 GB | 256-512 MB |
| **Escalabilidade** | ❌ Não | ⚠️ Limitada | ✅ Horizontal |
| **Portabilidade** | ⚠️ Média | ❌ Baixa | ✅ Alta |
| **Isolamento** | ❌ Não | ⚠️ Parcial | ✅ Total |
| **Versionamento** | ❌ Difícil | ⚠️ Médio | ✅ Fácil |
| **Rollback** | ❌ Manual | ⚠️ Complexo | ✅ Automático |
| **Custo Operacional** | 💰 Baixo | 💰💰 Médio | 💰💰💰 Alto |
| **Curva Aprendizado** | 📚 Baixa | 📚📚 Média | 📚📚📚 Alta |

---

## 🔄 Pipeline CI/CD Completo com GitHub Actions

```yaml
name: Complete CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  REGISTRY: myregistry.azurecr.io
  IMAGE_NAME: ecommerce-api
  JAVA_VERSION: '21'

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: maven
      
      - name: Build with Maven
        run: mvn clean install -DskipTests
      
      - name: Run tests
        run: mvn test
      
      - name: Code coverage
        run: mvn jacoco:report
      
      - name: Upload coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml

  docker-build-push:
    needs: build-and-test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Log in to ACR
        uses: azure/docker-login@v1
        with:
          login-server: ${{ env.REGISTRY }}
          username: ${{ secrets.ACR_USERNAME }}
          password: ${{ secrets.ACR_PASSWORD }}
      
      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: |
            ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:latest
            ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}

  deploy-to-kubernetes:
    needs: docker-build-push
    runs-on: ubuntu-latest
    
    steps:
      - name: Azure Login
        uses: azure/login@v1
        with:
          creds: ${{ secrets.AZURE_CREDENTIALS }}
      
      - name: Set K8s context
        uses: azure/aks-set-context@v3
        with:
          resource-group: myResourceGroup
          cluster-name: myAKSCluster
      
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/ecommerce-api \
            api=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}
          
          kubectl rollout status deployment/ecommerce-api
```

---

## 📚 Material de Estudo

### Leitura Recomendada
- [The Twelve-Factor App](https://12factor.net/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

### Livros
- "Docker Deep Dive" - Nigel Poulton
- "Kubernetes in Action" - Marko Lukša
- "Continuous Delivery" - Jez Humble

---

## 🔗 Links Úteis

- [Docker Hub](https://hub.docker.com/)
- [Kubernetes Patterns](https://kubernetes.io/docs/concepts/cluster-administration/manage-deployment/)
- [GitHub Actions Marketplace](https://github.com/marketplace?type=actions)
- [Azure Container Registry](https://azure.microsoft.com/services/container-registry/)
- [CNCF Cloud Native Landscape](https://landscape.cncf.io/)

## 1. Git Avançado e Workflows

**Git Flow para times**
```bash
# Feature branch
git checkout -b feature/add-payment-api
# ... desenvolve ...
git add .
git commit -m "feat: add payment API endpoints"
git push origin feature/add-payment-api

# Pull Request → Code Review → Merge

# Hotfix
git checkout -b hotfix/fix-payment-bug main
git commit -m "fix: resolve payment gateway timeout"
git push origin hotfix/fix-payment-bug
```

**Conventional Commits**
```
feat: add user authentication
fix: resolve null pointer in order service
docs: update API documentation
style: format code according to style guide
refactor: restructure payment module
test: add integration tests for products
chore: update dependencies
perf: improve database query performance
ci: configure GitHub Actions pipeline
```

**Code Review Best Practices**
```markdown
# Pull Request Template

## Descrição
Adiciona autenticação JWT para proteger endpoints da API

## Tipo de mudança
- [ ] Bug fix
- [x] Nova feature
- [ ] Breaking change
- [ ] Documentação

## Checklist
- [x] Código segue style guide do projeto
- [x] Testes unitários adicionados/atualizados
- [x] Testes de integração adicionados
- [x] Documentação atualizada
- [x] Sem breaking changes
- [x] Build passa localmente

## Como testar
1. Executar `mvn clean install`
2. Fazer login em `/api/auth/login`
3. Usar token em endpoints protegidos
```

**Git hooks**
```bash
# .git/hooks/pre-commit
#!/bin/sh
echo "Running pre-commit hooks..."

# Formatar código
mvn spotless:apply

# Executar testes
mvn test

if [ $? -ne 0 ]; then
    echo "Tests failed. Commit aborted."
    exit 1
fi
```

## 2. Docker - Containerização

**Dockerfile para Java Spring Boot**
```dockerfile
# Multi-stage build

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar apenas pom.xml primeiro (cache de dependências)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código fonte
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Criar usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR do stage de build
COPY --from=build /app/target/*.jar app.jar

# Configurar health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expor porta
EXPOSE 8080

# Configurar JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Executar aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**Docker Compose para ambiente local**
```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ecommerce
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
    networks:
      - app-network
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=ecommerce
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d
    networks:
      - app-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - app-network
    command: redis-server --appendonly yes

  mongodb:
    image: mongo:7
    environment:
      - MONGO_INITDB_ROOT_USERNAME=admin
      - MONGO_INITDB_ROOT_PASSWORD=admin
      - MONGO_INITDB_DATABASE=catalog
    ports:
      - "27017:27017"
    volumes:
      - mongo-data:/data/db
    networks:
      - app-network

volumes:
  postgres-data:
  redis-data:
  mongo-data:

networks:
  app-network:
    driver: bridge
```

**Docker comandos úteis**
```bash
# Build
docker build -t ecommerce-api:latest .

# Run
docker run -d -p 8080:8080 --name ecommerce-api ecommerce-api:latest

# Logs
docker logs -f ecommerce-api

# Exec
docker exec -it ecommerce-api sh

# Compose
docker-compose up -d
docker-compose down
docker-compose logs -f app

# Limpeza
docker system prune -a
docker volume prune
```

**Otimização de imagens**
```dockerfile
# Ruim: ~800MB
FROM openjdk:21
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Melhor: ~300MB
FROM eclipse-temurin:21-jre-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Ótimo: ~200MB (com multi-stage)
FROM maven:3.9-eclipse-temurin-21 AS build
# ... build ...

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 3. Azure Container Registry

**Push para ACR**
```bash
# Login no Azure
az login

# Criar registry
az acr create \
  --resource-group myResourceGroup \
  --name myregistry \
  --sku Basic

# Login no ACR
az acr login --name myregistry

# Tag da imagem
docker tag ecommerce-api:latest myregistry.azurecr.io/ecommerce-api:latest

# Push
docker push myregistry.azurecr.io/ecommerce-api:latest

# Pull
docker pull myregistry.azurecr.io/ecommerce-api:latest
```

## 4. GitHub Actions - CI/CD

**Workflow básico**
```yaml
# .github/workflows/ci.yml
name: CI Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

env:
  JAVA_VERSION: '21'
  MAVEN_OPTS: -Xmx512m

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: maven
      
      - name: Build with Maven
        run: mvn clean install -DskipTests
      
      - name: Run tests
        run: mvn test
      
      - name: Generate test report
        uses: dorny/test-reporter@v1
        if: always()
        with:
          name: Test Results
          path: target/surefire-reports/*.xml
          reporter: java-junit
      
      - name: Code coverage
        run: mvn jacoco:report
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml
          fail_ci_if_error: true
```

**Pipeline completo (Build + Deploy)**
```yaml
# .github/workflows/cd.yml
name: CD Pipeline

on:
  push:
    branches: [ main ]
    tags:
      - 'v*'

env:
  REGISTRY: myregistry.azurecr.io
  IMAGE_NAME: ecommerce-api

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      
      - name: Build and Test
        run: |
          mvn clean verify
          mvn jacoco:report
      
      - name: SonarQube Scan
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: |
          mvn sonar:sonar \
            -Dsonar.projectKey=ecommerce-api \
            -Dsonar.host.url=https://sonarcloud.io \
            -Dsonar.organization=myorg
      
      - name: Upload artifact
        uses: actions/upload-artifact@v3
        with:
          name: app-jar
          path: target/*.jar

  docker-build-push:
    needs: build-and-test
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Download artifact
        uses: actions/download-artifact@v3
        with:
          name: app-jar
          path: target/
      
      - name: Log in to Azure Container Registry
        uses: azure/docker-login@v1
        with:
          login-server: ${{ env.REGISTRY }}
          username: ${{ secrets.ACR_USERNAME }}
          password: ${{ secrets.ACR_PASSWORD }}
      
      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
          tags: |
            type=ref,event=branch
            type=semver,pattern={{version}}
            type=sha
      
      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=registry,ref=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:buildcache
          cache-to: type=registry,ref=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:buildcache,mode=max


  deploy-to-kubernetes:
    needs: docker-build-push
    runs-on: ubuntu-latest
    
    steps:
      - name: Azure Login
        uses: azure/login@v1
        with:
          creds: ${{ secrets.AZURE_CREDENTIALS }}
      
      - name: Set K8s context
        uses: azure/aks-set-context@v3
        with:
          resource-group: myResourceGroup
          cluster-name: myAKSCluster
      
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/ecommerce-api \
            api=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}
          
          kubectl rollout status deployment/ecommerce-api
```

---

## 📚 Material de Estudo

### Leitura Recomendada
- [The Twelve-Factor App](https://12factor.net/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

### Livros
- "Docker Deep Dive" - Nigel Poulton
- "Kubernetes in Action" - Marko Lukša
- "Continuous Delivery" - Jez Humble

---

## 🔗 Links Úteis

- [Docker Hub](https://hub.docker.com/)
- [Kubernetes Patterns](https://kubernetes.io/docs/concepts/cluster-administration/manage-deployment/)
- [GitHub Actions Marketplace](https://github.com/marketplace?type=actions)
- [Azure Container Registry](https://azure.microsoft.com/services/container-registry/)
- [CNCF Cloud Native Landscape](https://landscape.cncf.io/)
