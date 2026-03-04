import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { getFavorites } from '../api/movieApi';
import { useAuth } from '../context/AuthContext';
import MovieGrid from '../components/MovieGrid';
import Loading from '../components/Loading';
import './FavoritesPage.css';

export default function FavoritesPage() {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchFavorites = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError(null);
    try {
      const result = await getFavorites(page, 20);
      setData(result);
    } catch (err) {
      console.error('Erro ao carregar favoritos:', err);
      setError('Erro ao carregar favoritos.');
    } finally {
      setLoading(false);
    }
  }, [page, isAuthenticated]);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/');
      return;
    }
    fetchFavorites();
  }, [fetchFavorites, isAuthenticated, navigate]);

  return (
    <div className="favorites-page">
      <section className="favorites-header">
        <h1>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="#ef4444">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
          </svg>
          Meus Favoritos
        </h1>
        {data && (
          <span className="favorites-count">
            {data.totalElements} filme{data.totalElements !== 1 ? 's' : ''}
          </span>
        )}
      </section>

      {loading && <Loading message="Carregando favoritos..." />}

      {error && (
        <div className="error-banner">
          <p>{error}</p>
          <button onClick={fetchFavorites} className="retry-btn">Tentar novamente</button>
        </div>
      )}

      {!loading && !error && data && (
        <>
          <MovieGrid
            movies={data.content}
            onUpdate={fetchFavorites}
            emptyMessage="Você ainda não favoritou nenhum filme. Explore os filmes populares e comece a criar sua lista!"
          />

          {data.totalPages > 1 && (
            <div className="favorites-pagination">
              <button
                className="pagination-btn"
                disabled={page <= 0}
                onClick={() => setPage((p) => p - 1)}
              >
                Anterior
              </button>
              <span className="pagination-info">
                Página {page + 1} de {data.totalPages}
              </span>
              <button
                className="pagination-btn"
                disabled={page >= data.totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                Próxima
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
