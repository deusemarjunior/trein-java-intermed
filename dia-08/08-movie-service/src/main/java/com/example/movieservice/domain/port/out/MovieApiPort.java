package com.example.movieservice.domain.port.out;

// TODO 1: Criar o Port de saída (interface) MovieApiPort
//
// Este port define o CONTRATO para consumir uma API externa de filmes.
// A implementação concreta (TheMovieDbAdapter) ficará em adapter/out/rest/.
//
// Métodos que devem ser definidos:
//
//   MoviePage searchMovies(String query, int page);
//       → Buscar filmes por texto (ex: "Matrix")
//
//   Movie getMovieDetails(Long movieId);
//       → Detalhes de um filme específico (título, sinopse, nota, etc.)
//
//   MoviePage getPopularMovies(int page);
//       → Listar filmes populares (paginado)
//
//   MovieCredits getMovieCredits(Long movieId);
//       → Elenco e equipe técnica de um filme
//
// Imports necessários:
//   import com.example.movieservice.domain.model.Movie;
//   import com.example.movieservice.domain.model.MoviePage;
//   import com.example.movieservice.domain.model.MovieCredits;

public interface MovieApiPort {

    // TODO 1: Defina os 4 métodos acima

}
