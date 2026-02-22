package com.example.movieservice.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

// TODO 9: Criar testes unitários para MovieService (mínimo 5 cenários)
//
// Use @Mock para simular MovieApiPort, FavoriteRepositoryPort e WatchLaterRepositoryPort.
// Use @InjectMocks para injetar os mocks no MovieService.
//
// Cenários obrigatórios:
//
// 1. Buscar filmes com sucesso
//    - Arranjar: mock do movieApiPort.searchMovies() retornando MoviePage com 3 filmes
//    - Atuar: chamar movieService.searchMovies("Matrix", 1)
//    - Verificar: resultado contém 3 filmes, verify(movieApiPort).searchMovies() chamado
//
// 2. Favoritar filme com sucesso
//    - Arranjar: mock existsByMovieIdAndUserId → false, countByUserId → 5
//    - Atuar: chamar movieService.addFavorite(123L, "user-1")
//    - Verificar: verify(favoriteRepository).save(any()) chamado,
//                 use ArgumentCaptor para verificar movieId e userId
//
// 3. Favoritar além do limite (20) → exceção
//    - Arranjar: mock existsByMovieIdAndUserId → false, countByUserId → 20
//    - Atuar + Verificar: assertThrows(MaxFavoritesException.class, ...)
//
// 4. Detalhar filme inexistente → exceção
//    - Arranjar: mock do movieApiPort.getMovieDetails() lançando MovieNotFoundException
//    - Atuar + Verificar: assertThrows(MovieNotFoundException.class, ...)
//
// 5. Fallback quando TheMovieDB indisponível
//    - Arranjar: mock do movieApiPort.searchMovies() lançando ExternalServiceException
//    - Atuar + Verificar: assertThrows(ExternalServiceException.class, ...)
//
// Estrutura sugerida:
//
// @ExtendWith(MockitoExtension.class)
// class MovieServiceTest {
//
//     @Mock
//     private MovieApiPort movieApiPort;
//
//     @Mock
//     private FavoriteRepositoryPort favoriteRepository;
//
//     @Mock
//     private WatchLaterRepositoryPort watchLaterRepository;
//
//     @InjectMocks
//     private MovieService movieService;
//
//     @Test
//     @DisplayName("Deve buscar filmes com sucesso")
//     void shouldSearchMoviesSuccessfully() {
//         // Arrange
//         // Act
//         // Assert
//     }
// }

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    // TODO 9: Implementar os 5 testes aqui

}
