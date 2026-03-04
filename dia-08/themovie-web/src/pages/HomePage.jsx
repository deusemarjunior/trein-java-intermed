import { useState, useEffect, useCallback } from 'react';
import { getPopularMovies } from '../api/movieApi';
import MovieGrid from '../components/MovieGrid';
import Pagination from '../components/Pagination';
import Loading from '../components/Loading';
import './HomePage.css';

export default function HomePage() {
  const [data, setData] = useState(null);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchMovies = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await getPopularMovies(page);
      setData(result);
    } catch (err) {
      console.error('Erro ao carregar filmes populares:', err);
      setError(
        err.response?.status === 503
          ? 'O serviço de filmes está temporariamente indisponível. Tente novamente em alguns instantes.'
          : 'Erro ao carregar filmes. Verifique se o backend está rodando em localhost:8080.'
      );
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    fetchMovies();
  }, [fetchMovies]);

  const handlePageChange = (newPage) => {
    setPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="home-page">
      <section className="home-hero">
        <h1>Filmes Populares</h1>
        <p>Descubra os filmes mais populares do momento</p>
      </section>

      {loading && <Loading message="Carregando filmes populares..." />}

      {error && (
        <div className="error-banner">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="8" x2="12" y2="12" />
            <line x1="12" y1="16" x2="12.01" y2="16" />
          </svg>
          <p>{error}</p>
          <button onClick={fetchMovies} className="retry-btn">Tentar novamente</button>
        </div>
      )}

      {!loading && !error && data && (
        <>
          <MovieGrid movies={data.movies} onUpdate={fetchMovies} />
          <Pagination
            page={data.page}
            totalPages={Math.min(data.totalPages, 500)}
            onPageChange={handlePageChange}
          />
        </>
      )}
    </div>
  );
}
