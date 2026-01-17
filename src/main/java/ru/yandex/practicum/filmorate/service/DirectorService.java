package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dao.dto.director.DirectorMapper;
import ru.yandex.practicum.filmorate.dao.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dao.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.dao.repository.mappers.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final DirectorMapper directorMapper;

    public DirectorDto createDirector(NewDirectorRequest request) {
        Director director = directorMapper.toEntity(request);
        director = directorRepository.save(director);
        return directorMapper.toDto(director);
    }

    public DirectorDto updateDirector(UpdateDirectorRequest request) {
        // Проверяем существование режиссера
        Director existingDirector = directorRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Режиссер с id=" + request.getId() + " не найден"));

        Director updatedDirector = directorMapper.updateDirectorFields(existingDirector, request);
        updatedDirector = directorRepository.save(updatedDirector);
        return directorMapper.toDto(updatedDirector);
    }

    public List<DirectorDto> getAllDirectors() {
        return directorRepository.findAll().stream()
                .map(directorMapper::toDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getDirectorById(Long id) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id=" + id + " не найден"));
        return directorMapper.toDto(director);
    }

    public void deleteDirector(Long id) {
        // Проверяем существование перед удалением
        getDirectorById(id); // выбросит NotFoundException если не найден

        boolean deleted = directorRepository.delete(id);
        if (!deleted) {
            throw new NotFoundException("Режиссер с id=" + id + " не найден");
        }
    }

    // Метод для связи фильма с режиссерами
    public void linkFilmWithDirectors(Long filmId, List<Long> directorIds) {
        if (directorIds != null) {
            // Проверяем что все режиссеры существуют
            for (Long directorId : directorIds) {
                if (!directorRepository.findById(directorId).isPresent()) {
                    throw new NotFoundException("Режиссер с id=" + directorId + " не найден");
                }
            }
            directorRepository.linkFilmWithDirectors(filmId, directorIds);
        }
    }
    public Set<Director> getDirectorsByFilmId(Long filmId) {
        // Проверяем что фильм существует? (опционально)
        return directorRepository.findDirectorsByFilmId(filmId);
    }
}