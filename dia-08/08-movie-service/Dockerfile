# ──── Build stage ────
# Usa Maven 3.9 com Java 21 (Eclipse Temurin) para compilar o projeto
FROM docker.io/library/maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia descriptor de dependências + download offline
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia código-fonte e compila
COPY src ./src
RUN mvn package -DskipTests -B

# ──── Runtime stage ────
# Usa JRE (mais leve que JDK) em Alpine Linux (imagem mínima)
FROM docker.io/library/eclipse-temurin:21-jre-alpine

LABEL maintainer="movie-service"
LABEL description="API REST para gerenciamento de filmes - Movie Service"

WORKDIR /app

# Copia o JAR compilado do stage anterior
COPY --from=build /app/target/*.jar app.jar

# Variáveis de ambiente padrão para Spring Boot
ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"

# Health check para monitorar disponibilidade da aplicação
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

# Inicia aplicação Spring Boot
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
