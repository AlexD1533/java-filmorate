package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService filmService;
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;

    private Film film1;
    private Film film2;
    private Film film3;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);

        film1 = new Film(0, "Film 1", "Description 1",
                LocalDate.of(2020, 1, 1), 120);
        film2 = new Film(0, "Film 2", "Description 2",
                LocalDate.of(2021, 1, 1), 130);
        film3 = new Film(0, "Film 3", "Description 3",
                LocalDate.of(2022, 1, 1), 140);

        film1 = filmService.create(film1);
        film2 = filmService.create(film2);
        film3 = filmService.create(film3);

        user1 = new User(0, "user1@email.com", "user1", "User One",
                LocalDate.of(1990, 1, 1));
        user2 = new User(0, "user2@email.com", "user2", "User Two",
                LocalDate.of(1991, 1, 1));

        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);
    }

    @Test
    void create_ShouldCreateFilm() {
        Film newFilm = new Film(0, "New Film", "New Description",
                LocalDate.of(2023, 1, 1), 150);

        Film createdFilm = filmService.create(newFilm);

        assertNotNull(createdFilm);
        assertTrue(createdFilm.getId() > 0);
        assertEquals("New Film", createdFilm.getName());
    }

    @Test
    void update_ShouldUpdateFilm() {
        Film updatedFilm = new Film(film1.getId(), "Updated Film 1", "Updated Description 1",
                LocalDate.of(2020, 1, 1), 180);

        Film result = filmService.update(updatedFilm);

        assertEquals("Updated Film 1", result.getName());
        assertEquals("Updated Description 1", result.getDescription());
        assertEquals(180, result.getDuration());
    }

    @Test
    void update_ShouldThrowExceptionWhenFilmNotFound() {
        Film nonExistentFilm = new Film(999, "Non-existent", "Desc",
                LocalDate.of(2020, 1, 1), 120);

        assertThrows(NotFoundException.class, () -> filmService.update(nonExistentFilm));
    }

    @Test
    void getAll_ShouldReturnAllFilms() {
        List<Film> films = (List<Film>) filmService.getAll();

        assertEquals(3, films.size());
        assertTrue(films.stream().anyMatch(f -> f.getId() == film1.getId()));
        assertTrue(films.stream().anyMatch(f -> f.getId() == film2.getId()));
        assertTrue(films.stream().anyMatch(f -> f.getId() == film3.getId()));
    }

    @Test
    void getById_ShouldReturnCorrectFilm() {
        Film retrievedFilm = filmService.getById(film1.getId());

        assertNotNull(retrievedFilm);
        assertEquals(film1.getId(), retrievedFilm.getId());
        assertEquals("Film 1", retrievedFilm.getName());
    }

    @Test
    void getById_ShouldThrowExceptionWhenFilmNotFound() {
        assertThrows(NotFoundException.class, () -> filmService.getById(999));
    }

    @Test
    void addLike_ShouldAddLikeWhenFilmAndUserExist() {
        filmService.addLike(film1.getId(), user1.getId());

        assertEquals(1, filmService.getLikesCount(film1.getId()));
    }

    @Test
    void addLike_ShouldThrowExceptionWhenFilmNotFound() {
        assertThrows(NotFoundException.class, () -> filmService.addLike(999, user1.getId()));
    }

    @Test
    void addLike_ShouldThrowExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> filmService.addLike(film1.getId(), 999));
    }

    @Test
    void addLike_ShouldThrowExceptionWhenDuplicateLike() {
        filmService.addLike(film1.getId(), user1.getId());

        assertThrows(IllegalArgumentException.class, () -> filmService.addLike(film1.getId(), user1.getId()));
    }

    @Test
    void removeLike_ShouldRemoveExistingLike() {
        filmService.addLike(film1.getId(), user1.getId());
        assertEquals(1, filmService.getLikesCount(film1.getId()));

        filmService.removeLike(film1.getId(), user1.getId());

        assertEquals(0, filmService.getLikesCount(film1.getId()));
    }

    @Test
    void removeLike_ShouldDoNothingWhenLikeNotExists() {
        filmService.removeLike(film1.getId(), user1.getId());

        assertEquals(0, filmService.getLikesCount(film1.getId()));
    }

    @Test
    void getPopularFilms_ShouldReturnFilmsSortedByLikes() {
        filmService.addLike(film1.getId(), user1.getId()); // film1: 1 like
        filmService.addLike(film2.getId(), user1.getId()); // film2: 1 like
        filmService.addLike(film2.getId(), user2.getId()); // film2: 2 likes
        filmService.addLike(film3.getId(), user1.getId()); // film3: 1 like
        filmService.addLike(film3.getId(), user2.getId()); // film3: 2 likes

        List<Film> popularFilms = filmService.getPopularFilms(2);

        assertEquals(2, popularFilms.size());
        assertEquals(film2.getId(), popularFilms.get(0).getId()); // Most likes
        assertEquals(film3.getId(), popularFilms.get(1).getId()); // Second most likes
    }

    @Test
    void getPopularFilms_ShouldReturnDefaultCountWhenNegative() {
        List<Film> popularFilms = filmService.getPopularFilms(-1);

        assertEquals(3, popularFilms.size()); // All films since no likes
    }

    @Test
    void getLikesCount_ShouldReturnCorrectCount() {
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());

        int likesCount = filmService.getLikesCount(film1.getId());

        assertEquals(2, likesCount);
    }

    @Test
    void getLikesCount_ShouldReturnZeroForFilmWithoutLikes() {
        int likesCount = filmService.getLikesCount(film1.getId());

        assertEquals(0, likesCount);
    }

    @Test
    void getPopularFilms_ShouldReturnAllFilmsWhenCountGreaterThanTotal() {
        List<Film> popularFilms = filmService.getPopularFilms(10);

        assertEquals(3, popularFilms.size());
    }
}