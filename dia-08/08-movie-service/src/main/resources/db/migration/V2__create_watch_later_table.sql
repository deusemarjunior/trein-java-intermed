CREATE TABLE IF NOT EXISTS watch_later (
    id          BIGSERIAL    PRIMARY KEY,
    movie_id    BIGINT       NOT NULL,
    user_id     VARCHAR(255) NOT NULL,
    title       VARCHAR(500) NOT NULL,
    poster_path VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_watch_later_movie_user UNIQUE (movie_id, user_id)
);

CREATE INDEX idx_watch_later_user_id ON watch_later(user_id);
