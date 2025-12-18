package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
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

        user1 = new User(0, "user1@email.com", "user1", "User One",
                LocalDate.of(1990, 1, 1));
        user2 = new User(0, "user2@email.com", "user2", "User Two",
                LocalDate.of(1991, 1, 1));
        user3 = new User(0, "user3@email.com", "user3", "User Three",
                LocalDate.of(1992, 1, 1));
        user4 = new User(0, "userqqq3@email.com", "user3", "User Three",
                LocalDate.of(1992, 1, 1));

        user1 = userService.create(user1);
        user2 = userService.create(user2);
        user3 = userService.create(user3);
        user4 = userService.create(user4);
    }

    @Test
    void create_ShouldCreateUser() {
        User newUser = new User(0, "new@email.com", "newuser", "New User",
                LocalDate.of(1995, 1, 1));

        User createdUser = userService.create(newUser);

        assertNotNull(createdUser);
        assertTrue(createdUser.getId() > 0);
        assertEquals("newuser", createdUser.getLogin());
    }

    @Test
    void update_ShouldUpdateUser() {
        User updatedUser = new User(user1.getId(), "updated@email.com", "updated",
                "Updated User", LocalDate.of(1990, 1, 1));

        User result = userService.update(updatedUser);

        assertEquals("updated", result.getLogin());
        assertEquals("updated@email.com", result.getEmail());
        assertEquals("Updated User", result.getName());
    }

    @Test
    void update_ShouldThrowExceptionWhenUserNotFound() {
        User nonExistentUser = new User(999, "none@email.com", "none", "None",
                LocalDate.of(1990, 1, 1));

        assertThrows(NotFoundException.class, () -> userService.update(nonExistentUser));
    }

    @Test
    void getAll_ShouldReturnAllUsers() {
        Collection<User> users = userService.getAll();

        assertEquals(4, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getId() == user1.getId()));
        assertTrue(users.stream().anyMatch(u -> u.getId() == user2.getId()));
        assertTrue(users.stream().anyMatch(u -> u.getId() == user3.getId()));
        assertTrue(users.stream().anyMatch(u -> u.getId() == user4.getId()));
    }

    @Test
    void getById_ShouldReturnCorrectUser() {
        User retrievedUser = userService.getById(user1.getId());

        assertNotNull(retrievedUser);
        assertEquals(user1.getId(), retrievedUser.getId());
        assertEquals("user1", retrievedUser.getLogin());
    }

    @Test
    void getById_ShouldThrowExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getById(999));
    }

    @Test
    void addFriend_ShouldAddMutualFriendship() {
        userService.addFriend(user1.getId(), user2.getId());

        List<User> user1Friends = userService.getFriends(user1.getId());
        List<User> user2Friends = userService.getFriends(user2.getId());

        assertEquals(1, user1Friends.size());
        assertEquals(1, user2Friends.size());
        assertEquals(user2.getId(), user1Friends.get(0).getId());
        assertEquals(user1.getId(), user2Friends.get(0).getId());
    }

    @Test
    void addFriend_ShouldThrowExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.addFriend(999, user1.getId()));
    }

    @Test
    void addFriend_ShouldThrowExceptionWhenFriendNotFound() {
        assertThrows(NotFoundException.class, () -> userService.addFriend(user1.getId(), 999));
    }

    @Test
    void removeFriend_ShouldRemoveMutualFriendship() {
        userService.addFriend(user1.getId(), user2.getId());
        assertEquals(1, userService.getFriends(user1.getId()).size());

        userService.removeFriend(user1.getId(), user2.getId());

        assertEquals(0, userService.getFriends(user1.getId()).size());
        assertEquals(0, userService.getFriends(user2.getId()).size());
    }

    @Test
    void removeFriend_ShouldDoNothingWhenFriendshipNotExists() {
        userService.removeFriend(user1.getId(), user2.getId());

        assertEquals(0, userService.getFriends(user1.getId()).size());
    }

    @Test
    void getFriends_ShouldReturnEmptyListWhenNoFriends() {
        List<User> friends = userService.getFriends(user1.getId());

        assertTrue(friends.isEmpty());
    }

    @Test
    void getFriends_ShouldReturnAllFriends() {
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());

        List<User> friends = userService.getFriends(user1.getId());

        assertEquals(2, friends.size());
        assertTrue(friends.stream().anyMatch(u -> u.getId() == user2.getId()));
        assertTrue(friends.stream().anyMatch(u -> u.getId() == user3.getId()));
    }

    @Test
    void getCommonFriends_ShouldReturnCommonFriends() {
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());

        List<User> commonFriends = userService.getCommonFriends(user1.getId(), user2.getId());

        assertEquals(1, commonFriends.size());
        assertEquals(user3.getId(), commonFriends.get(0).getId());
    }

    @Test
    void getCommonFriends_ShouldReturnEmptyListWhenNoCommonFriends() {
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user3.getId(), user4.getId());

        List<User> commonFriends = userService.getCommonFriends(user1.getId(), user3.getId());

        assertTrue(commonFriends.isEmpty());
    }

    @Test
    void getCommonFriends_ShouldThrowExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(999, user1.getId()));
    }

    @Test
    void getCommonFriends_ShouldThrowExceptionWhenOtherUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(user1.getId(), 999));
    }

    @Test
    void addFriend_ShouldWorkWithMultipleFriends() {
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());

        List<User> friends = userService.getFriends(user1.getId());

        assertEquals(2, friends.size());
    }

    @Test
    void removeFriend_ShouldWorkWhenOnlyOneFriendRemains() {
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());
        assertEquals(2, userService.getFriends(user1.getId()).size());

        userService.removeFriend(user1.getId(), user2.getId());

        List<User> friends = userService.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(user3.getId(), friends.get(0).getId());
    }
}