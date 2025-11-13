package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class FilmValidatorTest {

    private final FilmValidator validator = new FilmValidator();

    @Test
    void shouldPassValidFilm() {
        Film film = Film.builder()
                .name("Valid Film")
                .description("Short desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();

        assertThatCode(() -> validator.validate(film))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Film film = Film.builder()
                .name(" ")
                .description("Desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();

        assertThatThrownBy(() -> validator.validate(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Название фильма не может быть пустым");
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() {
        Film film = Film.builder()
                .name("Name")
                .description("a".repeat(201))
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();

        assertThatThrownBy(() -> validator.validate(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Максимальная длина описания — 200 символов");
    }

    @Test
    void shouldFailWhenReleaseDateIsTooEarly() {
        Film film = Film.builder()
                .name("Name")
                .description("Desc")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(120)
                .build();

        assertThatThrownBy(() -> validator.validate(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Дата релиза должна быть не раньше 28 декабря 1895 года");
    }

    @Test
    void shouldFailWhenDurationIsZeroOrNegative() {
        Film film = Film.builder()
                .name("Name")
                .description("Desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(0)
                .build();

        assertThatThrownBy(() -> validator.validate(film))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Продолжительность фильма должна быть положительной");
    }
}