package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dao.dto.director.DirectorMapper;
import ru.yandex.practicum.filmorate.dao.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.dao.repository.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorMapper directorMapper;

    private final DirectorRepository directorRepository;

    public DirectorDto create(DirectorDto director) {
        Director directorNew = directorRepository.create(director);
        return directorMapper.mapToDirectorDto(directorNew);
    }

    public DirectorDto update(DirectorDto director) {
        Director directorUpdate = directorRepository.update(director);
        return directorMapper.mapToDirectorDto(directorUpdate);    }

    public void delete(Long id) {
        directorRepository.delete(id);
    }

    public Collection<DirectorDto> getAll() {
        return directorRepository.findAll().stream()
                .map(directorMapper::mapToDirectorDto)
                .toList();
    }

    public DirectorDto getById(Long id) {
       return directorRepository.findById(id)
               .map(directorMapper::mapToDirectorDto)
                .orElseThrow(() -> new IllegalArgumentException("Режиссёр с id=" + id + " не найден"));
    }
}
