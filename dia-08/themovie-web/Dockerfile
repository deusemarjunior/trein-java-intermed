# ──── Build stage ────
FROM docker.io/library/node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm ci --ignore-scripts

COPY . .
RUN npm run build

# ──── Production stage ────
FROM docker.io/library/nginx:1.25-alpine

# Remove config padrão
RUN rm /etc/nginx/conf.d/default.conf

# Template com envsubst para injetar API_URL
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Copia build do React
COPY --from=build /app/dist /usr/share/nginx/html

# Variável de ambiente (pode ser sobrescrita no podman run / podman-compose)
ENV API_URL=http://host.containers.internal:8080

EXPOSE 80

# nginx:alpine com templates já usa envsubst automaticamente
CMD ["nginx", "-g", "daemon off;"]
