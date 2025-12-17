package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.GenreDto;
import ru.yandex.practicum.filmorate.dao.dto.GenreMapper;
import ru.yandex.practicum.filmorate.dao.repository.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<GenreDto> getGenres() {
        return genreRepository.findAll().stream()
                .map(GenreMapper::mapToGenreDto)
                .toList();
    }

    public GenreDto getGenreById(long genreId) {
        return genreRepository.findById(genreId)
                .map(GenreMapper::mapToGenreDto)
                .orElseThrow(() -> new NotFoundException("Жанр не найден с ID: " + genreId));
    }

    public Set<Long> getGenresIdByFilm (long filmId) {
        return genreRepository.findIdsByFilm(filmId);
    }

    public void saveByFilm(long id, Set<Long> genres) {
         genreRepository.saveGenresIdsByFilm(id, genres);
    }
}
