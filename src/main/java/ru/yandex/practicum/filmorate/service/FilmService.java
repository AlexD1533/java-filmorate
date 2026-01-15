package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmMapper;
import ru.yandex.practicum.filmorate.dao.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dao.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dao.repository.UserRepository;
import ru.yandex.practicum.filmorate.dao.repository.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.repository.FilmStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final LikeService likeService;
    private final UserRepository userRepository;
    private final FilmMapper filmMapper;
    private final UserStorage userStorage;

    public FilmDto create(NewFilmRequest request) {

        Film film = filmMapper.mapToFilm(request);
        film = filmStorage.create(film);
        Set<Long> genres = request.getGenres();
        film.setGenres(genres);
        genreService.saveByFilm(film.getId(), genres);

        return filmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        Film existingFilm = filmStorage.getById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм =" + request.getName() + " не найден"));

        Film updatedFilm = filmMapper.updateFilmFields(existingFilm, request);
        updatedFilm = filmStorage.update(updatedFilm);

        Set<Long> genres = request.getGenres();
        genreService.saveByFilm(updatedFilm.getId(), genres);

        return filmMapper.mapToFilmDto(updatedFilm);
    }

    public Collection<FilmDto> getAll() {
        return filmStorage.getAll().stream()
                .map(film -> updateCollections(film, film.getId()))
                .map(filmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getById(long id) {
        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        return filmMapper.mapToFilmDto(updateCollections(film, id));

    }

    public List<FilmDto> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count).stream()
                .map(film -> updateCollections(film, film.getId()))
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    public Film updateCollections(Film film, long id) {
        film.setGenres(genreService.getGenresIdByFilm(id));
        film.setLikes(likeService.getLikesIdsByFilm(id));
        return film;
    }

    public Map<Long, Collection<Film>> getLikedFilmsByAllUsers() {
        List<Long> allUsersIds = userRepository.getAll().stream()
                .map(User::getId)
                .toList();

        Map<Long, Collection<Film>> likedFilmsByAllUsers = new HashMap<>();
        for (Long id : allUsersIds) {
            likedFilmsByAllUsers.put(id, filmStorage.getLikedFilmsByUserId(id));
        }

        return likedFilmsByAllUsers;

    }

    public Collection<FilmDto> getRecommendations(long userId) {
        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Collection<Film> userLikedFilms = filmStorage.getLikedFilmsByUserId(userId);
        if (userLikedFilms.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Collection<Film>> likedFilmsByAllUsers = getLikedFilmsByAllUsers();
        Set<Film> userLikesSet = new HashSet<>(userLikedFilms);

        Optional<Map.Entry<Long, Collection<Film>>> targetUserEntry = likedFilmsByAllUsers.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(userId))
                .max(Comparator.comparingInt(entry -> {
                    Set<Film> otherLikesSet = new HashSet<>(entry.getValue());
                    Set<Film> intesection = new HashSet<>(userLikesSet);
                    intesection.retainAll(otherLikesSet);
                    return intesection.size();
                }));

        if (targetUserEntry.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<Film> targetUserLikedFilms = targetUserEntry.get().getValue();
        Set<Film> targetLikesSet = new HashSet<>(targetUserLikedFilms);

        targetLikesSet.removeAll(userLikedFilms);

        if (targetLikesSet.isEmpty()) {
            return Collections.emptyList();
        }

        return targetLikesSet.stream()
                .map(film -> updateCollections(film, film.getId()))
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

}