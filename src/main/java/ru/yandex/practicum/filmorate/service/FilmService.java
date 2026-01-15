package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmMapper;
import ru.yandex.practicum.filmorate.dao.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dao.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
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
        Set<Long> genres = request.getGenres();
        film.setGenres(genres);
        genreService.saveByFilm(film.getId(), genres);

        return filmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        Film existingFilm = filmStorage.getById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм =" + request.getName() + " не найден"));

        Film updatedFilm = filmMapper.updateFilmFields(existingFilm, request);
        updatedFilm = filmStorage.update(updatedFilm);

        Set<Long> genres = request.getGenres();

        genreService.saveByFilm(updatedFilm.getId(), genres);

        return filmMapper.mapToFilmDto(updatedFilm);
    }

    public Collection<FilmDto> getAll() {
        return filmStorage.getAll().stream()
                .map(film -> updateCollections(film, film.getId()))
                .map(filmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getById(long id) {
        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        return filmMapper.mapToFilmDto(updateCollections(film, id));

    }

    public List<FilmDto> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count).stream()
                .map(film -> updateCollections(film, film.getId()))
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    public Film updateCollections(Film film, long id) {
        film.setGenres(genreService.getGenresIdByFilm(id));
        film.setLikes(likeService.getLikesIdsByFilm(id));
        return film;
    }


    public void deleteFilm(long id) {
        getById(id);
        boolean deleted = filmStorage.deleteFilm(id);
        if (!deleted) {
            throw new InternalServerException("Не удалось удалить фильм с id=" + id);
        }
    }
}