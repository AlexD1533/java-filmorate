package ru.yandex.practicum.filmorate.dao.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @NotNull(message = "ID отзыва обязателен")
    @Positive(message = "ID отзыва должен быть положительным")
    private Long reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва обязателен")
    private Boolean isPositive;

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    public boolean hasIsPositive() {
        return isPositive != null;
    }
}