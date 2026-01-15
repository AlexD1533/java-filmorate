package ru.yandex.practicum.filmorate.dao.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
public interface FilmStorage {


    Film create(Film film);

    Film update(Film film);

    Collection<Film> getAll();

    Optional<Film> getById(Long id);

    Optional<Film> findByNameAndReleaseDate(String name, LocalDate releaseDate);

    List<Film> getPopularFilms(int count);

    boolean validateId(long id);

    Collection<Film> getLikedFilmsByUserId(long userId);
}
