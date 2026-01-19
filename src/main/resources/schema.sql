-- Создание таблицы mpa_rating
CREATE TABLE IF NOT EXISTS mpa_rating (
    rating_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Создание таблицы genre
CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Создание таблицы users
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255),
    birthday DATE
);

-- Создание таблицы films
CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE,
    duration INTEGER,
    rating_id INTEGER,
    FOREIGN KEY (rating_id) REFERENCES mpa_rating(rating_id) ON DELETE RESTRICT
);

-- Создание таблицы film_genre (связь многие-ко-многим)
CREATE TABLE IF NOT EXISTS film_genre (
    film_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON DELETE RESTRICT
);

-- Создание таблицы likes
CREATE TABLE IF NOT EXISTS likes (
    user_id INTEGER NOT NULL,
    film_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE
);

-- Создание таблицы friends
CREATE TABLE IF NOT EXISTS friends (
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Создание таблицы рецензий (отзывов)
CREATE TABLE IF NOT EXISTS reviews (
    review_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    useful INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS review_likes (
    review_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    is_like BOOLEAN NOT NULL,
    PRIMARY KEY (review_id, user_id),
    FOREIGN KEY (review_id) REFERENCES reviews(review_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Таблица режиссёров
CREATE TABLE IF NOT EXISTS directors (
     director_id INTEGER AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(255) NOT NULL
    );

-- Связь фильмы ↔ режиссёры (многие ко многим)
CREATE TABLE IF NOT EXISTS film_directors (
      film_id INTEGER NOT NULL,
      director_id INTEGER NOT NULL,
      PRIMARY KEY (film_id, director_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (director_id) REFERENCES directors(director_id) ON DELETE CASCADE
    );

-- Создание индексов для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_films_rating ON films(rating_id);
CREATE INDEX IF NOT EXISTS idx_film_genre_film ON film_genre(film_id);
CREATE INDEX IF NOT EXISTS idx_film_genre_genre ON film_genre(genre_id);
CREATE INDEX IF NOT EXISTS idx_likes_user ON likes(user_id);
CREATE INDEX IF NOT EXISTS idx_likes_film ON likes(film_id);
CREATE INDEX IF NOT EXISTS idx_friends_user ON friends(user_id);
CREATE INDEX IF NOT EXISTS idx_friends_friend ON friends(friend_id);


-- Вставка предопределенных данных для MPA рейтингов
MERGE INTO mpa_rating (rating_id, name) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

-- Вставка предопределенных данных для жанров
MERGE INTO genre (genre_id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');