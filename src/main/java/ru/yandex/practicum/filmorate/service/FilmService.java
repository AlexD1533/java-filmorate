package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dao.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dao.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.repository.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final LikeService likeService;
    private final FilmMapper filmMapper;

    public FilmDto create(NewFilmRequest request) {
        Film film = filmMapper.mapToFilm(request);
        film = filmStorage.create(film);

        // Сохраняем жанры
        Set<Long> genres = request.getGenres();
        film.setGenres(genres);
        genreService.saveByFilm(film.getId(), genres);

        // Сохраняем режиссёров
        Set<Long> directors = request.getDirectors();
        film.setDirectors(directors);
        filmStorage.saveDirectors(film.getId(), directors);

        return filmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        Film existingFilm = filmStorage.getById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + request.getId() + " не найден"));

        Film updatedFilm = filmMapper.updateFilmFields(existingFilm, request);
        updatedFilm = filmStorage.update(updatedFilm);

        // Сохраняем жанры
        Set<Long> genres = request.getGenres();
        updatedFilm.setGenres(genres);
        genreService.saveByFilm(updatedFilm.getId(), genres);

        // Сохраняем режиссёров
        Set<Long> directors = request.getDirectors();
        updatedFilm.setDirectors(directors);
        filmStorage.saveDirectors(updatedFilm.getId(), directors);

        return filmMapper.mapToFilmDto(updatedFilm);
    }

    public Collection<FilmDto> getAll() {
        return filmStorage.getAll().stream()
                .map(this::updateCollections)
                .map(filmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getById(long id) {
        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        return filmMapper.mapToFilmDto(updateCollections(film));
    }

    public List<FilmDto> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count).stream()
                .map(this::updateCollections)
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    private Film updateCollections(Film film) {
        long id = film.getId();
        film.setGenres(genreService.getGenresIdByFilm(id));
        film.setLikes(likeService.getLikesIdsByFilm(id));
        film.setDirectors(filmStorage.getDirectorsByFilm(id)); // <- добавлено
        return film;
    }

    public List<FilmDto> getFilmsByDirector(long directorId, String sortBy) {
        return filmStorage.getFilmsByDirector(directorId, sortBy).stream()
                .map(this::updateCollections) // чтобы заполнить жанры, лайки, режиссёров
                .map(filmMapper::mapToFilmDto)
                .toList();
    }
}
