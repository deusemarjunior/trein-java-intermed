package com.example.movieservice.adapter.out.persistence;

import com.example.movieservice.AbstractIntegrationTest;
import com.example.movieservice.adapter.out.persistence.repository.FavoriteJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

// TODO 10: Criar testes de integração com Testcontainers para FavoriteRepository
//
// Esta classe estende AbstractIntegrationTest que já configura:
//   - PostgreSQL real via Testcontainers
//   - DynamicPropertySource com credenciais do container
//   - Cache desabilitado
//
// Cenários sugeridos:
//
// 1. Salvar e buscar favorito por movieId e userId
//    - Salvar FavoriteEntity com movieId=123, userId="user-1"
//    - Buscar: findByMovieIdAndUserId(123, "user-1")
//    - Verificar: entity presente com dados corretos
//
// 2. Contar favoritos por userId
//    - Salvar 3 favoritos para "user-1" e 2 para "user-2"
//    - Verificar: countByUserId("user-1") == 3
//
// 3. Deletar favorito por movieId e userId
//    - Salvar e deletar
//    - Verificar: findByMovieIdAndUserId retorna Optional.empty()
//
// 4. Buscar favoritos paginados
//    - Salvar 15 favoritos para "user-1"
//    - Buscar: findByUserId("user-1", PageRequest.of(0, 10))
//    - Verificar: page.getContent().size() == 10, page.getTotalElements() == 15
//
// 5. Constraint unique (movieId + userId)
//    - Salvar favorito com movieId=123, userId="user-1"
//    - Tentar salvar outro com mesmo movieId e userId
//    - Verificar: lança DataIntegrityViolationException
//
// Estrutura sugerida:
//
// class FavoriteRepositoryIT extends AbstractIntegrationTest {
//
//     @Autowired
//     private FavoriteJpaRepository repository;
//
//     @BeforeEach
//     void setUp() {
//         repository.deleteAll();
//     }
//
//     @Test
//     @DisplayName("Deve salvar e buscar favorito por movieId e userId")
//     void shouldSaveAndFindFavorite() {
//         // Arrange
//         // Act
//         // Assert
//     }
// }

class FavoriteRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private FavoriteJpaRepository repository;

    // TODO 10: Implementar os testes de integração aqui

}
