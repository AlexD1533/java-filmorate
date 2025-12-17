package ru.yandex.practicum.filmorate.dao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Set;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private int mpa;
    private Set<Long> genres;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Long> likes;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate creationDate;
}