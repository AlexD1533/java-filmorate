package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.repository.FilmRepository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class FilmRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FilmRepository filmRepository;

    @BeforeEach
    void setUp() {
        FilmRowMapper mapper = new FilmRowMapper();
        filmRepository = new FilmRepository(jdbcTemplate, mapper);

        // Очистка таблиц перед каждым тестом
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM mpa_rating");

        // Создание тестовых данных для MPA
        jdbcTemplate.update("INSERT INTO mpa_rating (rating_id, name) VALUES (1, 'G')");
        jdbcTemplate.update("INSERT INTO mpa_rating (rating_id, name) VALUES (2, 'PG')");

        // Вставляем тестовые фильмы БЕЗ указания ID (используем автоматическую генерацию)
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration, rating_id) VALUES ('Film One', 'Description One', '2000-01-01', 120, 1)");
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration, rating_id) VALUES ('Film Two', 'Description Two', '2001-01-01', 130, 2)");
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration, rating_id) VALUES ('Film Three', 'Description Three', '2002-01-01', 140, 1)");
    }

    @Test
    void testCreate() {
        // Arrange
        Film newFilm = new Film();
        newFilm.setName("New Film");
        newFilm.setDescription("New Description");
        newFilm.setReleaseDate(LocalDate.of(2003, 1, 1));
        newFilm.setDuration(150);
        newFilm.setMpa(1L);

        // Act
        Film createdFilm = filmRepository.create(newFilm);

        // Assert
        assertThat(createdFilm.getId()).isPositive();
        assertThat(createdFilm.getName()).isEqualTo("New Film");
        assertThat(createdFilm.getDescription()).isEqualTo("New Description");

        // Проверяем, что фильм действительно сохранен в БД
        Optional<Film> retrievedFilm = filmRepository.getById(createdFilm.getId());
        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getName()).isEqualTo("New Film");
    }

    @Test
    void testUpdate() {
        // Сначала получаем ID существующего фильма
        List<Film> allFilms = (List<Film>) filmRepository.getAll();
        Long existingFilmId = allFilms.get(0).getId();

        // Arrange
        Film filmToUpdate = new Film();
        filmToUpdate.setId(existingFilmId);
        filmToUpdate.setName("Updated Film Name");
        filmToUpdate.setDescription("Updated Description");
        filmToUpdate.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmToUpdate.setDuration(125);
        filmToUpdate.setMpa(2L);

        // Act
        Film updatedFilm = filmRepository.update(filmToUpdate);

        // Assert
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film Name");
        assertThat(updatedFilm.getDuration()).isEqualTo(125);
        assertThat(updatedFilm.getMpa()).isEqualTo(2L);

        // Проверяем, что изменения действительно сохранены в БД
        Optional<Film> retrievedFilm = filmRepository.getById(existingFilmId);
        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getName()).isEqualTo("Updated Film Name");
        assertThat(retrievedFilm.get().getDuration()).isEqualTo(125);
    }

    @Test
    void testGetAll() {
        // Act
        Collection<Film> films = filmRepository.getAll();

        // Assert
        assertThat(films).hasSize(3);
        assertThat(films).extracting(Film::getName)
                .containsExactlyInAnyOrder("Film One", "Film Two", "Film Three");
    }

    @Test
    void testGetById() {
        // Сначала получаем ID существующего фильма
        List<Film> allFilms = (List<Film>) filmRepository.getAll();
        Long existingFilmId = allFilms.get(0).getId();

        // Act
        Optional<Film> film = filmRepository.getById(existingFilmId);

        // Assert
        assertThat(film).isPresent();
        assertThat(film.get().getId()).isEqualTo(existingFilmId);
        assertThat(film.get().getName()).isEqualTo("Film One");
        assertThat(film.get().getDescription()).isEqualTo("Description One");
    }

    @Test
    void testGetById_WhenNotFound() {
        // Act
        Optional<Film> film = filmRepository.getById(999L);

        // Assert
        assertThat(film).isEmpty();
    }

    @Test
    void testFindByNameAndReleaseDate() {
        // Act
        Optional<Film> film = filmRepository.findByNameAndReleaseDate(
                "Film One",
                LocalDate.of(2000, 1, 1)
        );

        // Assert
        assertThat(film).isPresent();
        assertThat(film.get().getName()).isEqualTo("Film One");
    }

    @Test
    void testFindByNameAndReleaseDate_WhenNotFound() {
        // Act
        Optional<Film> film = filmRepository.findByNameAndReleaseDate(
                "Non-existent Film",
                LocalDate.of(2000, 1, 1)
        );

        // Assert
        assertThat(film).isEmpty();
    }

    @Test
    void testGetPopularFilms() {
        // --- Arrange ---
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

        // users
        jdbcTemplate.update("""
                    INSERT INTO users (email, login, name, birthday)
                    VALUES
                    ('test@test.com', 'test', 'Test', '1990-01-01'),
                    ('test2@test.com', 'test2', 'Test2', '1990-01-01')
                """);

        Long userId1 = jdbcTemplate.queryForObject(
                "SELECT user_id FROM users WHERE login = 'test'", Long.class);
        Long userId2 = jdbcTemplate.queryForObject(
                "SELECT user_id FROM users WHERE login = 'test2'", Long.class);

        // films (MPA обязателен!)
        jdbcTemplate.update("""
                    INSERT INTO films (name, description, release_date, duration, rating_id)
                    VALUES
                    ('Film 1', 'Desc', '2000-01-01', 120, 1),
                    ('Film 2', 'Desc', '2001-01-01', 120, 1)
                """);

        Long filmId1 = jdbcTemplate.queryForObject(
                "SELECT film_id FROM films WHERE name = 'Film 1'", Long.class);
        Long filmId2 = jdbcTemplate.queryForObject(
                "SELECT film_id FROM films WHERE name = 'Film 2'", Long.class);

        // likes
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId1, userId1);
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId1, userId2);
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId2, userId1);

        // --- Act ---
        List<Film> popularFilms = filmRepository.getPopularFilms(
                null,   // genreId
                null,   // year
                2       // count
        );

        // --- Assert ---
        assertThat(popularFilms).hasSize(2);
        assertThat(popularFilms.get(0).getId()).isEqualTo(filmId1);
        assertThat(popularFilms.get(1).getId()).isEqualTo(filmId2);
    }

    @Test
    void testValidateId() {
        // Сначала получаем ID существующего фильма
        List<Film> allFilms = (List<Film>) filmRepository.getAll();
        Long existingFilmId = allFilms.get(0).getId();

        // Act & Assert
        assertThat(filmRepository.validateId(existingFilmId)).isTrue();
        assertThat(filmRepository.validateId(999L)).isFalse();
    }

    @Test
    void testDeleteFilm() {
        // Arrange - получим ID существующего фильма
        List<Film> allFilms = (List<Film>) filmRepository.getAll();
        Long existingFilmId = allFilms.get(0).getId();

        // Act
        boolean isDeleted = filmRepository.deleteFilm(existingFilmId);

        // Assert
        assertThat(isDeleted).isTrue();

        // Проверяем, что фильм действительно удален из БД
        Optional<Film> retrievedFilm = filmRepository.getById(existingFilmId);
        assertThat(retrievedFilm).isEmpty();

        // Проверяем, что остальные фильмы остались
        Collection<Film> remainingFilms = filmRepository.getAll();
        assertThat(remainingFilms).hasSize(2);
    }

    @Test
    void testDeleteFilm_WhenNotFound() {
        // Act
        boolean isDeleted = filmRepository.deleteFilm(999L);

        // Assert
        assertThat(isDeleted).isFalse();
    }

    @Test
    void testDeleteFilm_CascadeDeletesLikesAndGenres() {
        // Arrange
        // Создаем фильм
        Film newFilm = new Film();
        newFilm.setName("Cascade Test Film");
        newFilm.setDescription("Test Description");
        newFilm.setReleaseDate(LocalDate.of(2003, 1, 1));
        newFilm.setDuration(150);
        newFilm.setMpa(1L);
        Film createdFilm = filmRepository.create(newFilm);
        Long filmId = createdFilm.getId();

        // Добавляем жанры к фильму
        jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", filmId, 1);
        jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", filmId, 2);

        // Создаем пользователя и лайк
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES ('test@test.com', 'test', 'Test', '1990-01-01')");
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'test@test.com'", Long.class);
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);

        // Act
        boolean isDeleted = filmRepository.deleteFilm(filmId);

        // Assert
        assertThat(isDeleted).isTrue();

        // Проверяем, что лайки удалены (CASCADE)
        Integer likesCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = ?", Integer.class, filmId);
        assertThat(likesCount).isZero();

        // Проверяем, что связи с жанрами удалены (CASCADE)
        Integer genresCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_genre WHERE film_id = ?", Integer.class, filmId);
        assertThat(genresCount).isZero();
    }

    @Test
    void searchFilms_byTitle_sortedByLikes() {
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (10, 'u1@mail.ru', 'user1', 'User One', '1990-01-01')"
        );
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (11, 'u2@mail.ru', 'user2', 'User Two', '1991-01-01')"
        );
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (12, 'u3@mail.ru', 'user3', 'User Three', '1992-01-01')"
        );

        // фильмы (ID далеко от базовых)
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration) " +
                        "VALUES (100, 'Matrix', 'Desc', '1999-03-31', 136)"
        );
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration) " +
                        "VALUES (101, 'Matrix Reloaded', 'Desc', '2003-05-15', 138)"
        );

        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (101, 10)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (101, 11)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (100, 12)");

        List<Film> result = filmRepository.searchFilms("matrix", Set.of("title"));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(101); // больше лайков
        assertThat(result.get(1).getId()).isEqualTo(100);
    }

    @Test
    void searchFilms_byDescription_sortedByLikes() {
        // Arrange - создаем пользователей для лайков
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (20, 'u20@mail.ru', 'user20', 'User Twenty', '1990-01-01')"
        );
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (21, 'u21@mail.ru', 'user21', 'User Twenty-One', '1991-01-01')"
        );
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (22, 'u22@mail.ru', 'user22', 'User Twenty-Two', '1992-01-01')"
        );

        // Создаем фильмы с описаниями, содержащими искомое слово
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) " +
                        "VALUES (100, 'Film A', 'A great adventure movie about matrix world', '2000-01-01', 120, 1)"
        );
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) " +
                        "VALUES (101, 'Film B', 'Sci-fi film with matrix concept', '2001-01-01', 130, 1)"
        );
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) " +
                        "VALUES (102, 'Film C', 'Another film without matrix in description', '2002-01-01', 140, 1)"
        );

        // Добавляем лайки (фильм 101 должен иметь больше лайков)
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (101, 20)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (101, 21)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (100, 22)");

        // Act - ищем по описанию
        List<Film> result = filmRepository.searchFilms("matrix", Set.of("description"));

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(101); // Больше лайков (2 vs 1)
        assertThat(result.get(1).getId()).isEqualTo(100); // Меньше лайков

        // Дополнительные проверки
        assertThat(result).extracting(Film::getName)
                .containsExactly("Film B", "Film A", "Film C");
    }

    @Test
    void searchFilms_byDirector() {
        jdbcTemplate.update("INSERT INTO directors (director_id, name) VALUES (1, 'Nolan')");

        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) " +
                        "VALUES (1, 'Inception', 'Desc', '2010-01-01', 120, 1)"
        );

        jdbcTemplate.update(
                "INSERT INTO film_directors (film_id, director_id) VALUES (1, 1)"
        );

        List<Film> result = filmRepository.searchFilms("nolan", Set.of("director"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Inception");
    }

    @Test
    void searchFilms_byTitleAndDirector() {
        jdbcTemplate.update("INSERT INTO directors (director_id, name) VALUES (1, 'Scott')");

        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) " +
                        "VALUES (1, 'Alien', 'Desc', '1979-01-01', 120, 1)"
        );
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) " +
                        "VALUES (2, 'Blade Runner', 'Desc', '1982-01-01', 120, 1)"
        );

        jdbcTemplate.update("INSERT INTO film_directors (film_id, director_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO film_directors (film_id, director_id) VALUES (2, 1)");

        List<Film> result = filmRepository.searchFilms("scott", Set.of("title", "director"));

        assertThat(result).hasSize(2);
    }

    @Test
    void searchFilms_withoutSearchBy_usesTitleAndDirector() {
        jdbcTemplate.update("INSERT INTO directors (director_id, name) VALUES (1, 'Tarantino')");

        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) " +
                        "VALUES (1, 'Pulp Fiction', 'Desc', '1994-01-01', 120, 1)"
        );

        jdbcTemplate.update("INSERT INTO film_directors (film_id, director_id) VALUES (1, 1)");

        List<Film> result = filmRepository.searchFilms("tarantino", Set.of());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
    }

    @Test
    void searchFilms_whenNothingFound_returnsEmptyList() {
        List<Film> result = filmRepository.searchFilms("unknown", Set.of("title"));

        assertThat(result).isEmpty();
    }

    @Test
    void getCommonFilms_sortedByTotalLikes() {
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (20, 'u20@mail.ru', 'user20', 'User 20', '1990-01-01')"
        );
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (21, 'u21@mail.ru', 'user21', 'User 21', '1991-01-01')"
        );
        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (22, 'u22@mail.ru', 'user22', 'User 22', '1992-01-01')"
        );

        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration) " +
                        "VALUES (200, 'Film A', 'Desc', '2000-01-01', 100)"
        );
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration) " +
                        "VALUES (201, 'Film B', 'Desc', '2001-01-01', 110)"
        );
        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration) " +
                        "VALUES (202, 'Film C', 'Desc', '2002-01-01', 120)"
        );

        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (200, 20)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (200, 21)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (200, 22)");

        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (201, 20)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (201, 21)");

        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (202, 20)");

        List<Film> result = filmRepository.getCommonFilms(20, 21);

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getId()).isEqualTo(200); // 3 лайка
        assertThat(result.get(1).getId()).isEqualTo(201); // 2 лайка
    }
}
