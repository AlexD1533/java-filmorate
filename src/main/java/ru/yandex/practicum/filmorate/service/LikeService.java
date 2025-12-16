package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dao.dto.LikeDto;
import ru.yandex.practicum.filmorate.dao.dto.LikeMapper;
import ru.yandex.practicum.filmorate.dao.repository.LikeRepository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.validation.ValidationExist;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final ValidationExist validationExist;

    public LikeDto addLike(long filmId, long userId) {
        // Проверяем существование фильма и пользователя
        validationExist.validateFilmExists(filmId);
        validationExist.validateUserExists(userId);

        Set<Long> userLikes = likeRepository.findUserIdsByFilmId(filmId);
        if (userLikes.contains(userId)) {
            throw new DuplicatedDataException("Пользователь " + userId + " уже поставил лайк фильму " + filmId);
        }

        // Создаем и сохраняем лайк
        Like like = new Like(filmId, userId);
        like = likeRepository.save(like);

        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        return LikeMapper.mapToLikeDto(like);
    }

    public void removeLike(long filmId, long userId) {
        // Проверяем существование фильма и пользователя
        validationExist.validateFilmExists(filmId);
        validationExist.validateUserExists(userId);

        // Удаляем лайк
        boolean deleted = likeRepository.delete(filmId, userId);
        if (!deleted) {
            throw new NotFoundException("Лайк от пользователя " + userId + " фильму " + filmId + " не найден");
        }

        log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }

    public List<LikeDto> getLikesByFilmId(long filmId) {
        validationExist.validateFilmExists(filmId);

        List<Like> likes = likeRepository.findByFilmId(filmId);
        return likes.stream()
                .map(LikeMapper::mapToLikeDto)
                .collect(Collectors.toList());
    }

    public List<LikeDto> getLikesByUserId(long userId) {
        validationExist.validateUserExists(userId);

        List<Like> likes = likeRepository.findByUserId(userId);
        return likes.stream()
                .map(LikeMapper::mapToLikeDto)
                .collect(Collectors.toList());
    }

    public Set<Long> getUserIdsWhoLikedFilm(long filmId) {
        validationExist.validateFilmExists(filmId);
        return likeRepository.findUserIdsByFilmId(filmId);
    }

    public Set<Long> getFilmIdsLikedByUser(long userId) {
        validationExist.validateUserExists(userId);
        return likeRepository.findFilmIdsByUserId(userId);
    }

    public int getLikesCountForFilm(long filmId) {
        validationExist.validateFilmExists(filmId);
        return likeRepository.countLikesByFilmId(filmId);
    }

    public boolean hasUserLikedFilm(long filmId, long userId) {
        return likeRepository.findUserIdsByFilmId(filmId).contains(userId);
    }

    public void deleteAllLikesForFilm(long filmId) {
        validationExist.validateFilmExists(filmId);
        likeRepository.deleteByFilmId(filmId);
        log.info("Удалены все лайки для фильма {}", filmId);
    }

    public void deleteAllLikesForUser(long userId) {
        validationExist.validateUserExists(userId);
        likeRepository.deleteByUserId(userId);
        log.info("Удалены все лайки пользователя {}", userId);
    }

}