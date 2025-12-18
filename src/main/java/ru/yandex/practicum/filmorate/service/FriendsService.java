package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.friend.FriendDto;
import ru.yandex.practicum.filmorate.dao.dto.friend.FriendMapper;
import ru.yandex.practicum.filmorate.dao.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dao.repository.FriendRepository;
import ru.yandex.practicum.filmorate.dao.repository.UserRepository;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendsService {
    private final FriendRepository friendRepository;
    private final UserRepository userDbStorage;
    private final Validation validation;

    public FriendDto addFriend(long userId, long friendId) {
        validation.validateUserExists(userId);
        validation.validateUserExists(friendId);

        if (userId == friendId) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }

        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setStatus("NOT_CONFIRMED");

        return FriendMapper.mapToFriendDto(friendRepository.save(friend));
    }

    public void removeFriend(long userId, long friendId) {
        validation.validateUserExists(userId);
        validation.validateUserExists(friendId);

        Optional<Friend> existingFriendship = friendRepository.findById(userId, friendId);

        if (existingFriendship.isEmpty()) {
            log.debug("Friendship does not exist: {} -> {}, nothing to delete", userId, friendId);
            return;
        }

        boolean deleted = friendRepository.delete(userId, friendId);
        if (!deleted) {
            log.error("Failed to delete existing friendship: {} -> {}", userId, friendId);
            throw new IllegalArgumentException("Не удалось удалить дружбу");
        }

        log.info("Пользователь {} удалил друга {}", userId, friendId);
    }

    public List<UserDto> getFriends(long userId) {
        validation.validateUserExists(userId);
        return userDbStorage.getAllFriends(userId);
    }

    public FriendDto updateFriendshipStatus(long userId, long friendId, String newStatus) {
        validation.validateUserExists(userId);
        validation.validateUserExists(friendId);

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
        validation.validateUserExists(userId);
        return friendRepository.findByUserId(userId).size();
    }

    private boolean isValidStatus(String status) {
        return status != null &&
                (status.equals("PENDING") ||
                        status.equals("CONFIRMED")
                );
    }

    public List<UserDto> getCommonFriends(long userId, long otherUserId) {
        validation.validateUserExists(userId);
        validation.validateUserExists(otherUserId);

        List<UserDto> userFriends = getFriends(userId);
        List<UserDto> otherUserFriends = getFriends(otherUserId);

        Set<Long> userFriendIds = userFriends.stream()
                .map(UserDto::getId)
                .collect(Collectors.toSet());

        return otherUserFriends.stream()
                .filter(friend -> userFriendIds.contains(friend.getId()))
                .collect(Collectors.toList());
    }
}