package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GenreDto> getGenres() {
        log.info("Жанр: запрос на получение всех жанров");
        List<GenreDto> genres = genreService.getGenres();
        log.info("Найдено {} жанров", genres.size());
        return genres;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GenreDto getGenreById(@PathVariable long id) {
        log.info("Жанр: запрос на получение жанра по id={}", id);
        GenreDto genre = genreService.getGenreById(id);
        log.info("Найден жанр: {}", genre);
        return genre;
    }
}
