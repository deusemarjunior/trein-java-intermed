# ──── Build stage ────
FROM docker.io/library/node:20-alpine AS build

WORKDIR /app

# Copia arquivos de dependência
COPY package.json package-lock.json* ./

# Instala dependências
RUN npm install --ignore-scripts

# Copia código fonte
COPY . .

# Build da aplicação React com Vite
RUN npm run build

# ──── Production stage ────
FROM docker.io/library/nginx:1.25-alpine

LABEL maintainer="movie-service"
LABEL description="Frontend React para Movie Service - Consumidor da API Spring Boot"

# Remove config padrão do nginx
RUN rm /etc/nginx/conf.d/default.conf

# Template com envsubst para injetar variáveis de ambiente
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Copia build do React (gerado no stage anterior)
COPY --from=build /app/dist /usr/share/nginx/html

# Variáveis de ambiente para API backend
# Padrão: serviço backend rodando em localhost:8080 (ajuste conforme necessário)
ENV API_URL=http://localhost:8080

# Health check para monitorar container
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost/index.html || exit 1

EXPOSE 80

# Inicia nginx em foreground (necessário para container)
CMD ["nginx", "-g", "daemon off;"]
