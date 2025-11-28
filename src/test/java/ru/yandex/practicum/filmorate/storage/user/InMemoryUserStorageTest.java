package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();

        user1 = new User(0, "user1@email.com", "user1", "User One",
                LocalDate.of(1990, 1, 1));
        user2 = new User(0, "user2@email.com", "user2", "User Two",
                LocalDate.of(1991, 1, 1));
    }

    @Test
    void create_ShouldAssignIdAndStoreUser() {

        User createdUser = userStorage.create(user1);

        assertNotNull(createdUser);
        assertTrue(createdUser.getId() > 0);
        assertEquals("user1", createdUser.getLogin());

        User retrievedUser = userStorage.getById(createdUser.getId());
        assertEquals(createdUser.getId(), retrievedUser.getId());
    }

    @Test
    void create_ShouldAssignSequentialIds() {

        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);

        assertEquals(1, createdUser1.getId());
        assertEquals(2, createdUser2.getId());
    }

    @Test
    void update_ShouldUpdateExistingUser() {

        User createdUser = userStorage.create(user1);
        User updatedUser = new User(createdUser.getId(), "updated@email.com", "updated",
                "Updated User", LocalDate.of(1990, 1, 1));

        User result = userStorage.update(updatedUser);

        assertEquals("updated", result.getLogin());
        assertEquals("updated@email.com", result.getEmail());
        assertEquals("Updated User", result.getName());


        User retrievedUser = userStorage.getById(createdUser.getId());
        assertEquals("updated", retrievedUser.getLogin());
    }

    @Test
    void update_ShouldThrowExceptionWhenUserNotFound() {

        User nonExistentUser = new User(999, "none@email.com", "none", "None",
                LocalDate.of(1990, 1, 1));


        assertThrows(NotFoundException.class, () -> userStorage.update(nonExistentUser));
    }

    @Test
    void getById_ShouldReturnCorrectUser() {

        User createdUser = userStorage.create(user1);

        User retrievedUser = userStorage.getById(createdUser.getId());

        assertNotNull(retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        assertEquals("user1", retrievedUser.getLogin());
    }

    @Test
    void getById_ShouldThrowExceptionWhenUserNotFound() {

        assertThrows(NotFoundException.class, () -> userStorage.getById(999));
    }

    @Test
    void getAll_ShouldReturnAllUsers() {

        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);

        Collection<User> users = userStorage.getAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getId() == createdUser1.getId()));
        assertTrue(users.stream().anyMatch(u -> u.getId() == createdUser2.getId()));
    }

    @Test
    void getAll_ShouldReturnEmptyCollectionWhenNoUsers() {

        Collection<User> users = userStorage.getAll();


        assertTrue(users.isEmpty());
    }

    @Test
    void getUsersMap_ShouldReturnCopyOfInternalMap() {

        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);

        Map<Integer, User> usersMap = userStorage.getUsersMap();

        assertEquals(2, usersMap.size());
        assertTrue(usersMap.containsKey(createdUser1.getId()));
        assertTrue(usersMap.containsKey(createdUser2.getId()));


        userStorage.create(new User(0, "new@email.com", "newuser", "New User",
                LocalDate.of(1995, 1, 1)));


        assertEquals(2, usersMap.size());
    }

    @Test
    void contains_ShouldReturnTrueWhenUserExists() {

        User createdUser = userStorage.create(user1);


        assertTrue(userStorage.contains(createdUser.getId()));
    }

    @Test
    void contains_ShouldReturnFalseWhenUserNotExists() {

        assertFalse(userStorage.contains(999));
    }

    @Test
    void create_ShouldNotModifyOriginalObject() {

        User originalUser = new User(0, "original@email.com", "original", "Original",
                LocalDate.of(1990, 1, 1));


        User createdUser = userStorage.create(originalUser);


        assertEquals(1, originalUser.getId());
        assertTrue(createdUser.getId() > 0);
    }

    @Test
    void update_ShouldNotAffectOtherUsers() {

        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);

        User updatedUser1 = new User(createdUser1.getId(), "updated@email.com", "updated",
                "Updated User", LocalDate.of(1990, 1, 1));

        userStorage.update(updatedUser1);

        User retrievedUser1 = userStorage.getById(createdUser1.getId());
        User retrievedUser2 = userStorage.getById(createdUser2.getId());

        assertEquals("updated", retrievedUser1.getLogin());
        assertEquals("user2", retrievedUser2.getLogin()); // Второй пользователь не изменился
    }
}