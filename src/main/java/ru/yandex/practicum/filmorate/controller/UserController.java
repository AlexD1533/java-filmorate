package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final UserValidator validator;
    private int nextId = 1;

    public UserController(UserValidator validator) {
        this.validator = validator;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Пользователь: запрос на создание {}", user);
        validator.validate(user);
        user.setId(nextId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь создан с id={}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Пользователь: запрос на обновление {}", user);
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id={} не найден", user.getId());
            throw new IllegalArgumentException("Пользователь с id=" + user.getId() + " не найден");
        }
        validator.validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлён {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Пользователь: запрос на получение всех ({} шт.)", users.size());
        return new ArrayList<>(users.values());
    }
}