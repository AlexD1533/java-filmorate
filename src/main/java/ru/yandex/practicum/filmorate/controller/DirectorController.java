package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<DirectorDto> getAll() {
        log.info("GET /directors - Запрос на получение всех режиссёров");
        long startTime = System.currentTimeMillis();

        Collection<DirectorDto> directors = directorService.getAll();

        long duration = System.currentTimeMillis() - startTime;
        log.info("GET /directors - Успешно получено {} режиссёров за {} мс",
                directors.size(), duration);
        return directors;
    }

    @GetMapping("/{id}")
    public DirectorDto getById(@PathVariable Long id) {
        log.info("GET /directors/{} - Запрос на получение режиссёра по ID", id);
        long startTime = System.currentTimeMillis();

        try {
            DirectorDto director = directorService.getById(id);
            long duration = System.currentTimeMillis() - startTime;
            log.info("GET /directors/{} - Режиссёр успешно найден: {} (id: {}). Время выполнения: {} мс",
                    id, director.getName(), id, duration);
            return director;
        } catch (Exception e) {
            log.error("GET /directors/{} - Ошибка при получении режиссёра: {}",
                    id, e.getMessage());
            throw e;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@Valid @RequestBody DirectorDto director) {
        log.info("POST /directors - Запрос на создание нового режиссёра: {}",
                director.getName());
        long startTime = System.currentTimeMillis();

        try {
            DirectorDto createdDirector = directorService.create(director);
            long duration = System.currentTimeMillis() - startTime;
            log.info("POST /directors - Режиссёр успешно создан: {} (id: {}). Время выполнения: {} мс",
                    createdDirector.getName(), createdDirector.getId(), duration);
            return createdDirector;
        } catch (Exception e) {
            log.error("POST /directors - Ошибка при создании режиссёра {}: {}",
                    director.getName(), e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody DirectorDto director) {
        log.info("PUT /directors - Запрос на обновление режиссёра с ID {}: {}",
                director.getId(), director.getName());
        long startTime = System.currentTimeMillis();

        try {
            DirectorDto updatedDirector = directorService.update(director);
            long duration = System.currentTimeMillis() - startTime;
            log.info("PUT /directors - Режиссёр успешно обновлён: {} (id: {}). Время выполнения: {} мс",
                    updatedDirector.getName(), updatedDirector.getId(), duration);
            return updatedDirector;
        } catch (Exception e) {
            log.error("PUT /directors - Ошибка при обновлении режиссёра с ID {}: {}",
                    director.getId(), e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("DELETE /directors/{} - Запрос на удаление режиссёра", id);
        long startTime = System.currentTimeMillis();

        try {
            directorService.delete(id);
            long duration = System.currentTimeMillis() - startTime;
            log.info("DELETE /directors/{} - Режиссёр успешно удалён. Время выполнения: {} мс",
                    id, duration);
        } catch (Exception e) {
            log.error("DELETE /directors/{} - Ошибка при удалении режиссёра: {}",
                    id, e.getMessage());
            throw e;
        }
    }
}