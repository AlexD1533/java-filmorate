package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MpaDto> getMpa() {
        log.info("Рейтинг MPA: запрос на получение всех рейтингов");
        List<MpaDto> ratings = mpaService.getMpa();
        log.info("Найдено {} рейтингов MPA", ratings.size());
        return ratings;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MpaDto getMpaById(@PathVariable long id) {
        log.info("Рейтинг MPA: запрос на получение рейтинга по id={}", id);
        MpaDto mpa = mpaService.getMpaById(id);
        log.info("Найден рейтинг MPA: {}", mpa);
        return mpa;
    }
}