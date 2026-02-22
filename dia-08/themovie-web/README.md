# TheMovie Web — Frontend React

Frontend React para o **Movie Service** — exibe filmes populares, busca, detalhes com elenco, favoritos e autenticação JWT.

> **Para os alunos:** este frontend **já vem pronto**. Você precisa implementar o backend (`08-movie-service`) que alimenta esta aplicação.

## 🖥️ Telas

| Tela | Rota | Endpoint consumido |
|------|------|--------------------|
| Filmes Populares | `/` | `GET /api/movies/popular?page={p}` |
| Busca de Filmes | `/search?query={q}` | `GET /api/movies/search?query={q}&page={p}` |
| Detalhes do Filme | `/movie/:id` | `GET /api/movies/{id}` + `GET /api/movies/{id}/credits` |
| Meus Favoritos | `/favorites` | `GET /api/movies/favorites?page={p}&size={s}` |
| Login (modal) | — | `POST /auth/login` |

**Ações:**
- Favoritar/Desfavoritar → `POST/DELETE /api/movies/{id}/favorite`
- Assistir depois → `POST /api/movies/{id}/watch-later`

## 🚀 Como executar

### Opção 1: Docker (recomendada para alunos)

```bash
# Build da imagem
docker build -t themovie-web .

# Rodar apontando para o backend local
docker run -d \
  --name themovie-web \
  -p 3000:80 \
  -e API_URL=http://host.docker.internal:8080 \
  themovie-web
```

Acesse: http://localhost:3000

> O Nginx faz **proxy reverso**: as chamadas `/api/*` e `/auth/*` são redirecionadas ao backend automaticamente.

### Opção 2: Docker Compose (já incluso no 08-movie-service)

O `docker-compose.yml` do projeto backend já inclui o frontend:

```bash
cd ../08-movie-service
docker compose up -d
```

### Opção 3: Dev local (com Node.js)

```bash
npm install
npm run dev
```

O Vite sobe na porta 3000 com proxy automático para `localhost:8080`.

## 🏗️ Stack

- **React 18** + Vite 5
- **React Router v6** — navegação SPA
- **Axios** — chamadas HTTP + interceptors JWT
- **CSS puro** — sem frameworks, tudo customizado
- **Nginx** — servidor de produção com proxy reverso

## 📁 Estrutura

```
src/
├── main.jsx                    # Entrypoint
├── App.jsx                     # Router + Layout
├── index.css                   # CSS global (reset, variáveis, scrollbar)
├── api/
│   └── movieApi.js             # Chamadas HTTP (axios)
├── context/
│   └── AuthContext.jsx         # Estado de autenticação (JWT)
├── components/
│   ├── Header.jsx / .css       # Navbar com busca e login
│   ├── Footer.jsx / .css       # Rodapé
│   ├── SearchBar.jsx / .css    # Barra de busca
│   ├── MovieCard.jsx / .css    # Card de filme (poster, nota, ações)
│   ├── MovieGrid.jsx / .css    # Grid responsivo de cards
│   ├── Pagination.jsx / .css   # Paginação numérica
│   ├── LoginModal.jsx / .css   # Modal de login
│   └── Loading.jsx / .css      # Spinner de carregamento
└── pages/
    ├── HomePage.jsx / .css         # Filmes populares
    ├── SearchPage.jsx / .css       # Resultados de busca
    ├── MovieDetailPage.jsx / .css  # Detalhes + elenco + equipe
    └── FavoritesPage.jsx / .css    # Lista de favoritos
```

## 🔧 Variáveis de ambiente

| Variável | Contexto | Descrição | Default |
|----------|----------|-----------|---------|
| `VITE_API_URL` | Dev (Vite) | URL base da API | _(vazio — usa proxy)_ |
| `API_URL` | Docker (Nginx) | URL de proxy reverso | `http://host.docker.internal:8080` |

## 🎨 Design

- **Dark theme** com paleta indigo (#6366f1)
- Layout responsivo (mobile-first grid)
- Hover effects nos cards com overlay e ações
- Backdrop blur no header
- Animações suaves (fadeIn, slideUp)
- Poster images via TheMovieDB CDN (`https://image.tmdb.org/t/p/w500/...`)
