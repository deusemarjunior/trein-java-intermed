package com.example.movieservice.adapter.in.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

// TODO 5: Criar MovieController seguindo o contrato Swagger (openapi.yaml)
//
// Esta classe é o ADAPTER DE ENTRADA — recebe as requisições HTTP e delega ao use case.
//
// Endpoints que o frontend TheMovie Web consome:
//
//   GET  /api/movies/search?query={q}&page={p}     → Buscar filmes
//   GET  /api/movies/{id}                           → Detalhes do filme
//   GET  /api/movies/popular?page={p}               → Filmes populares
//   POST /api/movies/{id}/favorite                  → Favoritar filme
//   DELETE /api/movies/{id}/favorite                → Desfavoritar filme
//   POST /api/movies/{id}/watch-later               → Marcar para assistir depois
//   GET  /api/movies/favorites?page=0&size=10       → Listar favoritos (paginado)
//
// O que fazer:
//   1. Injetar MovieUseCasePort via construtor
//   2. Criar os 7 endpoints acima
//   3. Retornar ResponseEntity com status codes adequados (200, 201, 204, 404)
//   4. O userId pode ser hardcoded como "user-1" por enquanto (até implementar JWT)
//
// TODO 11: Documentar endpoints com Swagger:
//   - @Tag(name = "Movies", description = "Endpoints de filmes")
//   - @Operation(summary = "...") em cada endpoint
//   - @ApiResponse(responseCode = "200", description = "...") em cada endpoint
//
// TODO 12: Após implementar JWT, extrair o userId do token:
//   - Receber Authentication ou @AuthenticationPrincipal nos métodos
//   - Substituir "user-1" pelo userId real do token

@RestController
@RequestMapping("/api/movies")
// TODO 11: @Tag(name = "Movies", description = "Endpoints de filmes")
public class MovieController {

    private static final Logger log = LoggerFactory.getLogger(MovieController.class);

    // TODO 5: Injetar MovieUseCasePort e criar os endpoints
    //
    // Exemplo de implementação do search:
    //
    // private final MovieUseCasePort movieUseCase;
    //
    // public MovieController(MovieUseCasePort movieUseCase) {
    //     this.movieUseCase = movieUseCase;
    // }
    //
    // @GetMapping("/search")
    // @Operation(summary = "Buscar filmes por texto")
    // public ResponseEntity<MoviePage> searchMovies(
    //         @RequestParam String query,
    //         @RequestParam(defaultValue = "1") int page) {
    //     log.info("Buscando filmes: query='{}', page={}", query, page);
    //     return ResponseEntity.ok(movieUseCase.searchMovies(query, page));
    // }
    //
    // @PostMapping("/{id}/favorite")
    // @Operation(summary = "Favoritar um filme")
    // public ResponseEntity<Void> addFavorite(@PathVariable Long id) {
    //     String userId = "user-1"; // TODO 12: extrair do JWT
    //     movieUseCase.addFavorite(id, userId);
    //     return ResponseEntity.status(HttpStatus.CREATED).build();
    // }
}
