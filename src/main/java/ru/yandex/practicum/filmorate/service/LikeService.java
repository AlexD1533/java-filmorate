package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dao.dto.like.LikeDto;
import ru.yandex.practicum.filmorate.dao.dto.like.LikeMapper;
import ru.yandex.practicum.filmorate.dao.repository.LikeRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final Validation validation;
    private final EventService eventService;

    public LikeDto addLike(long filmId, long userId) {
        validation.validateFilmExists(filmId);
        validation.validateUserExists(userId);

        Set<Long> userLikes = likeRepository.findUserIdsByFilmId(filmId);
        if (userLikes.contains(userId)) {
            Like like = new Like(filmId, userId);
            eventService.addEvent(
                    userId,
                    EventType.LIKE,
                    EventOperation.ADD,
                    filmId
            );
            return LikeMapper.mapToLikeDto(like);
        }

        Like like = new Like(filmId, userId);
        like = likeRepository.save(like);

        eventService.addEvent(
                userId,
                EventType.LIKE,
                EventOperation.ADD,
                filmId
        );

        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        return LikeMapper.mapToLikeDto(like);
    }

    public void removeLike(long filmId, long userId) {
        validation.validateFilmExists(filmId);
        validation.validateUserExists(userId);

        // Удаляем лайк
        boolean deleted = likeRepository.delete(filmId, userId);
        if (!deleted) {
            throw new NotFoundException("Лайк от пользователя " + userId + " фильму " + filmId + " не найден");
        }

        eventService.addEvent(
                userId,
                EventType.LIKE,
                EventOperation.REMOVE,
                filmId
        );

        log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }

    public Set<LikeDto> getLikesByFilmId(long filmId) {
        validation.validateFilmExists(filmId);

        List<Like> likes = likeRepository.findByFilmId(filmId);
        return likes.stream()
                .map(LikeMapper::mapToLikeDto)
                .collect(Collectors.toSet());
    }

    public Set<Long> getLikesIdsByFilm(long filmId) {
        return likeRepository.findUserIdsByFilmId(filmId);
    }

    public List<LikeDto> getLikesByUserId(long userId) {
        validation.validateUserExists(userId);

        List<Like> likes = likeRepository.findByUserId(userId);
        return likes.stream()
                .map(LikeMapper::mapToLikeDto)
                .collect(Collectors.toList());
    }
}