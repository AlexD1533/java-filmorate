
        package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.repository.FriendRepository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class FriendRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FriendRepository friendRepository;

    @BeforeEach
    void setUp() {
        FriendRowMapper mapper = new FriendRowMapper();
        friendRepository = new FriendRepository(jdbcTemplate, mapper);

        // Очистка таблиц перед каждым тестом
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM users");

        // Создание тестовых данных пользователей
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (1, 'user1@test.com', 'user1', 'User One', '1990-01-01')");
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (2, 'user2@test.com', 'user2', 'User Two', '1990-02-02')");
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (3, 'user3@test.com', 'user3', 'User Three', '1990-03-03')");

        // Создание тестовых данных друзей
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, status) VALUES (1, 2, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, status) VALUES (1, 3, 'PENDING')");
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, status) VALUES (2, 3, 'CONFIRMED')");
    }

    @Test
    void testFindById() {
        // Act
        Optional<Friend> friend = friendRepository.findById(1L, 2L);

        // Assert
        assertThat(friend).isPresent();
        assertThat(friend.get().getUserId()).isEqualTo(1L);
        assertThat(friend.get().getFriendId()).isEqualTo(2L);
        assertThat(friend.get().getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void testFindById_WhenNotFound() {
        // Act
        Optional<Friend> friend = friendRepository.findById(999L, 999L);

        // Assert
        assertThat(friend).isEmpty();
    }

    @Test
    void testFindByUserId() {
        // Act
        List<Friend> friends = friendRepository.findByUserId(1L);

        // Assert
        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(Friend::getUserId).containsOnly(1L);
        assertThat(friends).extracting(Friend::getFriendId).containsExactlyInAnyOrder(2L, 3L);
        assertThat(friends).extracting(Friend::getStatus).containsExactlyInAnyOrder("CONFIRMED", "PENDING");
    }

    @Test
    void testFindByUserId_WhenNoFriends() {
        // Act
        List<Friend> friends = friendRepository.findByUserId(3L);

        // Assert
        assertThat(friends).isEmpty();
    }

    @Test
    void testSave_NewFriend() {
        // Arrange
        Friend newFriend = new Friend(2L, 1L, "PENDING");

        // Act
        Friend savedFriend = friendRepository.save(newFriend);

        // Assert
        assertThat(savedFriend).isEqualTo(newFriend);

        // Проверяем, что друг действительно сохранен в БД
        Optional<Friend> retrievedFriend = friendRepository.findById(2L, 1L);
        assertThat(retrievedFriend).isPresent();
        assertThat(retrievedFriend.get().getStatus()).isEqualTo("PENDING");
    }

    @Test
    void testSave_UpdateExistingFriend() {
        // Arrange
        Friend existingFriend = new Friend(1L, 2L, "REJECTED");

        // Act
        Friend updatedFriend = friendRepository.save(existingFriend);

        // Assert
        assertThat(updatedFriend).isEqualTo(existingFriend);

        // Проверяем, что статус действительно обновлен в БД
        Optional<Friend> retrievedFriend = friendRepository.findById(1L, 2L);
        assertThat(retrievedFriend).isPresent();
        assertThat(retrievedFriend.get().getStatus()).isEqualTo("REJECTED");
    }

    @Test
    void testDelete() {
        // Act
        boolean isDeleted = friendRepository.delete(1L, 2L);

        // Assert
        assertThat(isDeleted).isTrue();

        // Проверяем, что запись действительно удалена из БД
        Optional<Friend> retrievedFriend = friendRepository.findById(1L, 2L);
        assertThat(retrievedFriend).isEmpty();

        // Проверяем, что другие записи остались
        List<Friend> remainingFriends = friendRepository.findByUserId(1L);
        assertThat(remainingFriends).hasSize(1);
    }

    @Test
    void testDelete_WhenNotFound() {
        // Act
        boolean isDeleted = friendRepository.delete(999L, 999L);

        // Assert
        assertThat(isDeleted).isFalse();
    }

    @Test
    void testExists() {
        // Act & Assert
        assertThat(friendRepository.exists(1L, 2L)).isTrue();
        assertThat(friendRepository.exists(2L, 1L)).isFalse(); // Не двусторонняя проверка
        assertThat(friendRepository.exists(999L, 999L)).isFalse();
    }
}
