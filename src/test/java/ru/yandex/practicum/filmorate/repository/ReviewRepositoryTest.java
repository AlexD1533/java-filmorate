package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
class ReviewRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        reviewRepository = new ReviewRepository(jdbcTemplate, new ReviewRowMapper());

        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (1, 'user@mail.ru', 'user', 'User', '1990-01-01')"
        );

        jdbcTemplate.update(
                "INSERT INTO films (film_id, name, description, release_date, duration, rating_id) " +
                        "VALUES (1, 'Film', 'Desc', '2000-01-01', 120, 1)"
        );
    }

    @Test
    void testCreateReview() {
        Review review = buildReview("Good movie", true, 0);

        Review saved = reviewRepository.create(review);

        assertThat(saved.getReviewId()).isGreaterThan(0);

        Optional<Review> fromDb = reviewRepository.findById(saved.getReviewId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getContent()).isEqualTo("Good movie");
        assertThat(fromDb.get().isPositive()).isTrue();
    }

    @Test
    void testUpdateReview() {
        long reviewId = insertReview("Old text", true, 0);

        Review updated = new Review();
        updated.setReviewId(reviewId);
        updated.setContent("Updated text");
        updated.setPositive(false);
        updated.setUseful(5L);

        reviewRepository.update(updated);

        Review fromDb = reviewRepository.findById(reviewId).orElseThrow();
        assertThat(fromDb.getContent()).isEqualTo("Updated text");
        assertThat(fromDb.isPositive()).isFalse();
        assertThat(fromDb.getUseful()).isEqualTo(5L);
    }

    @Test
    void testSave_CreatePath() {
        Review review = buildReview("New", true, 0);

        Review saved = reviewRepository.save(review);

        assertThat(saved.getReviewId()).isGreaterThan(0);
    }

    @Test
    void testSave_UpdatePath() {
        long reviewId = insertReview("Text", true, 1);

        Review review = new Review();
        review.setReviewId(reviewId);
        review.setContent("Changed");
        review.setPositive(false);
        review.setUseful(10L);

        Review saved = reviewRepository.save(review);

        assertThat(saved.getReviewId()).isEqualTo(reviewId);
        assertThat(reviewRepository.findById(reviewId).get().getContent())
                .isEqualTo("Changed");
    }

    @Test
    void testFindById_WhenExists() {
        long reviewId = insertReview("Text", true, 3);

        Optional<Review> review = reviewRepository.findById(reviewId);

        assertThat(review).isPresent();
        assertThat(review.get().getUseful()).isEqualTo(3L);
    }

    @Test
    void testFindById_WhenNotExists() {
        Optional<Review> review = reviewRepository.findById(999L);
        assertThat(review).isEmpty();
    }

    @Test
    void testDeleteReview() {
        long reviewId = insertReview("Text", true, 0);

        reviewRepository.delete(reviewId);

        assertThat(reviewRepository.findById(reviewId)).isEmpty();
    }

    @Test
    void testDelete_WhenNotFound() {
        assertThatThrownBy(() -> reviewRepository.delete(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindByFilmId_SortedAndLimited() {
        insertReview("First", true, 1);
        insertReview("Second", true, 5);
        insertReview("Third", true, 3);

        List<Review> reviews = reviewRepository.findByFilmId(1L, 2);

        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getUseful()).isEqualTo(5L);
        assertThat(reviews.get(1).getUseful()).isEqualTo(3L);
    }

    @Test
    void testFindAll_SortedAndLimited() {
        insertReview("Low", true, 1);
        insertReview("High", true, 10);

        List<Review> reviews = reviewRepository.findAll(1);

        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getUseful()).isEqualTo(10L);
    }

    @Test
    void testExistsById() {
        long reviewId = insertReview("Text", true, 0);

        assertThat(reviewRepository.existsById(reviewId)).isTrue();
        assertThat(reviewRepository.existsById(999L)).isFalse();
    }

    @Test
    void testUpdateUseful() {
        long reviewId = insertReview("Text", true, 0);

        reviewRepository.updateUseful(reviewId, 7L);

        Review review = reviewRepository.findById(reviewId).orElseThrow();
        assertThat(review.getUseful()).isEqualTo(7L);
    }

    private long insertReview(String content, boolean positive, long useful) {
        Review review = buildReview(content, positive, useful);
        return reviewRepository.create(review).getReviewId();
    }

    private Review buildReview(String content, boolean positive, long useful) {
        Review review = new Review();
        review.setContent(content);
        review.setPositive(positive);
        review.setUserId(1L);
        review.setFilmId(1L);
        review.setUseful(useful);
        return review;
    }
}