package com.example.movieservice.domain.service;

import com.example.movieservice.adapter.out.persistence.entity.FavoriteEntity;
import com.example.movieservice.domain.exception.MaxFavoritesException;
import com.example.movieservice.domain.exception.MovieAlreadyFavoritedException;
import com.example.movieservice.domain.model.Movie;
import com.example.movieservice.domain.model.MovieCredits;
import com.example.movieservice.domain.model.MoviePage;
import com.example.movieservice.domain.port.in.MovieUseCasePort;
import com.example.movieservice.domain.port.out.FavoriteRepositoryPort;
import com.example.movieservice.domain.port.out.MovieApiPort;
import com.example.movieservice.domain.port.out.WatchLaterRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Override
    public MoviePage searchMovies(String query, int page) {
        log.info("Buscando filmes com query='{}', page={}", query, page);
        return movieApiPort.searchMovies(query, page);
    }
    //
    // Exemplo de implementação do addFavorite:
    //

    @Transactional
    public void addFavorite(Long movieId, String userId) {
        log.info("Adicionando filme {} aos favoritos do usuário {}", movieId, userId);
    
        // 1. Verificar se já é favorito
        if (favoriteRepository.existsByMovieIdAndUserId(movieId, userId)) {
            throw new MovieAlreadyFavoritedException(movieId);
        }
    
        // 2. Verificar limite de 20 favoritos
        long count = favoriteRepository.countByUserId(userId);
        if (count >= MAX_FAVORITES) {
            throw new MaxFavoritesException(MAX_FAVORITES);
        }
    
        // 3. Buscar dados do filme no TheMovieDB
        Movie movie = movieApiPort.getMovieDetails(movieId);
    
        // 4. Salvar no banco local
        FavoriteEntity entity = new FavoriteEntity();
        entity.setMovieId(movieId);
        entity.setUserId(userId);
        entity.setTitle(movie.title());
        entity.setPosterPath(movie.posterPath());
        entity.setOverview(movie.overview());
        entity.setVoteAverage(movie.voteAverage());
        favoriteRepository.save(entity);
    }

    @Transactional
    public void removeFavorite(Long movieId, String userId) {
        log.info("Removendo filme {} dos favoritos do usuário {}", movieId, userId);
        favoriteRepository.deleteByMovieIdAndUserId(movieId, userId);
    }

    @Transactional
    public void addWatchLater(Long movieId, String userId) {
        log.info("Adicionando filme {} à lista de assistir depois do usuário {}", movieId, userId);

        watchLaterRepository.save(movieId, userId);

    }

    @Transactional(readOnly = true)
    public Page<Movie> getFavorites(String userId, org.springframework.data.domain.Pageable pageable) {
        log.info("Buscando favoritos do usuário {} - page {}", userId, pageable.getPageNumber());
        return favoriteRepository.findByUserId(userId, pageable)
                .map(entity -> {
                    Movie movie = movieApiPort.getMovieDetails(entity.getMovieId());
                    return new Movie(
                            movie.id(),
                            entity.getTitle(),
                            entity.getPosterPath(),
                            entity.getOverview(),
                            entity.getVoteAverage(),
                            true, // favorito
                            watchLaterRepository.existsByMovieIdAndUserId(entity.getMovieId(), userId)
                    );
                });
    }

    @Override
    public Movie getMovieDetails(Long movieId, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMovieDetails'");
    }

    @Override
    public MoviePage getPopularMovies(int page) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPopularMovies'");
    }

    @Override
    public MovieCredits getMovieCredits(Long movieId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMovieCredits'");
    }

    @Override
    public void addFavorite(Long movieId, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addFavorite'");
    }

    @Override
    public void removeFavorite(Long movieId, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeFavorite'");
    }

    @Override
    public Page<Movie> getFavorites(Long userId, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFavorites'");
    }

    @Override
    public void addWatchLater(Long movieId, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addWatchLater'");
    }
}

