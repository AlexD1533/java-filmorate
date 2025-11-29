package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public void addFriend(int userId, int friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friendId);
        }
        if (friends.containsKey(friendId)) {
            friends.get(friendId).remove(userId);
        }
    }

    public List<User> getFriends(int userId) {
        validateUserExists(userId);

        Set<Integer> friendIds = friends.getOrDefault(userId, Collections.emptySet());
        return friendIds.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        validateUserExists(userId);
        validateUserExists(otherUserId);

        Set<Integer> userFriends = friends.getOrDefault(userId, Collections.emptySet());
        Set<Integer> otherUserFriends = friends.getOrDefault(otherUserId, Collections.emptySet());

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    private void validateUserExists(int userId) {
        if (!userStorage.getAll().stream().anyMatch(user -> user.getId() == userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }
}