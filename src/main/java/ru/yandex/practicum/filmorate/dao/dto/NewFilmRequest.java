package ru.yandex.practicum.filmorate.dao.dto;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import java.time.LocalDate;
import java.util.Set;

@Data
public class NewFilmRequest {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private int mpa;
    private Set<Long> genres;
}