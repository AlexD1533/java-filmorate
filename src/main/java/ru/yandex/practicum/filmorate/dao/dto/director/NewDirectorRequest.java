package ru.yandex.practicum.filmorate.dao.dto.director;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class NewDirectorRequest {

    @NotBlank(message = "Имя режиссёра не может быть пустым")
    private String name;
}


