package ru.yandex.practicum.filmorate.dao.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class NewLikeRequest {
    private Long filmId;

    private Long userId;
}