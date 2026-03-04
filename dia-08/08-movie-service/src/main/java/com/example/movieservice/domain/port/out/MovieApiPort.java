package com.example.movieservice.domain.port.out;

import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.domain.model.MoviePage;
import com.example.movieservice.domain.model.MovieCredits;

/**
 * Port de saída que define o contrato para consumir uma API externa de filmes.
 * 
 * Este port abstrai a implementação específica (TheMovieDB, OMDB, etc.),
 * permitindo trocar a fonte de dados sem alterar o domínio.
 * 
 * Implementações: TheMovieDbAdapter, MockMovieAdapter (para testes)
 */
public interface MovieApiPort {

    /**
     * Busca filmes por texto de busca.
     * 
     * @param query texto de busca (ex: "Matrix", "Avatar")
     * @param page número da página (começa em 1)
     * @return resultado paginado com lista de filmes encontrados
     */
    MoviePage searchMovies(String query, int page);

    /**
     * Retorna detalhes de um filme específico.
     * 
     * @param movieId ID do filme na API externa
     * @return detalhes completos do filme (título, sinopse, nota, etc.)
     */
    Movie getMovieDetails(Long movieId);

    /**
     * Lista filmes populares.
     * 
     * @param page número da página (começa em 1)
     * @return resultado paginado com filmes populares
     */
    MoviePage getPopularMovies(int page);

    /**
     * Retorna elenco e equipe técnica de um filme.
     * 
     * @param movieId ID do filme na API externa
     * @return créditos com atores e equipe técnica
     */
    MovieCredits getMovieCredits(Long movieId);
    
}
