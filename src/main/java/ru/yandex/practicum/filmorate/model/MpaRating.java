package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class MpaRating {
    @NotNull(message = "ID рейтинга MPA обязателен")
    @Min(value = 1, message = "Рейтинг MPA должен быть от 1 до 5")
    @Max(value = 5, message = "Рейтинг MPA должен быть от 1 до 5")
    private Long id;
    private String name;
}