package com.example.movieservice.domain.port.in;

import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.domain.model.MovieCredits;
import com.example.movieservice.domain.model.MoviePage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Port de entrada que define os casos de uso de gerenciamento de filmes.
 * 
 * Esta interface abstrai as operações de negócio que o Controller pode chamar.
 * Implementação: MovieService
 */
public interface MovieUseCasePort {

    /**
     * Busca filmes por texto de busca.
     * 
     * @param query texto de busca (ex: "Matrix")
     * @param page número da página (começa em 1)
     * @return resultado paginado com filmes encontrados
     */
    MoviePage searchMovies(String query, int page);

    /**
     * Obtém detalhes de um filme específico com status do usuário.
     * 
     * @param movieId ID do filme
     * @param userId ID do usuário (para marcar favorito/watchLater)
     * @return detalhes do filme com flags de favorito e watchLater preenchidas
     */
    Movie getMovieDetails(Long movieId, String userId);

    /**
     * Lista filmes populares.
     * 
     * @param page número da página (começa em 1)
     * @return resultado paginado com filmes populares
     */
    MoviePage getPopularMovies(int page);

    /**
     * Obtém elenco e equipe técnica de um filme.
     * 
     * @param movieId ID do filme
     * @return créditos com atores e equipe técnica
     */
    MovieCredits getMovieCredits(Long movieId);

    /**
     * Adiciona um filme aos favoritos do usuário (máximo 20).
     * 
     * @param movieId ID do filme
     * @param userId ID do usuário
     */
    void addFavorite(Long movieId, String userId);

    /**
     * Remove um filme dos favoritos do usuário.
     * 
     * @param movieId ID do filme
     * @param userId ID do usuário
     */
    void removeFavorite(Long movieId, String userId);

    /**
     * Lista os filmes favoritos do usuário (paginado).
     * 
     * @param userId ID do usuário
     * @param pageable informações de paginação
     * @return página com filmes favoritos
     */
    Page<Movie> getFavorites(String userId, Pageable pageable);

    /**
     * Marca um filme para assistir depois.
     * 
     * @param movieId ID do filme
     * @param userId ID do usuário
     */
    void addWatchLater(Long movieId, String userId);

}
