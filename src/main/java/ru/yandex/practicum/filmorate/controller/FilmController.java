package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.FilmDto;
import ru.yandex.practicum.filmorate.dao.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dao.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmValidator validator;
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@RequestBody NewFilmRequest request) {  // Убрать @RequestParam
        log.info("Фильм: запрос на создание {}", request);
        validator.validate(request);

        FilmDto createdFilm = filmService.create(request);
        log.info("Фильм создан с id={}", createdFilm.getId());
        return createdFilm;
    }

    @PutMapping
    public FilmDto update(@RequestBody UpdateFilmRequest request) {  // Убрать @RequestParam
        log.info("Фильм: запрос на обновление {}", request);

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
            @RequestParam(defaultValue = "10") int count) {
        log.info("Фильм: запрос на получение {} самых популярных фильмов", count);
        List<FilmDto> popularFilms = filmService.getPopularFilms(count);
        log.info("Найдено {} популярных фильмов", popularFilms.size());
        return popularFilms;
    }
}