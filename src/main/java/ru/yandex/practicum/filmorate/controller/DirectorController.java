package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dao.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dao.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<DirectorDto> getAllDirectors() {
        log.info("GET /directors - получение всех режиссеров");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public DirectorDto getDirectorById(@PathVariable Long id) {
        log.info("GET /directors/{} - получение режиссера по id", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto createDirector(@Valid @RequestBody NewDirectorRequest request) {
        log.info("POST /directors - создание режиссера: {}", request);
        DirectorDto createdDirector = directorService.createDirector(request);
        log.info("Создан режиссер с id={}", createdDirector.getId());
        return createdDirector;
    }

    @PutMapping
    public DirectorDto updateDirector(@Valid @RequestBody UpdateDirectorRequest request) {
        log.info("PUT /directors - обновление режиссера: {}", request);
        DirectorDto updatedDirector = directorService.updateDirector(request);
        log.info("Обновлен режиссер с id={}", updatedDirector.getId());
        return updatedDirector;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirector(@PathVariable Long id) {
        log.info("DELETE /directors/{} - удаление режиссера", id);
        directorService.deleteDirector(id);
        log.info("Режиссер с id={} удален", id);
    }
}