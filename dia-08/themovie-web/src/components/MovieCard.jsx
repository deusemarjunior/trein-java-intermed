import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { addFavorite, removeFavorite, addWatchLater } from '../api/movieApi';
import { useState } from 'react';
import './MovieCard.css';

const IMG_BASE = 'https://image.tmdb.org/t/p/w500';
const PLACEHOLDER = 'data:image/svg+xml,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="500" height="750" fill="%231e1e32"><rect width="500" height="750"/><text x="250" y="375" text-anchor="middle" fill="%23475569" font-size="48">🎬</text></svg>'
);

export default function MovieCard({ movie, onUpdate }) {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [loading, setLoading] = useState(false);

  const posterUrl = movie.posterPath ? `${IMG_BASE}${movie.posterPath}` : PLACEHOLDER;

  const handleFavoriteToggle = async (e) => {
    e.stopPropagation();
    if (!isAuthenticated || loading) return;
    setLoading(true);
    try {
      if (movie.favorite) {
        await removeFavorite(movie.id);
      } else {
        await addFavorite(movie.id);
      }
      onUpdate?.();
    } catch (err) {
      const msg = err.response?.data?.detail || 'Erro ao atualizar favorito';
      alert(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleWatchLater = async (e) => {
    e.stopPropagation();
    if (!isAuthenticated || loading) return;
    setLoading(true);
    try {
      await addWatchLater(movie.id);
      onUpdate?.();
    } catch (err) {
      const msg = err.response?.data?.detail || 'Erro ao marcar assistir depois';
      alert(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <article className="movie-card" onClick={() => navigate(`/movie/${movie.id}`)}>
      <div className="movie-card-poster">
        <img src={posterUrl} alt={movie.title} loading="lazy" />
        <div className="movie-card-overlay">
          {movie.voteAverage != null && (
            <span className="movie-card-rating">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="#facc15">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 22 12 18.56 5.82 22 7 14.14l-5-4.87 6.91-1.01z" />
              </svg>
              {movie.voteAverage.toFixed(1)}
            </span>
          )}

          {isAuthenticated && (
            <div className="movie-card-actions">
              <button
                className={`action-btn fav-btn ${movie.favorite ? 'active' : ''}`}
                onClick={handleFavoriteToggle}
                title={movie.favorite ? 'Remover dos favoritos' : 'Favoritar'}
                disabled={loading}
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill={movie.favorite ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="2">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                </svg>
              </button>
              <button
                className={`action-btn watch-btn ${movie.watchLater ? 'active' : ''}`}
                onClick={handleWatchLater}
                title="Assistir depois"
                disabled={loading}
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill={movie.watchLater ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10" />
                  <path d="M12 6v6l4 2" />
                </svg>
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="movie-card-info">
        <h3 className="movie-card-title">{movie.title}</h3>
        {movie.releaseDate && (
          <span className="movie-card-year">{movie.releaseDate.substring(0, 4)}</span>
        )}
      </div>
    </article>
  );
}
