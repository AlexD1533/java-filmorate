package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmMapper;
import ru.yandex.practicum.filmorate.dao.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dao.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dao.repository.mappers.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.repository.FilmStorage;

import java.util.ArrayList;
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
    private final DirectorService directorService;
    private final DirectorRepository directorRepository;

    private final FilmMapper filmMapper;

    public List<FilmDto> getFilmsByDirector(Long directorId, String sortBy) {
        // Проверить что режиссер существует
        directorService.getDirectorById(directorId);

        List<Long> filmIds;
        if ("likes".equals(sortBy)) {
            filmIds = directorRepository.findFilmIdsByDirectorSortedByLikes(directorId);
        } else if ("year".equals(sortBy)) {
            filmIds = directorRepository.findFilmIdsByDirectorSortedByYear(directorId);
        } else {
            throw new ValidationException("Параметр sortBy должен быть 'year' или 'likes'");
        }

        return filmIds.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public FilmDto create(NewFilmRequest request) {

        Film film = filmMapper.mapToFilm(request);
        film = filmStorage.create(film);
        Set<Long> genres = request.getGenres();
        film.setGenres(genres);
        genreService.saveByFilm(film.getId(), genres);

        Set<Long> directors = request.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            film.setDirectors(directors);
            directorService.linkFilmWithDirectors(film.getId(), new ArrayList<>(directors));
        }

        return filmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        Film existingFilm = filmStorage.getById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм =" + request.getName() + " не найден"));

        Film updatedFilm = filmMapper.updateFilmFields(existingFilm, request);
        updatedFilm = filmStorage.update(updatedFilm);

        Set<Long> genres = request.getGenres();
        genreService.saveByFilm(updatedFilm.getId(), genres);


        if (request.hasDirectors()) {
            Set<Long> directors = request.getDirectors();
            updatedFilm.setDirectors(directors);
            directorService.linkFilmWithDirectors(updatedFilm.getId(), new ArrayList<>(directors));
        }

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

}