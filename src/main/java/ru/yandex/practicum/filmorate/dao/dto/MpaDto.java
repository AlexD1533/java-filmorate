package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpaDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    private String name;
}