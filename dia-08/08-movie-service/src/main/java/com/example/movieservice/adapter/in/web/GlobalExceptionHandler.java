package com.example.movieservice.adapter.in.web;

import org.springframework.web.bind.annotation.RestControllerAdvice;

// TODO 8: Implementar GlobalExceptionHandler com Problem Details (RFC 7807)
//
// Esta classe deve tratar TODAS as exceções da aplicação de forma centralizada.
//
// Exceções a tratar:
//
//   MovieNotFoundException         → 404 Not Found
//   MovieAlreadyFavoritedException → 409 Conflict
//   MaxFavoritesException          → 422 Unprocessable Entity
//   ExternalServiceException       → 503 Service Unavailable
//   InvalidCredentialsException    → 401 Unauthorized
//   MethodArgumentNotValidException → 400 Bad Request (erros de @Valid)
//   Exception (genérica)           → 500 Internal Server Error
//
// Formato da resposta (Problem Details RFC 7807):
// {
//   "type": "about:blank",
//   "title": "Not Found",
//   "status": 404,
//   "detail": "Filme não encontrado com ID: 999",
//   "instance": "/api/movies/999"
// }
//
// O que fazer:
//   1. Anotar a classe com @RestControllerAdvice
//   2. Criar um @ExceptionHandler para cada exceção
//   3. Retornar ResponseEntity<ProblemDetail>
//   4. Usar ProblemDetail.forStatusAndDetail(HttpStatus, String)
//
// Exemplo:
//
// @ExceptionHandler(MovieNotFoundException.class)
// public ResponseEntity<ProblemDetail> handleMovieNotFound(MovieNotFoundException ex) {
//     ProblemDetail problem = ProblemDetail.forStatusAndDetail(
//             HttpStatus.NOT_FOUND, ex.getMessage());
//     problem.setTitle("Filme Não Encontrado");
//     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
// }

@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO 8: Implementar os @ExceptionHandler aqui

}
