package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.ReviewLikeRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class ReviewLikeRepository extends BaseRepository<ReviewLike> {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_BY_REVIEW_AND_USER_SQL =
            "SELECT * FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String INSERT_SQL =
            "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE review_likes SET is_like = ? WHERE review_id = ? AND user_id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String COUNT_LIKES_SQL =
            "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = true";
    private static final String COUNT_DISLIKES_SQL =
            "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = false";
    private static final String EXISTS_SQL =
            "SELECT COUNT(*) > 0 FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM review_likes WHERE review_id = ?";

    public ReviewLikeRepository(JdbcTemplate jdbc, ReviewLikeRowMapper mapper) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbc;
    }

    public Optional<ReviewLike> findByReviewIdAndUserId(Long reviewId, Long userId) {
        return findOne(FIND_BY_REVIEW_AND_USER_SQL, reviewId, userId);
    }

    public ReviewLike save(ReviewLike reviewLike) {
        if (exists(reviewLike.getReviewId(), reviewLike.getUserId())) {
            return update(reviewLike);
        } else {
            return create(reviewLike);
        }
    }

    public ReviewLike create(ReviewLike reviewLike) {
        String sql = INSERT_SQL;

        jdbcTemplate.update(sql,
                reviewLike.getReviewId(),
                reviewLike.getUserId(),
                reviewLike.getIsLike());

        return reviewLike;
    }

    public ReviewLike update(ReviewLike reviewLike) {
        String sql = UPDATE_SQL;

        int rowsUpdated = jdbcTemplate.update(sql,
                reviewLike.getIsLike(),
                reviewLike.getReviewId(),
                reviewLike.getUserId());

        if (rowsUpdated == 0) {
            throw new NotFoundException(
                    "Реакция на отзыв " + reviewLike.getReviewId() +
                            " от пользователя " + reviewLike.getUserId() + " не найдена");
        }

        return reviewLike;
    }

    public void delete(Long reviewId, Long userId) {
        boolean deleted = delete(DELETE_SQL, reviewId, userId);
        if (!deleted) {
            throw new NotFoundException(
                    "Реакция на отзыв " + reviewId +
                            " от пользователя " + userId + " не найдена");
        }
    }

    public long countLikes(Long reviewId) {
        Long count = jdbcTemplate.queryForObject(COUNT_LIKES_SQL, Long.class, reviewId);
        return count != null ? count : 0L;
    }

    public long countDislikes(Long reviewId) {
        Long count = jdbcTemplate.queryForObject(COUNT_DISLIKES_SQL, Long.class, reviewId);
        return count != null ? count : 0L;
    }

    public boolean exists(Long reviewId, Long userId) {
        return jdbcTemplate.queryForObject(EXISTS_SQL, Boolean.class, reviewId, userId);
    }

    public void updateReviewUseful(Long reviewId, Long useful) {
        String sql = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, useful, reviewId);
    }

    public Set<Long> findAllReviewLikes(Long reviewId) {
        List<Long> likesIds = jdbcTemplate.queryForList(FIND_BY_USER_ID_SQL, Long.class, reviewId);
        return new HashSet<>(likesIds);
    }

}