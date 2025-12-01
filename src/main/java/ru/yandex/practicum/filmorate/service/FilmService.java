package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public void addLike(int filmId, int userId) {
        validateFilmExists(filmId);

        Set<Integer> filmLikes = likes.computeIfAbsent(filmId, k -> new HashSet<>());
        if (filmLikes.contains(userId)) {
            throw new IllegalArgumentException("Пользователь " + userId + " уже поставил лайк фильму " + filmId);
        }
        if (!userStorage.getUsersMap().containsKey(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не существует и не может поставить лайк фильму" + filmId);
        }

        filmLikes.add(userId);
    }

    public void removeLike(int filmId, int userId) {
        validateFilmExists(filmId);

        if (!userStorage.getUsersMap().containsKey(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не существует и не может поставить лайк фильму" + filmId);
        }

        if (likes.containsKey(filmId)) {
            likes.get(filmId).remove(userId);
        }
    }

    public int getLikesCount(int filmId) {
        validateFilmExists(filmId);
        return likes.getOrDefault(filmId, Collections.emptySet()).size();
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            count = 10;
        }

        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> {
                    int likes1 = getLikesCount(f1.getId());
                    int likes2 = getLikesCount(f2.getId());
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilmExists(int filmId) {
        if (!filmStorage.getFilmsMap().containsKey(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
    }
}