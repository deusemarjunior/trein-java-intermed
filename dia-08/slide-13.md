# Slide 13: TODOs 9-10 — Testes Unitários e de Integração

**Horário:** 13:15 - 15:00 (continuação)

---

## TODO 9: Testes Unitários para MovieService

Mínimo de **5 cenários** usando JUnit 5 + Mockito:

```java
@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieApiPort movieApiPort;

    @Mock
    private FavoriteRepositoryPort favoriteRepository;

    @InjectMocks
    private MovieService movieService;

    // Cenário 1: Buscar filmes com sucesso
    @Test
    @DisplayName("Deve retornar filmes ao buscar por título")
    void shouldSearchMoviesSuccessfully() {
        // Arrange
        var expected = new MovieSearchResult(1, 5, 100, List.of(
            new MovieSummary(1L, "Matrix", "...", "/poster.jpg", 8.7, LocalDate.of(1999, 3, 31))
        ));
        when(movieApiPort.searchMovies("Matrix", 1)).thenReturn(expected);

        // Act
        var result = movieService.searchMovies("Matrix", 1);

        // Assert
        assertThat(result.results()).hasSize(1);
        assertThat(result.results().get(0).title()).isEqualTo("Matrix");
        verify(movieApiPort).searchMovies("Matrix", 1);
    }

    // Cenário 2: Favoritar filme com sucesso
    @Test
    @DisplayName("Deve favoritar filme quando abaixo do limite")
    void shouldAddFavoriteSuccessfully() {
        when(favoriteRepository.countFavorites()).thenReturn(5L);
        when(movieApiPort.getMovieDetails(550L)).thenReturn(createMovieDetail());

        movieService.addFavorite(550L);

        verify(favoriteRepository).saveFavorite(eq(550L), anyString(), anyString());
    }

    // Cenário 3: Favoritar além do limite (20) → exceção
    @Test
    @DisplayName("Deve lançar exceção ao exceder limite de 20 favoritos")
    void shouldThrowWhenMaxFavoritesExceeded() {
        when(favoriteRepository.countFavorites()).thenReturn(20L);

        assertThrows(MaxFavoritesExceededException.class,
            () -> movieService.addFavorite(550L));

        verify(favoriteRepository, never()).saveFavorite(anyLong(), anyString(), anyString());
    }

    // Cenário 4: Detalhar filme inexistente → exceção
    @Test
    @DisplayName("Deve lançar exceção quando filme não encontrado")
    void shouldThrowWhenMovieNotFound() {
        when(movieApiPort.getMovieDetails(999L))
            .thenThrow(new MovieNotFoundException("Filme 999 não encontrado"));

        assertThrows(MovieNotFoundException.class,
            () -> movieService.getMovieDetails(999L));
    }

    // Cenário 5: Detalhes com status de favorito
    @Test
    @DisplayName("Deve enriquecer detalhes com status de favorito")
    void shouldEnrichDetailsWithFavoriteStatus() {
        when(movieApiPort.getMovieDetails(550L)).thenReturn(createMovieDetail());
        when(favoriteRepository.existsByMovieId(550L)).thenReturn(true);
        when(favoriteRepository.existsWatchLaterByMovieId(550L)).thenReturn(false);

        var result = movieService.getMovieDetails(550L);

        assertThat(result.favorite()).isTrue();
        assertThat(result.watchLater()).isFalse();
    }
}
```

---

## TODO 10: Testes de Integração com Testcontainers

```java
@SpringBootTest
@Testcontainers
class FavoriteRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Test
    @DisplayName("Deve persistir e recuperar favorito do PostgreSQL")
    void shouldSaveAndRetrieveFavorite() {
        // Arrange
        var favorite = new FavoriteEntity(null, 550L, "Fight Club", "/poster.jpg", LocalDateTime.now());

        // Act
        var saved = favoriteRepository.save(favorite);
        var found = favoriteRepository.findByMovieId(550L);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getMovieId()).isEqualTo(550L);
        assertThat(found.get().getTitle()).isEqualTo("Fight Club");
    }

    @Test
    @DisplayName("Deve listar favoritos paginados")
    void shouldListFavoritesPaginated() {
        // Salvar 15 favoritos
        IntStream.rangeClosed(1, 15).forEach(i ->
            favoriteRepository.save(new FavoriteEntity(null, (long) i, "Movie " + i, "/poster.jpg", LocalDateTime.now()))
        );

        var page = favoriteRepository.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }
}
```

> **Lembra do Dia 4?** Mockito para unitários, Testcontainers para integração — agora testando funcionalidades reais do projeto.
