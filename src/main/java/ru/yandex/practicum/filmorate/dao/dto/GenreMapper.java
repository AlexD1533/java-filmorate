package ru.yandex.practicum.filmorate.dao.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenreDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenreMapper {

    public static Genre mapToGenre(GenreDto request) {
        Genre genre = new Genre();
        genre.setName(request.getName());
        return genre;
    }

    public static GenreDto mapToGenreDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

}