package ru.yandex.practicum.filmorate.dao.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> getAll();

    Optional<Film> getById(Long id);

    Optional<Film> findByNameAndReleaseDate(String name, LocalDate releaseDate);

    List<Film> getPopularFilms(int count);

    boolean validateId(long id);

    // ===== новые методы для режиссёров =====

    void saveDirectors(long filmId, Set<Long> directorIds);

    Set<Long> getDirectorsByFilm(long filmId);

    // ===== метод для получения фильмов по режиссёру =====

    List<Film> getFilmsByDirector(long directorId, String sortBy);

    List<Film> findByDirectorIdSorted(Long directorId, String sortBy);
}
