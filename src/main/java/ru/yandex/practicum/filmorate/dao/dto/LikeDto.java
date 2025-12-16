package ru.yandex.practicum.filmorate.dao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long filmId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long userId;
}