package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> {
    private final JdbcTemplate jdbcTemplate;
    private static final String FIND_BY_ID_SQL = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String FIND_BY_FILM_ID_SQL = """
        SELECT * FROM reviews 
        WHERE film_id = ? 
        ORDER BY useful DESC 
        LIMIT ?
        """;
    private static final String INSERT_SQL = """
        INSERT INTO reviews (content, is_positive, user_id, film_id, useful, creation_date) 
        VALUES (?, ?, ?, ?, ?, ?)
        """;
    private static final String UPDATE_SQL = """
        UPDATE reviews 
        SET content = ?, is_positive = ?, useful = ? 
        WHERE review_id = ?
        """;
    private static final String DELETE_SQL = "DELETE FROM reviews WHERE review_id = ?";
    private static final String EXISTS_BY_ID_SQL = "SELECT COUNT(*) > 0 FROM reviews WHERE review_id = ?";

    public ReviewRepository(JdbcTemplate jdbc, ReviewRowMapper mapper) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbc;
    }

    public Review save(Review review) {
        if (review.getReviewId() == 0) {
            return create(review);
        } else {
            return update(review);
        }
    }

    public Review create(Review review) {
        String sql = INSERT_SQL;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.isPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setLong(5, review.getUseful());
            ps.setTimestamp(6, Timestamp.valueOf(review.getCreationDate()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            review.setReviewId(key.longValue());
        } else {
            throw new InternalServerException("Не удалось создать отзыв");
        }

        return review;
    }

    public Review update(Review review) {
        String sql = UPDATE_SQL;

        int rowsUpdated = jdbcTemplate.update(sql,
                review.getContent(),
                review.isPositive(),
                review.getUseful(),
                review.getReviewId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Отзыв с ID=" + review.getReviewId() + " не найден");
        }

        return review;
    }

    public Optional<Review> findById(Long reviewId) {
        return findOne(FIND_BY_ID_SQL, reviewId);
    }

    public void delete(Long reviewId) {
        boolean deleted = delete(DELETE_SQL, reviewId);
        if (!deleted) {
            throw new NotFoundException("Отзыв с ID=" + reviewId + " не найден");
        }
    }

    public List<Review> findByFilmId(Long filmId, Integer count) {
        String sql = FIND_BY_FILM_ID_SQL;
        return findMany(sql, filmId, count);
    }

    public List<Review> findAll(Integer count) {
        String sql = FIND_ALL_SQL;
        return findMany(sql, count);
    }

    public boolean existsById(Long reviewId) {
        return jdbcTemplate.queryForObject(EXISTS_BY_ID_SQL, Boolean.class, reviewId);
    }

    public void updateUseful(Long reviewId, Long useful) {
        String sql = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, useful, reviewId);
    }
}