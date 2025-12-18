package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dao.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dao.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dao.dto.user.UserMapper;
import ru.yandex.practicum.filmorate.dao.repository.FriendRepository;
import ru.yandex.practicum.filmorate.dao.repository.UserRepository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userStorage;
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

        return UserMapper.mapToUserDto(updateCollections(updatedUser, updatedUser.getId()));
    }

    public Collection<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(user -> updateCollections(user, user.getId()))
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getById(long id) {

        User user = userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
        return UserMapper.mapToUserDto(updateCollections(user, id));
    }

    public User updateCollections (User user, long userId) {
        user.setFriends(friendRepository.findByUserId(userId));
        return user;
    }

}