package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class UserValidatorTest {

    private final UserValidator validator = new UserValidator();

    @Test
    void shouldPassValidUser() {
        User user = new User(0, "user@mail.ru", "user123", "Display Name",
                LocalDate.of(1990, 1, 1));

        assertThatCode(() -> validator.validate(user))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        User user = new User(0, " ", "user123", "Name",
                LocalDate.of(1990, 1, 1));

        assertThatThrownBy(() -> validator.validate(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void shouldFailWhenEmailHasNoAt() {
        User user = new User(0, "invalid.mail.ru", "user123", "Name",
                LocalDate.of(1990, 1, 1));

        assertThatThrownBy(() -> validator.validate(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        User user = new User(0, "user@mail.ru", " ", "Name",
                LocalDate.of(1990, 1, 1));

        assertThatThrownBy(() -> validator.validate(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Логин не может быть пустым и содержать пробелы");
    }

    @Test
    void shouldFailWhenLoginHasSpace() {
        User user = new User(0, "user@mail.ru", "user 123", "Name",
                LocalDate.of(1990, 1, 1));

        assertThatThrownBy(() -> validator.validate(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Логин не может быть пустым и содержать пробелы");
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() {
        User user = new User(0, "user@mail.ru", "user123", "Name",
                LocalDate.now().plusDays(1));

        assertThatThrownBy(() -> validator.validate(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Дата рождения не может быть в будущем");
    }

    @Test
    void shouldPassWhenNameIsNull() {
        User user = new User(0, "user@mail.ru", "user123", null,
                LocalDate.of(1990, 1, 1));

        assertThatCode(() -> validator.validate(user))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldPassWhenBirthdayIsToday() {
        User user = new User(0, "user@mail.ru", "user123", "Name",
                LocalDate.now());

        assertThatCode(() -> validator.validate(user))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldPassWhenBirthdayIsNull() {
        User user = new User(0, "user@mail.ru", "user123", "Name", null);

        assertThatCode(() -> validator.validate(user))
                .doesNotThrowAnyException();
    }
}