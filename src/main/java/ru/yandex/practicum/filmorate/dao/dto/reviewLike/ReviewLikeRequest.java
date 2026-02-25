package ru.yandex.practicum.filmorate.dao.dto.reviewLike;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReviewLikeRequest {
    @NotNull(message = "ID отзыва не может быть null")
    @Positive(message = "ID отзыва должен быть положительным")
    private Long reviewId;

    @NotNull(message = "ID пользователя не может быть null")
    @Positive(message = "ID пользователя должен быть положительным")
    private Long userId;

    @NotNull(message = "Тип реакции обязателен (true - лайк, false - дизлайк)")
    private Boolean isLike;
}