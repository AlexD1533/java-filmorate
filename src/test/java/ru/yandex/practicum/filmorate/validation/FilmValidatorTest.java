package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class FilmValidatorTest {

    private final FilmValidator validator = new FilmValidator();

    @Test
    void shouldPassValidFilm() {
        Film film = new Film(0, "Valid Film", "Short desc",
                LocalDate.of(2000, 1, 1), 120);

        assertThatCode(() -> validator.validate(film))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Film film = new Film(0, " ", "Desc",
                LocalDate.of(2000, 1, 1), 120);

        assertThatThrownBy(() -> validator.validate(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Название фильма не может быть пустым");
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() {
        Film film = new Film(0, "Name", "a".repeat(201),
                LocalDate.of(2000, 1, 1), 120);

        assertThatThrownBy(() -> validator.validate(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Максимальная длина описания — 200 символов");
    }

    @Test
    void shouldFailWhenReleaseDateIsTooEarly() {
        Film film = new Film(0, "Name", "Desc",
                LocalDate.of(1895, 12, 27), 120);

        assertThatThrownBy(() -> validator.validate(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Дата релиза должна быть не раньше 28 декабря 1895 года");
    }

    @Test
    void shouldFailWhenDurationIsZero() {
        Film film = new Film(0, "Name", "Desc",
                LocalDate.of(2000, 1, 1), 0);

        assertThatThrownBy(() -> validator.validate(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Продолжительность фильма должна быть положительной");
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        Film film = new Film(0, "Name", "Desc",
                LocalDate.of(2000, 1, 1), -10);

        assertThatThrownBy(() -> validator.validate(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Продолжительность фильма должна быть положительной");
    }

    @Test
    void shouldPassWhenReleaseDateIsExactlyBoundary() {
        Film film = new Film(0, "Name", "Desc",
                LocalDate.of(1895, 12, 28), 120);

        assertThatCode(() -> validator.validate(film))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldPassWhenDescriptionIsExactly200Characters() {
        Film film = new Film(0, "Name", "a".repeat(200),
                LocalDate.of(2000, 1, 1), 120);

        assertThatCode(() -> validator.validate(film))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldPassWhenDurationIsPositive() {
        Film film = new Film(0, "Name", "Desc",
                LocalDate.of(2000, 1, 1), 1);

        assertThatCode(() -> validator.validate(film))
                .doesNotThrowAnyException();
    }
}