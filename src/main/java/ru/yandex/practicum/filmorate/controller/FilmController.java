package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmValidator validator;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmValidator validator, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.validator = validator;
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Фильм: запрос на создание {}", film);
        validator.validate(film);
        Film createdFilm = filmStorage.create(film);
        log.info("Фильм создан с id={}", createdFilm.getId());
        return createdFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Фильм: запрос на обновление {}", film);
        validator.validate(film);
        Film updatedFilm = filmStorage.update(film);
        log.info("Фильм обновлён {}", updatedFilm);
        return updatedFilm;
    }

    @GetMapping
    public Collection<Film> getAll() {
        Collection<Film> films = filmStorage.getAll();
        log.info("Фильм: запрос на получение всех ({} шт.)", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        log.info("Фильм: запрос на получение по id={}", id);
        Film film = filmStorage.getById(id);
        log.info("Найден фильм: {}", film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Фильм: запрос на добавление лайка фильму {} от пользователя {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Фильм: запрос на удаление лайка с фильма {} от пользователя {}", id, userId);
        filmService.removeLike(id, userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        log.info("Фильм: запрос на получение {} самых популярных фильмов", count);
        List<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Найдено {} популярных фильмов", popularFilms.size());
        return popularFilms;
    }
}