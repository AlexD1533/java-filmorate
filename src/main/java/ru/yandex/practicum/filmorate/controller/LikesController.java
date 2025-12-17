package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.dto.LikeDto;
import ru.yandex.practicum.filmorate.service.LikeService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class LikesController {
    private final LikeService likeService;

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Фильм: запрос на добавление лайка фильму {} от пользователя {}", id, userId);
        LikeDto likeDto = likeService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
        return likeDto;
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Фильм: запрос на удаление лайка с фильма {} от пользователя {}", id, userId);
        likeService.removeLike(id, userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, id);
    }

    @GetMapping("/{id}/likes")
    public List<LikeDto> getLikesByFilmId(@PathVariable long id) {
        log.info("Фильм: запрос на получение лайков фильма {}", id);
        List<LikeDto> likes = likeService.getLikesByFilmId(id);
        log.info("Найдено {} лайков для фильма {}", likes.size(), id);
        return likes;
    }
}