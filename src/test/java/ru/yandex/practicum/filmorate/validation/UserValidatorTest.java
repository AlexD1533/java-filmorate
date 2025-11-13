package ru.yandex.practicum.filmorate.validation;


import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class UserValidatorTest {

    private final UserValidator validator = new UserValidator();

    @Test
    void shouldPassValidUser() {
        User user = User.builder()
                .email("user@mail.ru")
                .login("user123")
                .name("Display Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThatCode(() -> validator.validate(user))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        User user = User.builder()
                .email(" ")
                .login("user123")
                .name("Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThatThrownBy(() -> validator.validate(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void shouldFailWhenEmailHasNoAt() {
        User user = User.builder()
                .email("invalid.mail.ru")
                .login("user123")
                .name("Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThatThrownBy(() -> validator.validate(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        User user = User.builder()
                .email("user@mail.ru")
                .login(" ")
                .name("Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThatThrownBy(() -> validator.validate(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Логин не может быть пустым и содержать пробелы");
    }

    @Test
    void shouldFailWhenLoginHasSpace() {
        User user = User.builder()
                .email("user@mail.ru")
                .login("user 123")
                .name("Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThatThrownBy(() -> validator.validate(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Логин не может быть пустым и содержать пробелы");
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() {
        User user = User.builder()
                .email("user@mail.ru")
                .login("user123")
                .name("Name")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        assertThatThrownBy(() -> validator.validate(user))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Дата рождения не может быть в будущем");
    }
}