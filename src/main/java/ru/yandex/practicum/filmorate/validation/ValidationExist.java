package ru.yandex.practicum.filmorate.validation;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@RequiredArgsConstructor
public class ValidationExist {
    private  final UserStorage userStorage;
    private  final FilmStorage filmStorage;


    public  void validateUserExists(long userId) {
        if (!userStorage.validateId(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    public  void validateFilmExists(long filmId) {
        if (!filmStorage.validateId(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
    }
}


