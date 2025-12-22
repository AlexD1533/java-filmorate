package ru.yandex.practicum.filmorate.dao.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.dao.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaDto mpa;

    private Set<Genre> genres = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Long> likes = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate creationDate;


}