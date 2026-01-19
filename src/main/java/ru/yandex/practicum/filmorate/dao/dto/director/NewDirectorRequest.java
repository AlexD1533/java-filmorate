package ru.yandex.practicum.filmorate.dao.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.stereotype.Component;


@Data
public class NewDirectorRequest {

    @NotBlank(message = "Имя режиссёра не может быть пустым")
    private String name;
}


