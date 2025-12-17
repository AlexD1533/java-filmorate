package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dao.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dao.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserValidator validator;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody NewUserRequest request) {
        log.info("Пользователь: запрос на создание {}", request);
        validator.validate(request);

        if (request.getName() == null || request.getName().isBlank()) {
            request.setName(request.getLogin());
        }

        UserDto createdUser = userService.create(request);
        log.info("Пользователь создан с id={}", createdUser.getId());
        return createdUser;
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UpdateUserRequest request) {
        log.info("Пользователь: запрос на обновление {} с id={}", request, id);
        validator.validate(request);

        if (request.getName() == null || request.getName().isBlank()) {
            request.setName(request.getLogin());
        }

        UserDto updatedUser = userService.updateUser(id, request);
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