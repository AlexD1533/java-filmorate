
        package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.repository.LikeRepository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class LikeRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LikeRepository likeRepository;

    @BeforeEach
    void setUp() {
        LikeRowMapper mapper = new LikeRowMapper();
        likeRepository = new LikeRepository(jdbcTemplate, mapper);

        // Очистка таблиц перед каждым тестом
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

        // Создание тестовых данных
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (1, 'user1@test.com', 'user1', 'User One', '1990-01-01')");
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (2, 'user2@test.com', 'user2', 'User Two', '1990-02-02')");
        jdbcTemplate.update("INSERT INTO users (user_id, email, login, name, birthday) VALUES (3, 'user3@test.com', 'user3', 'User Three', '1990-03-03')");

        jdbcTemplate.update("INSERT INTO films (film_id, name, description, release_date, duration, rating_id) VALUES (1, 'Film One', 'Description One', '2000-01-01', 120, 1)");
        jdbcTemplate.update("INSERT INTO films (film_id, name, description, release_date, duration, rating_id) VALUES (2, 'Film Two', 'Description Two', '2001-01-01', 130, 2)");

        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (1, 2)");
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (2, 1)");
    }

    @Test
    void testFindByFilmId() {
        // Act
        List<Like> likes = likeRepository.findByFilmId(1L);

        // Assert
        assertThat(likes).hasSize(2);
        assertThat(likes).extracting(Like::getFilmId).containsOnly(1L);
        assertThat(likes).extracting(Like::getUserId).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void testFindByFilmId_WhenNoLikes() {
        // Act
        List<Like> likes = likeRepository.findByFilmId(999L);

        // Assert
        assertThat(likes).isEmpty();
    }

    @Test
    void testFindByUserId() {
        // Act
        List<Like> likes = likeRepository.findByUserId(1L);

        // Assert
        assertThat(likes).hasSize(2);
        assertThat(likes).extracting(Like::getFilmId).containsExactlyInAnyOrder(1L, 2L);
        assertThat(likes).extracting(Like::getUserId).containsOnly(1L);
    }

    @Test
    void testFindUserIdsByFilmId() {
        // Act
        Set<Long> userIds = likeRepository.findUserIdsByFilmId(1L);

        // Assert
        assertThat(userIds).hasSize(2);
        assertThat(userIds).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void testFindUserIdsByFilmId_WhenNoLikes() {
        // Act
        Set<Long> userIds = likeRepository.findUserIdsByFilmId(999L);

        // Assert
        assertThat(userIds).isEmpty();
    }

    @Test
    void testSave() {
        // Arrange
        Like newLike = new Like(2L, 3L);

        // Act
        Like savedLike = likeRepository.save(newLike);

        // Assert
        assertThat(savedLike).isEqualTo(newLike);

        // Проверяем, что лайк действительно сохранен в БД
        List<Like> likes = likeRepository.findByFilmId(2L);
        assertThat(likes).hasSize(2);
        assertThat(likes).extracting(Like::getUserId).contains(3L);
    }

    @Test
    void testDelete() {
        // Act
        boolean isDeleted = likeRepository.delete(1L, 1L);

        // Assert
        assertThat(isDeleted).isTrue();

        // Проверяем, что лайк действительно удален
        List<Like> likes = likeRepository.findByFilmId(1L);
        assertThat(likes).hasSize(1);
        assertThat(likes).extracting(Like::getUserId).containsOnly(2L);
    }

    @Test
    void testDelete_WhenNotFound() {
        // Act
        boolean isDeleted = likeRepository.delete(999L, 999L);

        // Assert
        assertThat(isDeleted).isFalse();
    }
}
