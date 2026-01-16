package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.ReviewLikeService;

@RestController
@RequestMapping("/reviews/{reviewId}")
@RequiredArgsConstructor
@Slf4j
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    @PutMapping("/like/{userId}")
    public void addLike(@PathVariable Long reviewId,
                        @PathVariable Long userId) {
        log.info("PUT /reviews/{}/like/{} - добавление лайка", reviewId, userId);
        reviewLikeService.addLike(reviewId, userId);
    }

    @PutMapping("/dislike/{userId}")
    public void addDislike(@PathVariable Long reviewId,
                           @PathVariable Long userId) {
        log.info("PUT /reviews/{}/dislike/{} - добавление дизлайка", reviewId, userId);
        reviewLikeService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable Long reviewId,
                           @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/like/{} - удаление лайка", reviewId, userId);
        reviewLikeService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDislike(@PathVariable Long reviewId,
                              @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/dislike/{} - удаление дизлайка", reviewId, userId);
        reviewLikeService.removeDislike(reviewId, userId);
    }
}