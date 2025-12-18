
        package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.repository.GenreRepository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class GenreRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        GenreRowMapper mapper = new GenreRowMapper();
        genreRepository = new GenreRepository(jdbcTemplate, mapper);

        // Очистка таблиц перед каждым тестом
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM genre");

        // Создание тестовых данных жанров
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (1, 'Комедия')");
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (2, 'Драма')");
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (3, 'Мультфильм')");
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (4, 'Триллер')");
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (5, 'Документальный')");
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (6, 'Боевик')");

        // Создание тестового фильма
        jdbcTemplate.update("INSERT INTO films (film_id, name, description, release_date, duration, rating_id) VALUES (1, 'Test Film', 'Description', '2000-01-01', 120, 1)");

        // Связывание жанров с фильмом
        jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (1, 2)");
        jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (1, 3)");
    }

    @Test
    void testFindAll() {
        // Act
        List<Genre> genres = genreRepository.findAll();

        // Assert
        assertThat(genres).hasSize(6);
        assertThat(genres).extracting(Genre::getName)
                .containsExactlyInAnyOrder(
                        "Комедия", "Драма", "Мультфильм",
                        "Триллер", "Документальный", "Боевик"
                );
    }

    @Test
    void testFindById() {
        // Act
        Optional<Genre> genre = genreRepository.findById(1L);

        // Assert
        assertThat(genre).isPresent();
        assertThat(genre.get().getId()).isEqualTo(1L);
        assertThat(genre.get().getName()).isEqualTo("Комедия");
    }

    @Test
    void testFindById_WhenNotFound() {
        // Act
        Optional<Genre> genre = genreRepository.findById(999L);

        // Assert
        assertThat(genre).isEmpty();
    }

    @Test
    void testFindIdsByFilm() {
        // Act
        Set<Long> genreIds = genreRepository.findIdsByFilm(1L);

        // Assert
        assertThat(genreIds).hasSize(3);
        assertThat(genreIds).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    void testFindIdsByFilm_WhenNoGenres() {
        // Arrange
        jdbcTemplate.update("INSERT INTO films (film_id, name, description, release_date, duration, rating_id) VALUES (2, 'Film Without Genres', 'Description', '2001-01-01', 130, 1)");

        // Act
        Set<Long> genreIds = genreRepository.findIdsByFilm(2L);

        // Assert
        assertThat(genreIds).isEmpty();
    }

    @Test
    void testFindsGenresByFilm() {
        // Act
        List<Genre> genres = genreRepository.findsGenresByFilm(1L);

        // Assert
        assertThat(genres).hasSize(3);
        assertThat(genres).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Комедия", "Драма", "Мультфильм");
    }

    @Test
    void testSaveGenresIdsByFilm() {
        // Arrange
        Set<Long> newGenreIds = new HashSet<>();
        newGenreIds.add(4L); // Триллер
        newGenreIds.add(5L); // Документальный
        newGenreIds.add(6L); // Боевик

        // Act
        genreRepository.saveGenresIdsByFilm(1L, newGenreIds);

        // Assert
        Set<Long> updatedGenreIds = genreRepository.findIdsByFilm(1L);
        assertThat(updatedGenreIds).hasSize(3);
        assertThat(updatedGenreIds).containsExactlyInAnyOrder(4L, 5L, 6L);
    }

    @Test
    void testSaveGenresIdsByFilm_WithEmptySet() {
        // Arrange
        Set<Long> emptyGenreIds = new HashSet<>();

        // Act
        genreRepository.saveGenresIdsByFilm(1L, emptyGenreIds);

        // Assert
        Set<Long> updatedGenreIds = genreRepository.findIdsByFilm(1L);
        assertThat(updatedGenreIds).isEmpty();
    }

    @Test
    void testSaveGenresIdsByFilm_WithNullSet() {
        // Act
        genreRepository.saveGenresIdsByFilm(1L, null);

        // Assert
        Set<Long> updatedGenreIds = genreRepository.findIdsByFilm(1L);
        assertThat(updatedGenreIds).isEmpty();
    }
}
