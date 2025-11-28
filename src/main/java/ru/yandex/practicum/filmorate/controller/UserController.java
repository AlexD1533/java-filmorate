package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserValidator validator;
    private final UserService userService;
    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Пользователь: запрос на создание {}", user);
        validator.validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User createdUser = userStorage.create(user);
        log.info("Пользователь создан с id={}", createdUser.getId());
        return createdUser;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Пользователь: запрос на обновление {}", user);
        validator.validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User updatedUser = userStorage.update(user);
        log.info("Пользователь обновлён {}", updatedUser);
        return updatedUser;
    }

    @GetMapping
    public Collection<User> getAll() {
        Collection<User> users = userStorage.getAll();
        log.info("Пользователь: запрос на получение всех ({} шт.)", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        log.info("Пользователь: запрос на получение по id={}", id);
        User user = userStorage.getById(id);
        log.info("Найден пользователь: {}", user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Пользователь: запрос на добавление в друзья: {} -> {}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователи {} и {} теперь друзья", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Пользователь: запрос на удаление из друзей: {} -> {}", id, friendId);
        userService.removeFriend(id, friendId);
        log.info("Пользователи {} и {} больше не друзья", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Пользователь: запрос на получение списка друзей пользователя {}", id);
        List<User> friends = userService.getFriends(id);
        log.info("Найдено {} друзей у пользователя {}", friends.size(), id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Пользователь: запрос на получение общих друзей пользователей {} и {}", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Найдено {} общих друзей у пользователей {} и {}", commonFriends.size(), id, otherId);
        return commonFriends;
    }
}