package ru.yandex.practicum.filmorate.dao.repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.dao.repository.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;

import java.util.Optional;

@Repository
public class FriendRepository extends BaseRepository<Friend> {

    private static final String FIND_BY_ID_SQL = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM friends WHERE user_id = ?";
    private static final String INSERT_SQL = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
    private static final String UPDATE_STATUS_SQL = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_BY_IDS_SQL = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String EXISTS_SQL = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";

    public FriendRepository(JdbcTemplate jdbc, FriendRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Optional<Friend> findById(Long userId, Long friendId) {
        return findOne(FIND_BY_ID_SQL, userId, friendId);
    }

    public List<Friend> findByUserId(Long userId) {
        return findMany(FIND_BY_USER_ID_SQL, userId);
    }


    public Friend save(Friend friend) {
        if (exists(friend.getUserId(), friend.getFriendId())) {
            update(UPDATE_STATUS_SQL,
                    friend.getStatus(),
                    friend.getUserId(),
                    friend.getFriendId());
        } else {
            jdbc.update(INSERT_SQL,
                    friend.getUserId(),
                    friend.getFriendId(),
                    friend.getStatus());
        }
        return friend;
    }

    public boolean delete(Long userId, Long friendId) {
      return delete(DELETE_BY_IDS_SQL, userId, friendId);
    }


    public boolean exists(Long userId, Long friendId) {
        Integer count = jdbc.queryForObject(EXISTS_SQL, Integer.class, userId, friendId);
        return count != null && count > 0;
    }

}