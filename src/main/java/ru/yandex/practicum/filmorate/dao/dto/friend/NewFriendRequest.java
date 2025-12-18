package ru.yandex.practicum.filmorate.dao.dto.friend;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class NewFriendRequest {
    @NotNull(message = "ID пользователя не может быть null")
    @Positive(message = "ID пользователя должен быть положительным")
    private Long userId;

    @NotNull(message = "ID друга не может быть null")
    @Positive(message = "ID друга должен быть положительным")
    private Long friendId;

    @Pattern(regexp = "PENDING|confirmed|rejected",
            message = "Статус должен быть: PENDING, confirmed или rejected")
    private String status = "PENDING"; // Значение по умолчанию
}