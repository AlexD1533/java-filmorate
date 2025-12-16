package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.GenreMapper;
import ru.yandex.practicum.filmorate.dao.repository.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.GenreDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;


    public List<GenreDto> getGenres() {
        return genreRepository.findAll().stream()
                .map(GenreMapper::mapToGenreDto)
                .toList();
    }

    public GenreDto getUserById(long genreId) {
        return genreRepository.findById(genreId)
                .map(GenreMapper::mapToGenreDto)
                .orElseThrow(() -> new NotFoundException("Жанр не найден с ID: " + genreId));


    }
}
