package ru.yandex.practicum.filmorate.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.dao.repository.FilmStorage;
import ru.yandex.practicum.filmorate.dao.repository.UserStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class Validation {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final DirectorRepository directorRepository;

    public void validationMpa(long mpaId) {
        if (mpaId > 5 || mpaId < 1) {
            throw new NotFoundException("Выход за пределы рейтинга");
        }
    }

    public void validationGenre(Set<Long> genres) {
        for (Long g : genres) {
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

    public void validateDirectorsSetExists(Set<Long> directors) {
        for (Long d : directors) {
            if (directorRepository.findById(d).isEmpty()) {
                throw new NotFoundException("Режиссер с id=" + d + " не найден");
            }
        }
    }


    public void validateDirectorExists(long directorId) {
        if (directorRepository.findById(directorId).isEmpty()) {
            throw new NotFoundException("Режиссер с id=" + directorId + " не найден");
        }
    }

    public void validateSearchParameters(Set<String> searchBy) {
        if (searchBy == null || searchBy.isEmpty()) {
            throw new ValidationException("Параметр 'by' должен содержать значение: title, или director");
        }

        for (String param : searchBy) {
            if (!param.equals("title") && !param.equals("director")) {
                throw new ValidationException(
                        "Недопустимое значение в параметре 'by': " + param + ". Допустимые значения: title, director"
                );
            }
        }
    }
}
