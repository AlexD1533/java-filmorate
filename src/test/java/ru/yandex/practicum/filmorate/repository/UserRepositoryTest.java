
        package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.repository.UserRepository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class UserRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        UserRowMapper mapper = new UserRowMapper();
        userRepository = new UserRepository(jdbcTemplate, mapper);

        // Очистка таблиц перед каждым тестом
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM friends");

        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES ('user1@test.com', 'user1', 'User One', '1990-01-01')");
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES ('user2@test.com', 'user2', 'User Two', '1990-02-02')");
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES ('user3@test.com', 'user3', 'User Three', '1990-03-03')");

        // Получаем ID созданных пользователей
        Long userId1 = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user1@test.com'", Long.class);
        Long userId2 = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user2@test.com'", Long.class);
        Long userId3 = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user3@test.com'", Long.class);

        // Добавляем друзей
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'CONFIRMED')", userId1, userId2);
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'PENDING')", userId1, userId3);
    }

    @Test
    void testCreate() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("newuser@test.com");
        newUser.setLogin("newuser");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 5, 5));

        // Act
        User createdUser = userRepository.create(newUser);

        // Assert
        assertThat(createdUser.getId()).isPositive();
        assertThat(createdUser.getEmail()).isEqualTo("newuser@test.com");
        assertThat(createdUser.getLogin()).isEqualTo("newuser");

        // Проверяем, что пользователь действительно сохранен в БД
        Optional<User> retrievedUser = userRepository.getById(createdUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmail()).isEqualTo("newuser@test.com");
    }

    @Test
    void testUpdate() {
        // Получаем ID существующего пользователя
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user1@test.com'", Long.class);

        // Arrange
        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setEmail("updated@test.com");
        userToUpdate.setLogin("updateduser");
        userToUpdate.setName("Updated User");
        userToUpdate.setBirthday(LocalDate.of(1991, 1, 1));

        // Act
        User updatedUser = userRepository.update(userToUpdate);

        // Assert
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.com");
        assertThat(updatedUser.getLogin()).isEqualTo("updateduser");
        assertThat(updatedUser.getName()).isEqualTo("Updated User");

        // Проверяем, что изменения действительно сохранены в БД
        Optional<User> retrievedUser = userRepository.getById(userId);
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmail()).isEqualTo("updated@test.com");
        assertThat(retrievedUser.get().getName()).isEqualTo("Updated User");
    }

    @Test
    void testGetAll() {
        // Act
        Collection<User> users = userRepository.getAll();

        // Assert
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder(
                        "user1@test.com",
                        "user2@test.com",
                        "user3@test.com"
                );
    }

    @Test
    void testGetById() {
        // Получаем ID существующего пользователя
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user1@test.com'", Long.class);

        // Act
        Optional<User> user = userRepository.getById(userId);

        // Assert
        assertThat(user).isPresent();
        assertThat(user.get().getId()).isEqualTo(userId);
        assertThat(user.get().getEmail()).isEqualTo("user1@test.com");
        assertThat(user.get().getLogin()).isEqualTo("user1");
        assertThat(user.get().getName()).isEqualTo("User One");
    }

    @Test
    void testGetById_WhenNotFound() {
        // Act
        Optional<User> user = userRepository.getById(999L);

        // Assert
        assertThat(user).isEmpty();
    }

    @Test
    void testFindByEmail() {
        // Act
        Optional<User> user = userRepository.findByEmail("user1@test.com");

        // Assert
        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("user1@test.com");
    }

    @Test
    void testFindByEmail_WhenNotFound() {
        // Act
        Optional<User> user = userRepository.findByEmail("nonexistent@test.com");

        // Assert
        assertThat(user).isEmpty();
    }

    @Test
    void testGetAllFriends() {
        // Получаем ID пользователя
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user1@test.com'", Long.class);

        // Act
        List<ru.yandex.practicum.filmorate.dao.dto.user.UserDto> friends = userRepository.getAllFriends(userId);

        // Assert
        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(ru.yandex.practicum.filmorate.dao.dto.user.UserDto::getEmail)
                .containsExactlyInAnyOrder("user2@test.com", "user3@test.com");
    }

    @Test
    void testGetAllFriends_WhenNoFriends() {
        // Получаем ID пользователя без друзей
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user2@test.com'", Long.class);

        // Act
        List<ru.yandex.practicum.filmorate.dao.dto.user.UserDto> friends = userRepository.getAllFriends(userId);

        // Assert
        assertThat(friends).isEmpty();
    }

    @Test
    void testValidateId() {
        // Получаем ID существующего пользователя
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = 'user1@test.com'", Long.class);

        // Act & Assert
        assertThat(userRepository.validateId(userId)).isTrue();
        assertThat(userRepository.validateId(999L)).isFalse();
    }

    @Test
    void testDeleteUser() {
        // Arrange - получим ID существующего пользователя
        Long userId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM users WHERE email = 'user1@test.com'",
                Long.class
        );

        // Act
        boolean isDeleted = userRepository.deleteUser(userId);

        // Assert
        assertThat(isDeleted).isTrue();

        // Проверяем, что пользователь действительно удален из БД
        Optional<User> retrievedUser = userRepository.getById(userId);
        assertThat(retrievedUser).isEmpty();

        // Проверяем, что остальные пользователи остались
        Collection<User> remainingUsers = userRepository.getAll();
        assertThat(remainingUsers).hasSize(2);
    }

    @Test
    void testDeleteUser_WhenNotFound() {
        // Act
        boolean isDeleted = userRepository.deleteUser(999L);

        // Assert
        assertThat(isDeleted).isFalse();
    }

    @Test
    void testDeleteUser_CascadeDeletesLikesAndFriends() {
        // Arrange
        // Создаем нового пользователя
        User newUser = new User();
        newUser.setEmail("cascade@test.com");
        newUser.setLogin("cascade");
        newUser.setName("Cascade User");
        newUser.setBirthday(LocalDate.of(1995, 5, 5));
        User createdUser = userRepository.create(newUser);
        Long userId = createdUser.getId();

        // Добавляем друзей
        Long friendId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM users WHERE email = 'user2@test.com'",
                Long.class
        );
        jdbcTemplate.update(
                "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'CONFIRMED')",
                userId, friendId
        );

        // Добавляем лайки
        Long filmId = jdbcTemplate.queryForObject(
                "SELECT film_id FROM films LIMIT 1",
                Long.class
        );
        jdbcTemplate.update(
                "INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                filmId, userId
        );

        // Act
        boolean isDeleted = userRepository.deleteUser(userId);

        // Assert
        assertThat(isDeleted).isTrue();

        // Проверяем, что дружбы удалены (CASCADE)
        Integer friendsCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friends WHERE user_id = ? OR friend_id = ?",
                Integer.class, userId, userId
        );
        assertThat(friendsCount).isZero();

        // Проверяем, что лайки удалены (CASCADE)
        Integer likesCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE user_id = ?",
                Integer.class, userId
        );
        assertThat(likesCount).isZero();
    }
}
