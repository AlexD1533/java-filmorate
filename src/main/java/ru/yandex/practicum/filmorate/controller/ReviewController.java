package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dao.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dao.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto createReview(@Valid @RequestBody NewReviewRequest request) {
        log.info("POST /reviews - создание нового отзыва: {}", request);
        return reviewService.createReview(request);
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody UpdateReviewRequest request) {
        log.info("PUT /reviews - обновление отзыва: {}", request);
        return reviewService.updateReview(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable("id") Long reviewId) {
        log.info("DELETE /reviews/{} - удаление отзыва", reviewId);
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable("id") Long reviewId) {
        log.info("GET /reviews/{} - получение отзыва по ID", reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public List<ReviewDto> getReviews(
            @RequestParam(value = "filmId", required = false) Long filmId,
            @RequestParam(value = "count", defaultValue = "10") Integer count) {

        if (filmId != null) {
            log.info("GET /reviews?filmId={}&count={} - получение отзывов для фильма", filmId, count);
            return reviewService.getReviewsByFilmId(filmId, count);
        } else {
            log.info("GET /reviews?count={} - получение всех отзывов", count);
            return reviewService.getAllReviews(count);
        }
    }
}