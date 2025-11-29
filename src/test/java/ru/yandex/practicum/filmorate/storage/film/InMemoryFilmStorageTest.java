package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;

    private Film film1;
    private Film film2;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();

        film1 = new Film(0, "Film 1", "Description 1",
                LocalDate.of(2020, 1, 1), 120);
        film2 = new Film(0, "Film 2", "Description 2",
                LocalDate.of(2021, 1, 1), 130);
    }

    @Test
    void create_ShouldAssignIdAndStoreFilm() {

        Film createdFilm = filmStorage.create(film1);

        assertNotNull(createdFilm);
        assertTrue(createdFilm.getId() > 0);
        assertEquals("Film 1", createdFilm.getName());

        Film retrievedFilm = filmStorage.getById(createdFilm.getId());
        assertEquals(createdFilm.getId(), retrievedFilm.getId());
    }

    @Test
    void create_ShouldAssignSequentialIds() {
        Film createdFilm1 = filmStorage.create(film1);
        Film createdFilm2 = filmStorage.create(film2);

        assertEquals(1, createdFilm1.getId());
        assertEquals(2, createdFilm2.getId());
    }

    @Test
    void update_ShouldUpdateExistingFilm() {
        Film createdFilm = filmStorage.create(film1);
        Film updatedFilm = new Film(createdFilm.getId(), "Updated Film", "Updated Description",
                LocalDate.of(2020, 1, 1), 150);

        Film result = filmStorage.update(updatedFilm);

        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(150, result.getDuration());

        Film retrievedFilm = filmStorage.getById(createdFilm.getId());
        assertEquals("Updated Film", retrievedFilm.getName());
    }

    @Test
    void update_ShouldThrowExceptionWhenFilmNotFound() {
        Film nonExistentFilm = new Film(999, "Non-existent", "Desc",
                LocalDate.of(2020, 1, 1), 120);

        assertThrows(NotFoundException.class, () -> filmStorage.update(nonExistentFilm));
    }

    @Test
    void getById_ShouldReturnCorrectFilm() {
        Film createdFilm = filmStorage.create(film1);

        Film retrievedFilm = filmStorage.getById(createdFilm.getId());

        assertNotNull(retrievedFilm);
        assertEquals(createdFilm.getId(), retrievedFilm.getId());
        assertEquals("Film 1", retrievedFilm.getName());
    }

    @Test
    void getById_ShouldThrowExceptionWhenFilmNotFound() {
        assertThrows(NotFoundException.class, () -> filmStorage.getById(999));
    }

    @Test
    void getAll_ShouldReturnAllFilms() {
        Film createdFilm1 = filmStorage.create(film1);
        Film createdFilm2 = filmStorage.create(film2);

        Collection<Film> films = filmStorage.getAll();

        assertEquals(2, films.size());
        assertTrue(films.stream().anyMatch(f -> f.getId() == createdFilm1.getId()));
        assertTrue(films.stream().anyMatch(f -> f.getId() == createdFilm2.getId()));
    }

    @Test
    void getAll_ShouldReturnEmptyCollectionWhenNoFilms() {
        Collection<Film> films = filmStorage.getAll();

        assertTrue(films.isEmpty());
    }

    @Test
    void create_ShouldNotModifyOriginalObject() {
        Film originalFilm = new Film(0, "Original", "Desc",
                LocalDate.of(2020, 1, 1), 120);

        Film createdFilm = filmStorage.create(originalFilm);

        assertEquals(1, originalFilm.getId());
        assertTrue(createdFilm.getId() > 0);
    }

    @Test
    void update_ShouldNotAffectOtherFilms() {
        Film createdFilm1 = filmStorage.create(film1);
        Film createdFilm2 = filmStorage.create(film2);

        Film updatedFilm1 = new Film(createdFilm1.getId(), "Updated Film 1", "Updated Desc 1",
                LocalDate.of(2020, 1, 1), 180);

        filmStorage.update(updatedFilm1);

        Film retrievedFilm1 = filmStorage.getById(createdFilm1.getId());
        Film retrievedFilm2 = filmStorage.getById(createdFilm2.getId());

        assertEquals("Updated Film 1", retrievedFilm1.getName());
        assertEquals("Film 2", retrievedFilm2.getName());
    }
}