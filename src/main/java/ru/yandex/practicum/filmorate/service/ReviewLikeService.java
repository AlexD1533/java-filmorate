package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.ReviewLikeRepository;
import ru.yandex.practicum.filmorate.dao.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final EventService eventService;

    public void addReaction(Long reviewId, Long userId, boolean isLike) {
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID=" + reviewId + " не найден"));
        userService.getById(userId);

        Optional<ReviewLike> existingReactionOpt = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId);

        if (existingReactionOpt.isPresent()) {
            ReviewLike existingReaction = existingReactionOpt.get();
            Boolean existingIsLike = existingReaction.getIsLike();

            if (isLike == existingIsLike) {
                String reactionType = isLike ? "лайк" : "дизлайк";
                throw new DuplicatedDataException(
                        "Пользователь " + userId + " уже поставил " + reactionType + " отзыву " + reviewId);
            } else {
                updateReaction(reviewId, userId, isLike);
                String from = existingIsLike ? "лайк" : "дизлайк";
                String to = isLike ? "лайк" : "дизлайк";
                log.info("Пользователь {} сменил {} на {} для отзыва {}", userId, from, to, reviewId);
                return;
            }
        }

        ReviewLike reviewLike = new ReviewLike(reviewId, userId, isLike);
        reviewLikeRepository.save(reviewLike);
        updateReviewUseful(reviewId);

        String reactionType = isLike ? "лайк" : "дизлайк";
        log.info("Пользователь {} поставил {} отзыву {}", userId, reactionType, reviewId);
    }

    public void addLike(Long reviewId, Long userId) {
        addReaction(reviewId, userId, true);
    }

    public void addDislike(Long reviewId, Long userId) {
        addReaction(reviewId, userId, false);
    }

    public void removeLike(Long reviewId, Long userId) {
        removeReaction(reviewId, userId, true);

        eventService.addEvent(
                userId,
                EventType.LIKE,
                EventOperation.REMOVE,
                reviewId
        );

        log.info("Пользователь {} удалил лайк с отзыва {}", userId, reviewId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        removeReaction(reviewId, userId, false);
        log.info("Пользователь {} удалил дизлайк с отзыва {}", userId, reviewId);
    }

    private void removeReaction(Long reviewId, Long userId, Boolean expectedType) {
        ReviewLike existingReaction = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new NotFoundException(
                        "Реакция пользователя " + userId + " на отзыв " + reviewId + " не найдена"));

        if (!expectedType.equals(existingReaction.getIsLike())) {
            throw new NotFoundException(
                    "Ожидался " + (expectedType ? "лайк" : "дизлайк") +
                            ", но найдена другая реакция");
        }

        reviewLikeRepository.delete(reviewId, userId);

        updateReviewUseful(reviewId);
    }

    private void updateReaction(Long reviewId, Long userId, Boolean newIsLike) {
        ReviewLike reviewLike = new ReviewLike(reviewId, userId, newIsLike);
        reviewLikeRepository.update(reviewLike);

        updateReviewUseful(reviewId);
    }

    public Long getUseful(Long reviewId) {
        long likesCount = reviewLikeRepository.countLikes(reviewId);
        long dislikesCount = reviewLikeRepository.countDislikes(reviewId);
        return likesCount - dislikesCount;
    }

    private void updateReviewUseful(Long reviewId) {
        Long useful = getUseful(reviewId);
        reviewLikeRepository.updateReviewUseful(reviewId, useful);
    }

    public Optional<Boolean> getUserReaction(Long reviewId, Long userId) {
        return reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .map(ReviewLike::getIsLike);
    }
}