package com.example.movieservice.domain.service;

import com.example.movieservice.domain.port.in.MovieUseCasePort;
import com.example.movieservice.domain.port.out.FavoriteRepositoryPort;
import com.example.movieservice.domain.port.out.MovieApiPort;
import com.example.movieservice.domain.port.out.WatchLaterRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO 4: Implementar MovieService no domain/ (lógica de negócio)
//
// Esta classe deve:
//   - Implementar MovieUseCasePort
//   - Receber MovieApiPort, FavoriteRepositoryPort e WatchLaterRepositoryPort via construtor
//   - Orquestrar chamadas ao MovieApiPort (TheMovieDB) para buscar dados externos
//   - Gerenciar favoritos e "assistir depois" no banco local via os repository ports
//
// Regras de negócio:
//   - Máximo 20 filmes na lista de favoritos por usuário
//     → Se o usuário já tem 20, lançar MaxFavoritesException
//   - Não permitir favoritar o mesmo filme duas vezes
//     → Se já existe, lançar MovieAlreadyFavoritedException
//   - Ao buscar detalhes de um filme, enriquecer com status de favorito/watchLater do usuário
//
// Dicas:
//   - Use @Transactional nos métodos que alteram dados
//   - Use o Logger (SLF4J) para registrar operações importantes
//   - Consulte os testes em domain/service/MovieServiceTest.java para entender os cenários esperados

@Service
@Transactional(readOnly = true)
public class MovieService implements MovieUseCasePort /* TODO 4: implementar os 8 métodos */ {

    private static final Logger log = LoggerFactory.getLogger(MovieService.class);
    private static final int MAX_FAVORITES = 20;

    private final MovieApiPort movieApiPort;
    private final FavoriteRepositoryPort favoriteRepository;
    private final WatchLaterRepositoryPort watchLaterRepository;

    public MovieService(MovieApiPort movieApiPort,
                        FavoriteRepositoryPort favoriteRepository,
                        WatchLaterRepositoryPort watchLaterRepository) {
        this.movieApiPort = movieApiPort;
        this.favoriteRepository = favoriteRepository;
        this.watchLaterRepository = watchLaterRepository;
    }

    // TODO 4: Implementar os métodos do MovieUseCasePort aqui
    //
    // Exemplo de implementação do searchMovies:
    //
    // @Override
    // public MoviePage searchMovies(String query, int page) {
    //     log.info("Buscando filmes com query='{}', page={}", query, page);
    //     return movieApiPort.searchMovies(query, page);
    // }
    //
    // Exemplo de implementação do addFavorite:
    //
    // @Override
    // @Transactional
    // public void addFavorite(Long movieId, String userId) {
    //     log.info("Adicionando filme {} aos favoritos do usuário {}", movieId, userId);
    //
    //     // 1. Verificar se já é favorito
    //     if (favoriteRepository.existsByMovieIdAndUserId(movieId, userId)) {
    //         throw new MovieAlreadyFavoritedException(movieId);
    //     }
    //
    //     // 2. Verificar limite de 20 favoritos
    //     long count = favoriteRepository.countByUserId(userId);
    //     if (count >= MAX_FAVORITES) {
    //         throw new MaxFavoritesException(MAX_FAVORITES);
    //     }
    //
    //     // 3. Buscar dados do filme no TheMovieDB
    //     Movie movie = movieApiPort.getMovieDetails(movieId);
    //
    //     // 4. Salvar no banco local
    //     FavoriteEntity entity = new FavoriteEntity();
    //     entity.setMovieId(movieId);
    //     entity.setUserId(userId);
    //     entity.setTitle(movie.title());
    //     entity.setPosterPath(movie.posterPath());
    //     entity.setOverview(movie.overview());
    //     entity.setVoteAverage(movie.voteAverage());
    //     favoriteRepository.save(entity);
    // }
}
