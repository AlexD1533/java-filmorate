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
}
