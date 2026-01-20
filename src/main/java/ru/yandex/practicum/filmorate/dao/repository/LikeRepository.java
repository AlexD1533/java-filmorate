package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Repository
public class LikeRepository extends BaseRepository<Like> {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_BY_FILM_ID_SQL = "SELECT * FROM likes WHERE film_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM likes WHERE user_id = ?";
    private static final String FIND_USER_IDS_BY_FILM_ID_SQL = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String INSERT_SQL = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_BY_IDS_SQL = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    public LikeRepository(JdbcTemplate jdbc, LikeRowMapper mapper) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbc;
    }

    public List<Like> findByFilmId(Long filmId) {
        return findMany(FIND_BY_FILM_ID_SQL, filmId);
    }

    public List<Like> findByUserId(Long userId) {
        return findMany(FIND_BY_USER_ID_SQL, userId);
    }

    public Set<Long> findUserIdsByFilmId(Long filmId) {
        List<Long> userIds = jdbcTemplate.queryForList(FIND_USER_IDS_BY_FILM_ID_SQL, Long.class, filmId);
        return new HashSet<>(userIds);
    }

    public Like save(Like like) {
        jdbcTemplate.update(INSERT_SQL, like.getFilmId(), like.getUserId());
        return like;
    }

    public boolean delete(Long filmId, Long userId) {
        int rowsDeleted = jdbcTemplate.update(DELETE_BY_IDS_SQL, filmId, userId);
        return rowsDeleted > 0;
    }

}