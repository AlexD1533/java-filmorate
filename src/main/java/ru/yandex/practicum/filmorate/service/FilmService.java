package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final LikeService likeService;

    public FilmDto create(NewFilmRequest request) {
        Optional<Film> existingFilm = filmStorage.findByNameAndReleaseDate(
                request.getName(),
                request.getReleaseDate()
        );

        if (existingFilm.isPresent()) {
            throw new DuplicatedDataException("Фильм с таким названием и датой выпуска уже существует");
        }

        Film film = FilmMapper.mapToFilm(request);
        film = filmStorage.create(film);
        Set<Long> genres = request.getGenres();
        film.setGenres(genres);
        genreService.saveByFilm(film.getId(), genres);

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        Film existingFilm = filmStorage.findByNameAndReleaseDate(request.getName(), request.getReleaseDate())
                .orElseThrow(() -> new NotFoundException("Фильм =" + request.getName() + " не найден"));

        Film updatedFilm = FilmMapper.updateFilmFields(existingFilm, request);
        updatedFilm = filmStorage.update(updatedFilm);

        Set<Long> genres = updatedFilm.getGenres();

        updatedFilm.setGenres(genres);
        genreService.saveByFilm(updatedFilm.getId(), genres);

        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public Collection<FilmDto> getAll() {
        return filmStorage.getAll().stream()
                .map(film -> updateCollections(film, film.getId()))
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getById(long id) {
        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        return FilmMapper.mapToFilmDto(updateCollections(film, id));

    }

    public List<FilmDto> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count).stream()
                .map(film -> updateCollections(film, film.getId()))
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public Film updateCollections(Film film, long id) {
        film.setGenres(genreService.getGenresIdByFilm(id));
        film.setLikes(likeService.getLikesIdsByFilm(id));
        return film;
    }

}