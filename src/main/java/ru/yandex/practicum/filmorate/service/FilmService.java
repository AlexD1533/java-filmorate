package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dao.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dao.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dao.repository.UserRepository;
import ru.yandex.practicum.filmorate.dao.repository.UserStorage;
import ru.yandex.practicum.filmorate.dao.dto.film.FilmMapper;
import ru.yandex.practicum.filmorate.dao.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.repository.FilmStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final LikeService likeService;


    private final FilmMapper filmMapper;
    private final DirectorRepository directorRepository;
    private final DirectorService directorService;
    private final UserStorage userStorage;
    private final UserRepository userRepository;
    private final Validation validation;

    public FilmDto create(NewFilmRequest request) {

        Film film = filmMapper.mapToFilm(request);
        film = filmStorage.create(film);
        Set<Long> genres = request.getGenres();
        film.setGenres(genres);
        genreService.saveByFilm(film.getId(), genres);

        return filmMapper.mapToFilmDto(film);
    }

    @Transactional
    public FilmDto update(UpdateFilmRequest request) {
        Film existingFilm = filmStorage.getById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм =" + request.getName() + " не найден"));

        Film updatedFilm = filmMapper.updateFilmFields(existingFilm, request);
        updatedFilm = filmStorage.update(updatedFilm);

        Set<Long> genres = request.getGenres();
        updatedFilm.setGenres(genres);
        genreService.saveByFilm(updatedFilm.getId(), genres);

        Set<Long> directors = request.getDirectors();
        updatedFilm.setDirectors(directors);
        directorRepository.addDirectorsToFilm(request.getId(), directors);

        return filmMapper.mapToFilmDto(updatedFilm);
    }

    public Collection<FilmDto> getAll() {
        return filmStorage.getAll().stream()
                .map(this::updateCollections)
                .map(filmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getById(long id) {
        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        return filmMapper.mapToFilmDto(updateCollections(film));
    }


    public List<FilmDto> getPopularFilms(Integer genreId, Integer year, int count) {
        return filmStorage.getPopularFilms(genreId, year, count).stream()
                .map(this::updateCollections)
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    private Film updateCollections(Film film) {
        long id = film.getId();
        film.setGenres(genreService.getGenresIdByFilm(id));
        film.setLikes(likeService.getLikesIdsByFilm(id));
        film.setDirectors(directorService.getDirectorsIdsByFilm(id));
        return film;
    }

    public List<FilmDto> getFilmsByDirector(long directorId, String sortBy) {
        if (directorRepository.findById(directorId).isEmpty()) {
            throw new NotFoundException("Режиссера с id " + directorId + "не существует");
        }
        return filmStorage.findByDirectorIdSorted(directorId, sortBy).stream()
                .map(this::updateCollections) // чтобы заполнить жанры, лайки, режиссёров
                .map(filmMapper::mapToFilmDto)
                .toList();
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
                .map(this::updateCollections)
                .map(filmMapper::mapToFilmDto)
                .toList();
    }

    public List<FilmDto> getCommonFilms(long userId, long friendId) {
        validation.validateUserExists(userId);
        validation.validateUserExists(friendId);

        if (userId == friendId) {
            throw new ValidationException("ID пользователей должны быть разным");
        }

        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);
        return commonFilms.stream()
                .map(this::updateCollections)
                .map(filmMapper::mapToFilmDto)
                .toList();
    }
}