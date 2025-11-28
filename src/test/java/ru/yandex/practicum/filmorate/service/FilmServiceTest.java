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

        // Создаем тестовые фильмы
        film1 = new Film(0, "Film 1", "Description 1",
                LocalDate.of(2020, 1, 1), 120);
        film2 = new Film(0, "Film 2", "Description 2",
                LocalDate.of(2021, 1, 1), 130);
        film3 = new Film(0, "Film 3", "Description 3",
                LocalDate.of(2022, 1, 1), 140);

        film1 = filmStorage.create(film1);
        film2 = filmStorage.create(film2);
        film3 = filmStorage.create(film3);

        // Создаем тестовых пользователей
        user1 = new User(0, "user1@email.com", "user1", "User One",
                LocalDate.of(1990, 1, 1));
        user2 = new User(0, "user2@email.com", "user2", "User Two",
                LocalDate.of(1991, 1, 1));

        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);
    }

    @Test
    void addLike_ShouldAddLikeWhenFilmAndUserExist() {
        // When
        filmService.addLike(film1.getId(), user1.getId());

        // Then
        assertEquals(1, filmService.getLikesCount(film1.getId()));
    }

    @Test
    void addLike_ShouldThrowExceptionWhenFilmNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> filmService.addLike(999, user1.getId()));
    }

    @Test
    void addLike_ShouldThrowExceptionWhenUserNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> filmService.addLike(film1.getId(), 999));
    }

    @Test
    void addLike_ShouldThrowExceptionWhenDuplicateLike() {
        // Given
        filmService.addLike(film1.getId(), user1.getId());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> filmService.addLike(film1.getId(), user1.getId()));
    }

    @Test
    void removeLike_ShouldRemoveExistingLike() {
        // Given
        filmService.addLike(film1.getId(), user1.getId());
        assertEquals(1, filmService.getLikesCount(film1.getId()));

        // When
        filmService.removeLike(film1.getId(), user1.getId());

        // Then
        assertEquals(0, filmService.getLikesCount(film1.getId()));
    }

    @Test
    void removeLike_ShouldDoNothingWhenLikeNotExists() {
        // When
        filmService.removeLike(film1.getId(), user1.getId());

        // Then - No exception should be thrown
        assertEquals(0, filmService.getLikesCount(film1.getId()));
    }

    @Test
    void getPopularFilms_ShouldReturnFilmsSortedByLikes() {
        // Given
        filmService.addLike(film1.getId(), user1.getId()); // film1: 1 like
        filmService.addLike(film2.getId(), user1.getId()); // film2: 1 like
        filmService.addLike(film2.getId(), user2.getId()); // film2: 2 likes
        filmService.addLike(film3.getId(), user1.getId()); // film3: 1 like
        filmService.addLike(film3.getId(), user2.getId()); // film3: 2 likes

        // When
        List<Film> popularFilms = filmService.getPopularFilms(2);

        // Then
        assertEquals(2, popularFilms.size());
        assertEquals(film2.getId(), popularFilms.get(0).getId()); // Most likes
        assertEquals(film3.getId(), popularFilms.get(1).getId()); // Second most likes
    }

    @Test
    void getPopularFilms_ShouldReturnDefaultCountWhenNegative() {
        // When
        List<Film> popularFilms = filmService.getPopularFilms(-1);

        // Then
        assertEquals(3, popularFilms.size()); // All films since no likes
    }

    @Test
    void getLikesCount_ShouldReturnCorrectCount() {
        // Given
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());

        // When
        int likesCount = filmService.getLikesCount(film1.getId());

        // Then
        assertEquals(2, likesCount);
    }

    @Test
    void getLikesCount_ShouldReturnZeroForFilmWithoutLikes() {
        // When
        int likesCount = filmService.getLikesCount(film1.getId());

        // Then
        assertEquals(0, likesCount);
    }

    @Test
    void getPopularFilms_ShouldReturnAllFilmsWhenCountGreaterThanTotal() {
        // When
        List<Film> popularFilms = filmService.getPopularFilms(10);

        // Then
        assertEquals(3, popularFilms.size());
    }
}