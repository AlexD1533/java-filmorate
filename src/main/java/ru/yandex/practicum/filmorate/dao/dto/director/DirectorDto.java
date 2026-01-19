package ru.yandex.practicum.filmorate.dao.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DirectorDto {
    @NotNull(message = "ID обязателен")
    @Positive(message = "ID должен быть положительным")
    private Long id;

    @NotBlank(message = "Имя режиссёра не может быть пустым")
    private String name;
}
