package ru.yandex.practicum.filmorate.dao.dto.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public final class ReviewMapper {

    public Review mapToReview(NewReviewRequest request) {
        Review review = new Review();
        review.setContent(request.getContent());
        review.setPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        review.setUseful(0L);
        review.setUserLikes(new HashSet<>());
        return review;
    }

    public ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.isPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());
        dto.setUserLikes(review.getUserLikes() != null ? review.getUserLikes() : new HashSet<>());
        dto.setCreationDate(LocalDateTime.now());
        return dto;
    }

    public Review updateReviewFields(Review review, UpdateReviewRequest request) {
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        if (request.getIsPositive() != null) {
            review.setPositive(request.getIsPositive());
        }
        return review;
    }
}