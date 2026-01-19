package ru.yandex.practicum.filmorate.dao.dto.film;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class NewFilmRequest {
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NotNull(message = "Дата выпуска обязательна")
    @PastOrPresent(message = "Дата выпуска не может быть в будущем")
    private LocalDate releaseDate;

    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    public boolean isValidReleaseDate() {
        return releaseDate == null || !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }

    @Positive(message = "Продолжительность должна быть положительной")
    private Integer duration;

    @NotNull(message = "Рейтинг MPA обязателен")
    @Valid
    private Long mpa;

    private Set<Long> genres = new HashSet<>();
    private Set<Long> directors = new HashSet<>();

    @JsonSetter("genres")
    public void setGenresFromMaps(Set<Map<String, Long>> genreMaps) {
        if (genreMaps != null) {
            this.genres = genreMaps.stream().map(map -> map.get("id"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
    }

    @JsonSetter("mpa")
    public void setMpaToLong(MpaRating mpa) {
        if (mpa != null) this.mpa = mpa.getId();
    }

    @JsonSetter("directors")
    public void setDirectorsFromMaps(Set<Map<String, Long>> directorMaps) {
        if (directorMaps != null) {
            this.directors = directorMaps.stream().map(map -> map.get("id"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
    }
}
