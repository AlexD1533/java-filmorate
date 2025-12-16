package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {


    Film create(Film film);

    Film update(Film film);

    Collection<Film> getAll();

    Film getById(int id);
}
