package ru.yandex.practicum.filmorate.dao.dto.director;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDirectorRequest {
    private Long id;
    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;
    // Вспомогательные методы для проверки наличия полей (как в UpdateFilmRequest)
    public boolean hasName() {
        return name != null && !name.isBlank();
    }
}