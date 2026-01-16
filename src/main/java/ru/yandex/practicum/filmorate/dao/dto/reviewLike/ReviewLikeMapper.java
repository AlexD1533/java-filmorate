package ru.yandex.practicum.filmorate.dao.dto.reviewLike;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.ReviewLike;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReviewLikeMapper {

    public static ReviewLike mapToReviewLike(ReviewLikeRequest request) {
        return new ReviewLike(
                request.getReviewId(),
                request.getUserId(),
                request.getIsLike()
        );
    }

    public static ReviewLikeDto mapToReviewLikeDto(ReviewLike reviewLike) {
        return new ReviewLikeDto(
                reviewLike.getReviewId(),
                reviewLike.getUserId(),
                reviewLike.getIsLike()
        );
    }
}