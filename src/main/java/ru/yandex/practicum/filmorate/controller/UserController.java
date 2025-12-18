package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dao.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dao.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated

public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest request) {
        log.info("Пользователь: запрос на создание {}", request);


        if (request.getName() == null || request.getName().isBlank()) {
            request.setName(request.getLogin());
        }

        UserDto createdUser = userService.create(request);
        log.info("Пользователь создан с id={}", createdUser.getId());
        return createdUser;
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest request) {  // Убрать @PathVariable
        log.info("Пользователь: запрос на обновление {}", request);


        if (request.getName() == null || request.getName().isBlank()) {
            request.setName(request.getLogin());
        }

        // Передать id из request
        UserDto updatedUser = userService.updateUser(request.getId(), request);
        log.info("Пользователь обновлён {}", updatedUser);
        return updatedUser;
    }

    @GetMapping
    public Collection<UserDto> getAll() {

        log.info("Пользователь: запрос на получение всех пользователей)");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Пользователь: запрос на получение по id={}", id);
        UserDto user = userService.getById(id);
        log.info("Найден пользователь: {}", user);
        return user;
    }
}