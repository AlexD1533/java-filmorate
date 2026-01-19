package ru.yandex.practicum.filmorate.dao.dto.film;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UpdateFilmRequest {

    @NotNull(message = "ID фильма обязателен")
    @Positive(message = "ID должен быть положительным")
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NotNull(message = "Дата выпуска обязательна")
    @PastOrPresent(message = "Дата выпуска не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной")
    private Integer duration;

    @Valid
    private Long mpa;

    private Set<Long> genres = new HashSet<>();
    private Set<Long> directors = new HashSet<>();

    @JsonSetter("genres")
    public void setGenresFromMaps(Set<Map<String, Long>> genreMaps)
    {
        if (genreMaps != null)
        {
            this.genres = genreMaps.stream()
                    .map(map -> map.get("id"))
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
        }
    }

    @JsonSetter("mpa")
    public void setMpaToLong(MpaRating mpa)
    {
        if (mpa != null)
        {
            this.mpa = mpa.getId();
        }
    }

    @JsonSetter("directors")
    public void setDirectorsFromMaps(Set<Map<String, Long>> directorMaps)
    {
        if (directorMaps != null)
        {
            this.directors = directorMaps.stream()
                    .map(map -> map.get("id"))
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
        }
    }

    public boolean hasName()
    {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription()
    {
        return description != null && !description.isBlank();
    }

    public boolean hasReleaseDate()
    {
        return releaseDate != null;
    }

    public boolean hasDuration()
    {
        return duration != null;
    }

    public boolean hasMpa()
    {
        return mpa != null;
    }

    public boolean hasGenres()
    {
        return genres != null && !genres.isEmpty();
    }

    public boolean hasDirectors()
    {
        return directors != null && !directors.isEmpty();
    }
}
