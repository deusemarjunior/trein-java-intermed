CREATE TABLE IF NOT EXISTS favorites (
    id          BIGSERIAL    PRIMARY KEY,
    movie_id    BIGINT       NOT NULL,
    user_id     VARCHAR(255) NOT NULL,
    title       VARCHAR(500) NOT NULL,
    poster_path VARCHAR(500),
    overview    TEXT,
    vote_average DOUBLE PRECISION,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_favorites_movie_user UNIQUE (movie_id, user_id)
);

CREATE INDEX idx_favorites_user_id ON favorites(user_id);
CREATE INDEX idx_favorites_movie_id ON favorites(movie_id);
