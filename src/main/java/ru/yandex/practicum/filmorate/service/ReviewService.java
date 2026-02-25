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
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

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
    private final EventService eventService;

    public ReviewDto createReview(NewReviewRequest request) {
        userService.getById(request.getUserId());
        filmService.getById(request.getFilmId());

        Review review = reviewMapper.mapToReview(request);
        Review savedReview = reviewRepository.save(review);

        eventService.addEvent(
                savedReview.getUserId(),
                EventType.REVIEW,
                EventOperation.ADD,
                savedReview.getReviewId()
        );

        log.info("Создан отзыв ID={} для фильма ID={} от пользователя ID={}",
                savedReview.getReviewId(), savedReview.getFilmId(), savedReview.getUserId());

        return reviewMapper.mapToReviewDto(savedReview);
    }

    public ReviewDto updateReview(UpdateReviewRequest request) {
        Review existingReview = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв с ID=" + request.getReviewId() + " не найден"));

        Review updatedReview = reviewMapper.updateReviewFields(existingReview, request);
        Review savedReview = reviewRepository.update(updatedReview);

        eventService.addEvent(
                savedReview.getUserId(),
                EventType.REVIEW,
                EventOperation.UPDATE,
                savedReview.getReviewId()
        );

        log.info("Обновлен отзыв ID={}", savedReview.getReviewId());

        return reviewMapper.mapToReviewDto(savedReview);
    }


    public void deleteReview(Long reviewId) {
        Review deletedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID=" + reviewId + " не найден"));

        reviewRepository.delete(reviewId);

        eventService.addEvent(
                deletedReview.getUserId(),
                EventType.REVIEW,
                EventOperation.REMOVE,
                reviewId
        );

        log.info("Удален отзыв ID={}", reviewId);
    }

    public ReviewDto getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID=" + reviewId + " не найден"));

        review.setUseful(reviewLikeService.getUseful(reviewId));

        return reviewMapper.mapToReviewDto(review);
    }

    public List<ReviewDto> getReviewsByFilmId(Long filmId, Integer count) {
        filmService.getById(filmId);

        List<Review> reviews = reviewRepository.findByFilmId(filmId, count);

        reviews.forEach(review ->
                review.setUseful(reviewLikeService.getUseful(review.getReviewId())));

        return reviews.stream()
                .map(reviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getAllReviews(Integer count) {
        List<Review> reviews = reviewRepository.findAll(count);

        reviews.forEach(review ->
                review.setUseful(reviewLikeService.getUseful(review.getReviewId())));

        return reviews.stream()
                .map(reviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }
}