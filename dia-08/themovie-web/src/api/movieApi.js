import axios from 'axios';

const API_URL = window.__RUNTIME_CONFIG__?.API_URL || import.meta.env.VITE_API_URL || '';

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Interceptor: injeta token JWT se existir
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor: trata 401 (token expirado)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && localStorage.getItem('token')) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.dispatchEvent(new Event('auth:logout'));
    }
    return Promise.reject(error);
  }
);

// ────────────────────────────────────────
//  Filmes
// ────────────────────────────────────────

export const searchMovies = async (query, page = 1) => {
  const { data } = await api.get('/api/movies/search', {
    params: { query, page },
  });
  return data;
};

export const getPopularMovies = async (page = 1) => {
  const { data } = await api.get('/api/movies/popular', {
    params: { page },
  });
  return data;
};

export const getMovieDetails = async (id) => {
  const { data } = await api.get(`/api/movies/${id}`);
  return data;
};

export const getMovieCredits = async (id) => {
  const { data } = await api.get(`/api/movies/${id}/credits`);
  return data;
};

// ────────────────────────────────────────
//  Favoritos
// ────────────────────────────────────────

export const addFavorite = async (movieId) => {
  await api.post(`/api/movies/${movieId}/favorite`);
};

export const removeFavorite = async (movieId) => {
  await api.delete(`/api/movies/${movieId}/favorite`);
};

export const getFavorites = async (page = 0, size = 10) => {
  const { data } = await api.get('/api/movies/favorites', {
    params: { page, size },
  });
  return data;
};

// ────────────────────────────────────────
//  Watch Later
// ────────────────────────────────────────

export const addWatchLater = async (movieId) => {
  await api.post(`/api/movies/${movieId}/watch-later`);
};

// ────────────────────────────────────────
//  Autenticação
// ────────────────────────────────────────

export const login = async (email, password) => {
  const { data } = await api.post('/auth/login', { email, password });
  return data;
};

export default api;
