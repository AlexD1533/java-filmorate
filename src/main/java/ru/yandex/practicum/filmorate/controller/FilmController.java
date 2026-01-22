package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dao.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dao.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final Validation validation;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmRequest request) {
        log.info("Фильм: запрос на создание {}", request);
        validation.validationMpa(request.getMpa());
        validation.validationGenre(request.getGenres());
        validation.validateDirectorsSetExists(request.getDirectors());
        FilmDto createdFilm = filmService.create(request);
        log.info("Фильм создан с id={}", createdFilm.getId());
        return createdFilm;
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("Фильм: запрос на обновление {}", request);
        validation.validationMpa(request.getMpa());
        validation.validationGenre(request.getGenres());
        validation.validateDirectorsSetExists(request.getDirectors());
        FilmDto updatedFilm = filmService.update(request);
        log.info("Фильм обновлён {}", updatedFilm);
        return updatedFilm;
    }


    @GetMapping
    public Collection<FilmDto> getAll() {
        Collection<FilmDto> films = filmService.getAll();
        log.info("Фильм: запрос на получение всех ({} шт.)", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable long id) {
        log.info("Фильм: запрос на получение по id={}", id);
        FilmDto film = filmService.getById(id);
        log.info("Найден фильм: {}", film);
        return film;
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year
    ) {
        log.info(
                "Фильм: запрос популярных фильмов count={}, genreId={}, year={}",
                count, genreId, year
        );

        return filmService.getPopularFilms(genreId, year, count);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getFilmsByDirector(
            @PathVariable long directorId,
            @RequestParam(defaultValue = "likes") String sortBy
    ) {
        validation.validateDirectorExists(directorId);
        log.info("Фильм: запрос на получение фильмов режиссёра id={} с сортировкой по {}", directorId, sortBy);
        List<FilmDto> films = filmService.getFilmsByDirector(directorId, sortBy);
        log.info("Найдено {} фильмов режиссёра id={}", films.size(), directorId);
        return films;
    }

    @GetMapping("/common")
    public List<FilmDto> getCommonFilms(
            @RequestParam long userId,
            @RequestParam long friendId) {
        log.info("Запрос общих фильмов {} и {}", userId, friendId);
        List<FilmDto> commonFilms = filmService.getCommonFilms(userId, friendId);
        log.info("Найдено {} общих фильмов", commonFilms.size());
        return commonFilms;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable long id) {
        log.info("Фильм: запрос на удаление id={}", id);
        filmService.deleteFilm(id);
        log.info("Фильм с id={} удалён", id);
    }

    @GetMapping("/search")
    public List<FilmDto> searchFilms(
            @RequestParam String query,
            @RequestParam(defaultValue = "title,director,description") String by) {
        log.info("Поиск фильмов по запросу: '{}', параметры поиска: {}", query, by);

        Set<String> searchBy = Stream.of(by.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        validation.validateSearchParameters(searchBy);
        List<FilmDto> results = filmService.searchFilms(query, searchBy);
        log.info("Найдено {} фильмов по запросу '{}'", results.size(), query);
        return results;
    }
}