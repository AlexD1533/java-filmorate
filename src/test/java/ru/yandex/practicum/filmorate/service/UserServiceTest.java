package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private InMemoryUserStorage userStorage;

    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);

        // Создаем тестовых пользователей
        user1 = new User(0, "user1@email.com", "user1", "User One",
                LocalDate.of(1990, 1, 1));
        user2 = new User(0, "user2@email.com", "user2", "User Two",
                LocalDate.of(1991, 1, 1));
        user3 = new User(0, "user3@email.com", "user3", "User Three",
                LocalDate.of(1992, 1, 1));
        user4 = new User(0, "userqqq3@email.com", "user3", "User Three",
                LocalDate.of(1992, 1, 1));

        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);
        user3 = userStorage.create(user3);
        user4 = userStorage.create(user4);
    }

    @Test
    void addFriend_ShouldAddMutualFriendship() {
        // When
        userService.addFriend(user1.getId(), user2.getId());

        // Then
        List<User> user1Friends = userService.getFriends(user1.getId());
        List<User> user2Friends = userService.getFriends(user2.getId());

        assertEquals(1, user1Friends.size());
        assertEquals(1, user2Friends.size());
        assertEquals(user2.getId(), user1Friends.get(0).getId());
        assertEquals(user1.getId(), user2Friends.get(0).getId());
    }

    @Test
    void addFriend_ShouldThrowExceptionWhenUserNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> userService.addFriend(999, user1.getId()));
    }

    @Test
    void addFriend_ShouldThrowExceptionWhenFriendNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> userService.addFriend(user1.getId(), 999));
    }

    @Test
    void removeFriend_ShouldRemoveMutualFriendship() {
        // Given
        userService.addFriend(user1.getId(), user2.getId());
        assertEquals(1, userService.getFriends(user1.getId()).size());

        // When
        userService.removeFriend(user1.getId(), user2.getId());

        // Then
        assertEquals(0, userService.getFriends(user1.getId()).size());
        assertEquals(0, userService.getFriends(user2.getId()).size());
    }

    @Test
    void removeFriend_ShouldDoNothingWhenFriendshipNotExists() {
        // When
        userService.removeFriend(user1.getId(), user2.getId());

        // Then - No exception should be thrown
        assertEquals(0, userService.getFriends(user1.getId()).size());
    }

    @Test
    void getFriends_ShouldReturnEmptyListWhenNoFriends() {
        // When
        List<User> friends = userService.getFriends(user1.getId());

        // Then
        assertTrue(friends.isEmpty());
    }

    @Test
    void getFriends_ShouldReturnAllFriends() {
        // Given
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());

        // When
        List<User> friends = userService.getFriends(user1.getId());

        // Then
        assertEquals(2, friends.size());
        assertTrue(friends.stream().anyMatch(u -> u.getId() == user2.getId()));
        assertTrue(friends.stream().anyMatch(u -> u.getId() == user3.getId()));
    }

    @Test
    void getCommonFriends_ShouldReturnCommonFriends() {
        // Given
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());

        // When
        List<User> commonFriends = userService.getCommonFriends(user1.getId(), user2.getId());

        // Then
        assertEquals(1, commonFriends.size());
        assertEquals(user3.getId(), commonFriends.get(0).getId());
    }

    @Test
    void getCommonFriends_ShouldReturnEmptyListWhenNoCommonFriends() {
        // Given
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user3.getId(), user4.getId());

        // When
        List<User> commonFriends = userService.getCommonFriends(user1.getId(), user3.getId());

        // Then
        assertTrue(commonFriends.isEmpty());
    }

    @Test
    void getCommonFriends_ShouldThrowExceptionWhenUserNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(999, user1.getId()));
    }

    @Test
    void getCommonFriends_ShouldThrowExceptionWhenOtherUserNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(user1.getId(), 999));
    }

    @Test
    void addFriend_ShouldWorkWithMultipleFriends() {
        // Given
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());

        // When
        List<User> friends = userService.getFriends(user1.getId());

        // Then
        assertEquals(2, friends.size());
    }

    @Test
    void removeFriend_ShouldWorkWhenOnlyOneFriendRemains() {
        // Given
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());
        assertEquals(2, userService.getFriends(user1.getId()).size());

        // When
        userService.removeFriend(user1.getId(), user2.getId());

        // Then
        List<User> friends = userService.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(user3.getId(), friends.get(0).getId());
    }
}