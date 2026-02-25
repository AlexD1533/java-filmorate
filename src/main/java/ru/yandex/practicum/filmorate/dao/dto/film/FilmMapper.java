package ru.yandex.practicum.filmorate.dao.dto.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public final class FilmMapper {

    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorRepository directorRepository;


    public Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(request.getMpa());
        film.setGenres(request.getGenres() != null ? request.getGenres() : new HashSet<>());
        film.setLikes(new HashSet<>());
        return film;
    }

    public FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(mpaService.getMpaById(film.getMpa()));

        Set<Genre> genres = genreService.getGenresByFilmId(film.getId());
        dto.setGenres(genres);

        Set<Director> directors = new HashSet<>(directorRepository.findDirectorsByFilmId(film.getId()));
        dto.setDirectors(directors);
        dto.setLikes(film.getLikes());
        dto.setCreationDate(LocalDate.now());
        return dto;
    }

    public Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasMpa()) {
            film.setMpa(request.getMpa());
        }
        if (request.hasGenres()) {
            film.setGenres(request.getGenres());
        }
        if (request.hasDirectors()) {
            film.setDirectors(request.getDirectors());
        }

        return film;
    }
}