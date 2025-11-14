package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
@Component
public class FilmValidator {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Фильм: пустое название");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Фильм: описание длиннее 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Фильм: некорректная дата релиза {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Фильм: отрицательная или нулевая продолжительность {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}