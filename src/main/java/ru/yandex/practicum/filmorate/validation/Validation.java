package ru.yandex.practicum.filmorate.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.dao.repository.FilmStorage;
import ru.yandex.practicum.filmorate.dao.repository.UserStorage;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class Validation{
    private  final UserStorage userStorage;
    private  final FilmStorage filmStorage;

    public void valivationMpa(long mpaId) {
        if (mpaId > 5 || mpaId < 1) {
            throw new NotFoundException("Выход за пределы рейтинга");
        }
    }

    public void validationGenre(Set<Long> genres) {
        for(Long g: genres) {
            if (g < 1 || g > 6) {
                throw new NotFoundException("Выход за пределы жанра");
            }
        }


    }

    public void validateUserExists(long userId) {
        if (!userStorage.validateId(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    public void validateFilmExists(long filmId) {
        if (!filmStorage.validateId(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
    }
}


