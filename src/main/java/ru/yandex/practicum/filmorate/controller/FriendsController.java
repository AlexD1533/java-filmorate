package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.FriendDto;
import ru.yandex.practicum.filmorate.dao.dto.UserDto;
import ru.yandex.practicum.filmorate.service.FriendsService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FriendsController {
    private final FriendsService friendsService;

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public FriendDto addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пользователь: запрос на добавление в друзья: {} -> {}", id, friendId);
        FriendDto friendDto = friendsService.addFriend(id, friendId);
        log.info("Пользователи {} и {} теперь друзья", id, friendId);
        return friendDto;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пользователь: запрос на удаление из друзей: {} -> {}", id, friendId);
        friendsService.removeFriend(id, friendId);
        log.info("Пользователи {} и {} больше не друзья", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable long id) {
        log.info("Пользователь: запрос на получение списка друзей пользователя {}", id);
        List<UserDto> friends = friendsService.getFriends(id);
        log.info("Найдено {} друзей у пользователя {}", friends.size(), id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Пользователь: запрос на получение общих друзей пользователей {} и {}", id, otherId);
        List<UserDto> commonFriends = friendsService.getCommonFriends(id, otherId);
        log.info("Найдено {} общих друзей у пользователей {} и {}", commonFriends.size(), id, otherId);
        return commonFriends;
        throw new UnsupportedOperationException("Метод getCommonFriends еще не реализован");
    }
}