import { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { searchMovies } from '../api/movieApi';
import MovieGrid from '../components/MovieGrid';
import Pagination from '../components/Pagination';
import Loading from '../components/Loading';
import SearchBar from '../components/SearchBar';
import './SearchPage.css';

export default function SearchPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const query = searchParams.get('query') || '';
  const page = parseInt(searchParams.get('page') || '1', 10);

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchResults = useCallback(async () => {
    if (!query) return;
    setLoading(true);
    setError(null);
    try {
      const result = await searchMovies(query, page);
      setData(result);
    } catch (err) {
      console.error('Erro na busca:', err);
      setError('Erro ao buscar filmes. Verifique se o backend está rodando.');
    } finally {
      setLoading(false);
    }
  }, [query, page]);

  useEffect(() => {
    fetchResults();
  }, [fetchResults]);

  const handleSearch = (newQuery) => {
    setSearchParams({ query: newQuery, page: '1' });
  };

  const handlePageChange = (newPage) => {
    setSearchParams({ query, page: String(newPage) });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="search-page">
      <section className="search-header">
        <h1>Buscar Filmes</h1>
        <SearchBar onSearch={handleSearch} initialQuery={query} />
      </section>

      {query && (
        <p className="search-info">
          {data && !loading
            ? `${data.totalResults} resultado${data.totalResults !== 1 ? 's' : ''} para "${query}"`
            : `Buscando "${query}"...`}
        </p>
      )}

      {loading && <Loading message="Buscando filmes..." />}

      {error && (
        <div className="error-banner">
          <p>{error}</p>
          <button onClick={fetchResults} className="retry-btn">Tentar novamente</button>
        </div>
      )}

      {!loading && !error && data && (
        <>
          <MovieGrid
            movies={data.movies}
            onUpdate={fetchResults}
            emptyMessage={`Nenhum filme encontrado para "${query}".`}
          />
          <Pagination
            page={data.page}
            totalPages={Math.min(data.totalPages, 500)}
            onPageChange={handlePageChange}
          />
        </>
      )}

      {!query && !loading && (
        <div className="search-empty">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#475569" strokeWidth="1.2">
            <circle cx="11" cy="11" r="8" />
            <path d="M21 21l-4.35-4.35" />
          </svg>
          <p>Digite o nome de um filme para começar a busca</p>
        </div>
      )}
    </div>
  );
}
