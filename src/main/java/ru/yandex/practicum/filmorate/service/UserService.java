package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.*;
import ru.yandex.practicum.filmorate.dao.repository.FriendRepository;
import ru.yandex.practicum.filmorate.dao.repository.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userStorage;
private final FriendRepository friendRepository;

    public UserDto create(NewUserRequest request) {
        Optional<User> alreadyExistUser = userStorage.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }

        User user = UserMapper.mapToUser(request);
        user = userStorage.create(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(long userId, UpdateUserRequest request) {
        User updatedUser = userStorage.getById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedUser = userStorage.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public UserDto getById(long id) {

        User user = userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
        List<Friend> friends = friendRepository.findByUserId(id);
        user.setFriends(friends);
        return UserMapper.mapToUserDto(user);
    }
}