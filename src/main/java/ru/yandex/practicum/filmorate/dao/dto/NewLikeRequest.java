package ru.yandex.practicum.filmorate.dao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class NewLikeRequest {
    @NotNull(message = "ID фильма не может быть null")
    @Positive(message = "ID фильма должен быть положительным")
    private Long filmId;

    @NotNull(message = "ID пользователя не может быть null")
    @Positive(message = "ID пользователя должен быть положительным")
    private Long userId;
}