package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private final FilmValidator validator;
    private int nextId = 1;

    public FilmController(FilmValidator validator) {
        this.validator = validator;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Фильм: запрос на создание {}", film);
        validator.validate(film);
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Фильм создан с id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Фильм: запрос на обновление {}", film);
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id={} не найден", film.getId());
            throw new IllegalArgumentException("Фильм с id=" + film.getId() + " не найден");
        }
        validator.validate(film);
        films.put(film.getId(), film);
        log.info("Фильм обновлён {}", film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Фильм: запрос на получение всех ({} шт.)", films.size());
        return new ArrayList<>(films.values());
    }
}