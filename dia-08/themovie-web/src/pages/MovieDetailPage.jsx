import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getMovieDetails, getMovieCredits, addFavorite, removeFavorite, addWatchLater } from '../api/movieApi';
import { useAuth } from '../context/AuthContext';
import Loading from '../components/Loading';
import './MovieDetailPage.css';

const IMG_BASE = 'https://image.tmdb.org/t/p';

export default function MovieDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const [movie, setMovie] = useState(null);
  const [credits, setCredits] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionLoading, setActionLoading] = useState(false);

  const fetchMovie = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [movieData, creditsData] = await Promise.all([
        getMovieDetails(id),
        getMovieCredits(id).catch(() => null),
      ]);
      setMovie(movieData);
      setCredits(creditsData);
    } catch (err) {
      console.error('Erro ao carregar filme:', err);
      if (err.response?.status === 404) {
        setError('Filme não encontrado.');
      } else {
        setError('Erro ao carregar detalhes do filme.');
      }
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchMovie();
  }, [fetchMovie]);

  const handleFavoriteToggle = async () => {
    if (!isAuthenticated || actionLoading) return;
    setActionLoading(true);
    try {
      if (movie.favorite) {
        await removeFavorite(movie.id);
      } else {
        await addFavorite(movie.id);
      }
      setMovie((prev) => ({ ...prev, favorite: !prev.favorite }));
    } catch (err) {
      alert(err.response?.data?.detail || 'Erro ao atualizar favorito');
    } finally {
      setActionLoading(false);
    }
  };

  const handleWatchLater = async () => {
    if (!isAuthenticated || actionLoading) return;
    setActionLoading(true);
    try {
      await addWatchLater(movie.id);
      setMovie((prev) => ({ ...prev, watchLater: true }));
    } catch (err) {
      alert(err.response?.data?.detail || 'Erro ao marcar assistir depois');
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) return <Loading message="Carregando detalhes do filme..." />;

  if (error) {
    return (
      <div className="detail-error">
        <h2>{error}</h2>
        <button onClick={() => navigate(-1)} className="back-btn">Voltar</button>
      </div>
    );
  }

  if (!movie) return null;

  const backdropUrl = movie.backdropPath
    ? `${IMG_BASE}/original${movie.backdropPath}`
    : null;
  const posterUrl = movie.posterPath
    ? `${IMG_BASE}/w500${movie.posterPath}`
    : null;

  return (
    <div className="detail-page">
      {/* Backdrop */}
      {backdropUrl && (
        <div className="detail-backdrop" style={{ backgroundImage: `url(${backdropUrl})` }} />
      )}

      <div className="detail-content">
        <button onClick={() => navigate(-1)} className="back-link">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M15 18l-6-6 6-6" />
          </svg>
          Voltar
        </button>

        <div className="detail-main">
          {/* Poster */}
          {posterUrl && (
            <div className="detail-poster">
              <img src={posterUrl} alt={movie.title} />
            </div>
          )}

          {/* Info */}
          <div className="detail-info">
            <h1 className="detail-title">{movie.title}</h1>

            <div className="detail-meta">
              {movie.releaseDate && (
                <span className="meta-item">{movie.releaseDate.substring(0, 4)}</span>
              )}
              {movie.voteAverage != null && (
                <span className="meta-item meta-rating">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="#facc15">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 22 12 18.56 5.82 22 7 14.14l-5-4.87 6.91-1.01z" />
                  </svg>
                  {movie.voteAverage.toFixed(1)}
                  <span className="vote-count">({movie.voteCount?.toLocaleString()} votos)</span>
                </span>
              )}
            </div>

            {/* Actions */}
            {isAuthenticated && (
              <div className="detail-actions">
                <button
                  className={`detail-btn ${movie.favorite ? 'fav-active' : ''}`}
                  onClick={handleFavoriteToggle}
                  disabled={actionLoading}
                >
                  <svg width="18" height="18" viewBox="0 0 24 24" fill={movie.favorite ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="2">
                    <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                  </svg>
                  {movie.favorite ? 'Favoritado' : 'Favoritar'}
                </button>
                <button
                  className={`detail-btn ${movie.watchLater ? 'watch-active' : ''}`}
                  onClick={handleWatchLater}
                  disabled={actionLoading || movie.watchLater}
                >
                  <svg width="18" height="18" viewBox="0 0 24 24" fill={movie.watchLater ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="10" />
                    <path d="M12 6v6l4 2" />
                  </svg>
                  {movie.watchLater ? 'Na lista' : 'Assistir depois'}
                </button>
              </div>
            )}

            {/* Overview */}
            {movie.overview && (
              <div className="detail-overview">
                <h3>Sinopse</h3>
                <p>{movie.overview}</p>
              </div>
            )}
          </div>
        </div>

        {/* Cast */}
        {credits?.cast && credits.cast.length > 0 && (
          <section className="detail-cast">
            <h3>Elenco</h3>
            <div className="cast-grid">
              {credits.cast.slice(0, 12).map((person) => (
                <div key={person.id} className="cast-card">
                  {person.profilePath ? (
                    <img
                      src={`${IMG_BASE}/w185${person.profilePath}`}
                      alt={person.name}
                      className="cast-photo"
                      loading="lazy"
                    />
                  ) : (
                    <div className="cast-photo-placeholder">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#475569" strokeWidth="1.5">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                        <circle cx="12" cy="7" r="4" />
                      </svg>
                    </div>
                  )}
                  <span className="cast-name">{person.name}</span>
                  <span className="cast-character">{person.character}</span>
                </div>
              ))}
            </div>
          </section>
        )}

        {/* Crew */}
        {credits?.crew && credits.crew.length > 0 && (
          <section className="detail-crew">
            <h3>Equipe</h3>
            <div className="crew-list">
              {credits.crew
                .filter((c) => ['Director', 'Producer', 'Writer', 'Screenplay'].includes(c.job))
                .slice(0, 6)
                .map((person, i) => (
                  <div key={`${person.id}-${i}`} className="crew-item">
                    <span className="crew-name">{person.name}</span>
                    <span className="crew-job">{person.job}</span>
                  </div>
                ))}
            </div>
          </section>
        )}
      </div>
    </div>
  );
}
