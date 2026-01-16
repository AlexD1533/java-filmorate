package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dao.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dao.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.dao.dto.review.ReviewMapper;
import ru.yandex.practicum.filmorate.dao.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final FilmService filmService;
    private final ReviewLikeService reviewLikeService;

    public ReviewDto createReview(NewReviewRequest request) {
        // Проверяем существование пользователя и фильма
        userService.getById(request.getUserId()); // выбросит NotFoundException если не найден
        filmService.getById(request.getFilmId()); // выбросит NotFoundException если не найден

        Review review = reviewMapper.mapToReview(request);
        Review savedReview = reviewRepository.save(review);

        log.info("Создан отзыв ID={} для фильма ID={} от пользователя ID={}",
                savedReview.getReviewId(), savedReview.getFilmId(), savedReview.getUserId());

        return reviewMapper.mapToReviewDto(savedReview);
    }

    public ReviewDto updateReview(UpdateReviewRequest request) {
        Review existingReview = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв с ID=" + request.getReviewId() + " не найден"));

        Review updatedReview = reviewMapper.updateReviewFields(existingReview, request);
        Review savedReview = reviewRepository.update(updatedReview);

        log.info("Обновлен отзыв ID={}", savedReview.getReviewId());

        return reviewMapper.mapToReviewDto(savedReview);
    }

    public ReviewDto updateReview(Long reviewId, UpdateReviewRequest request) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID=" + reviewId + " не найден"));

        Review updatedReview = reviewMapper.updateReviewFields(existingReview, request);
        Review savedReview = reviewRepository.update(updatedReview);

        log.info("Обновлен отзыв ID={}", savedReview.getReviewId());

        return reviewMapper.mapToReviewDto(savedReview);
    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID=" + reviewId + " не найден"));

        reviewRepository.delete(reviewId);

        log.info("Удален отзыв ID={}", reviewId);
    }

    public ReviewDto getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID=" + reviewId + " не найден"));

        // Обновляем useful на основе актуальных лайков/дизлайков
        review.setUseful(reviewLikeService.getUseful(reviewId));

        return reviewMapper.mapToReviewDto(review);
    }

    public List<ReviewDto> getReviewsByFilmId(Long filmId, Integer count) {
        // Проверяем существование фильма
        filmService.getById(filmId);

        List<Review> reviews = reviewRepository.findByFilmId(filmId, count);

        // Обновляем useful для каждого отзыва
        reviews.forEach(review ->
                review.setUseful(reviewLikeService.getUseful(review.getReviewId())));

        return reviews.stream()
                .map(reviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getAllReviews(Integer count) {
        List<Review> reviews = reviewRepository.findAll(count);

        // Обновляем useful для каждого отзыва
        reviews.forEach(review ->
                review.setUseful(reviewLikeService.getUseful(review.getReviewId())));

        return reviews.stream()
                .map(reviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }
}