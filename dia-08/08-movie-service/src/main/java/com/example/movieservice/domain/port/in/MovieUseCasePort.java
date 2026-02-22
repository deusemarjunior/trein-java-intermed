package com.example.movieservice.domain.port.in;

import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.domain.model.MovieCredits;
import com.example.movieservice.domain.model.MoviePage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// TODO 3: Criar o Port de entrada (use case) MovieUseCasePort
//
// Este port define as OPERAÇÕES DE NEGÓCIO que o Controller pode chamar.
// A implementação concreta (MovieService) ficará em domain/service/.
//
// Métodos que devem ser definidos:
//
//   MoviePage searchMovies(String query, int page);
//       → Buscar filmes por texto
//
//   Movie getMovieDetails(Long movieId, String userId);
//       → Detalhes do filme + status de favorito/watchLater do usuário
//
//   MoviePage getPopularMovies(int page);
//       → Listar filmes populares
//
//   MovieCredits getMovieCredits(Long movieId);
//       → Elenco e equipe do filme
//
//   void addFavorite(Long movieId, String userId);
//       → Favoritar filme (máximo 20 por usuário!)
//
//   void removeFavorite(Long movieId, String userId);
//       → Remover filme dos favoritos
//
//   Page<Movie> getFavorites(String userId, Pageable pageable);
//       → Listar favoritos do usuário (paginado)
//
//   void addWatchLater(Long movieId, String userId);
//       → Marcar filme para assistir depois

public interface MovieUseCasePort {

    // TODO 3: Defina os 8 métodos acima

}
