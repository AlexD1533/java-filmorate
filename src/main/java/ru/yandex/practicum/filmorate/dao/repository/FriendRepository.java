package ru.yandex.practicum.filmorate.dao.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendRepository extends BaseRepository<Friend> {
    private final JdbcTemplate jdbcTemplate;

    // SQL константы
    private static final String FIND_BY_ID_SQL = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM friends WHERE user_id = ?";
    private static final String FIND_BY_FRIEND_ID_SQL = "SELECT * FROM friends WHERE friend_id = ?";
    private static final String FIND_MUTUAL_SQL = """
        SELECT f1.* FROM friends f1
        INNER JOIN friends f2 ON f1.user_id = f2.friend_id AND f1.friend_id = f2.user_id
        WHERE f1.user_id = ? AND f2.user_id = ?
        """;
    private static final String INSERT_SQL = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
    private static final String UPDATE_STATUS_SQL = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_BY_IDS_SQL = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_BY_USER_ID_SQL = "DELETE FROM friends WHERE user_id = ?";
    private static final String EXISTS_SQL = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String COUNT_FRIENDS_SQL = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND status = 'CONFIRMED'";

    public FriendRepository(JdbcTemplate jdbc, FriendRowMapper mapper) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbc;
    }

    public Optional<Friend> findById(Integer userId, Integer friendId) {
        return findOne(FIND_BY_ID_SQL, userId, friendId);
    }

    public List<Friend> findByUserId(Integer userId) {
        return findMany(FIND_BY_USER_ID_SQL, userId);
    }

    public List<Friend> findByFriendId(Integer friendId) {
        return findMany(FIND_BY_FRIEND_ID_SQL, friendId);
    }

    public List<Friend> findMutualFriends(Integer userId, Integer otherUserId) {
        return findMany(FIND_MUTUAL_SQL, userId, otherUserId);
    }

    public Set<Integer> findFriendIdsByUserId(Integer userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ? AND status = 'CONFIRMED'";
        List<Integer> friendIds = jdbcTemplate.queryForList(sql, Integer.class, userId);
        return new HashSet<>(friendIds);
    }

    public Friend save(Friend friend) {
        if (exists(friend.getUserId(), friend.getFriendId())) {
            jdbcTemplate.update(UPDATE_STATUS_SQL,
                    friend.getStatus(),
                    friend.getUserId(),
                    friend.getFriendId());
        } else {
            jdbcTemplate.update(INSERT_SQL,
                    friend.getUserId(),
                    friend.getFriendId(),
                    friend.getStatus());
        }
        return friend;
    }

    public boolean delete(Integer userId, Integer friendId) {
        int rowsDeleted = jdbcTemplate.update(DELETE_BY_IDS_SQL, userId, friendId);
        return rowsDeleted > 0;
    }

    public void deleteByUserId(Integer userId) {
        jdbcTemplate.update(DELETE_BY_USER_ID_SQL, userId);
    }

    public boolean exists(Integer userId, Integer friendId) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_SQL, Integer.class, userId, friendId);
        return count != null && count > 0;
    }

    public int countFriends(Integer userId) {
        Integer count = jdbcTemplate.queryForObject(COUNT_FRIENDS_SQL, Integer.class, userId);
        return count != null ? count : 0;
    }
}