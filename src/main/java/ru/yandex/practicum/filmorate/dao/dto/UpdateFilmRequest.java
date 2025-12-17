package ru.yandex.practicum.filmorate.dao.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NotNull(message = "Дата выпуска обязательна")
    @PastOrPresent(message = "Дата выпуска не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной")
    private Integer duration;

    @Min(value = 1, message = "Рейтинг MPA должен быть от 1 до 5")
    @Max(value = 5, message = "Рейтинг MPA должен быть от 1 до 5")
    private Integer mpa;

    private Set<Long> genres;
}