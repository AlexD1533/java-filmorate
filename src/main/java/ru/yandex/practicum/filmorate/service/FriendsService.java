package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.dto.FriendDto;
import ru.yandex.practicum.filmorate.dao.dto.FriendMapper;
import ru.yandex.practicum.filmorate.dao.dto.UserDto;
import ru.yandex.practicum.filmorate.dao.repository.FriendRepository;
import ru.yandex.practicum.filmorate.dao.repository.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.validation.ValidationExist;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendsService {
    private final FriendRepository friendRepository;
    private final UserDbStorage userDbStorage;
    private final ValidationExist validationExist;

    @Transactional
    public FriendDto addFriend(long userId, long friendId) {
        validationExist.validateUserExists(userId);
        validationExist.validateUserExists(friendId);

        if (userId == friendId) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }

        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setStatus("NOT_CONFIRMED"); // Исправлено на стандартный статус

        return FriendMapper.mapToFriendDto(friendRepository.save(friend));
    }

    @Transactional
    public void removeFriend(long userId, long friendId) {
        validationExist.validateUserExists(userId);
        validationExist.validateUserExists(friendId);

        boolean deleted = friendRepository.delete(userId, friendId);
        if (!deleted) {
            throw new IllegalArgumentException("Дружба не найдена");
        }
        log.info("Пользователь {} удалил друга {}", userId, friendId);
    }

    public List<UserDto> getFriends(long userId) {
        validationExist.validateUserExists(userId);
        return userDbStorage.getAllFriends(userId);
    }

    public FriendDto updateFriendshipStatus(long userId, long friendId, String newStatus) {
        validationExist.validateUserExists(userId);
        validationExist.validateUserExists(friendId);

        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Недопустимый статус: " + newStatus);
        }

        Optional<Friend> existingFriend = friendRepository.findById(userId, friendId);
        if (existingFriend.isEmpty()) {
            throw new IllegalArgumentException("Дружба не найдена");
        }

        Friend friend = existingFriend.get();
        friend.setStatus(newStatus);

        Friend updatedFriend = friendRepository.save(friend);
        log.info("Статус дружбы между {} и {} изменен на {}", userId, friendId, newStatus);

        return FriendMapper.mapToFriendDto(updatedFriend);
    }

    public int getFriendsCount(long userId) {
        validationExist.validateUserExists(userId);
        return friendRepository.findByUserId(userId).size();
    }

    private boolean isValidStatus(String status) {
        return status != null &&
                (status.equals("NOT_CONFIRMED") ||
                        status.equals("CONFIRMED")
                );
    }

}