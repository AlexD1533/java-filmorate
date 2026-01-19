package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dao.dto.director.DirectorMapper;
import ru.yandex.practicum.filmorate.dao.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dao.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorMapper directorMapper;

    private final DirectorRepository directorRepository;

    public DirectorDto create(NewDirectorRequest director) {
        Director directorNew = directorRepository.create(director);
        return directorMapper.mapToDirectorDto(directorNew);
    }

    public DirectorDto update(DirectorDto director) {
        if (directorRepository.findById(director.getId()).isEmpty()) {
            throw new NotFoundException("Режиссера с id " + director.getId() + "не существует");
        }
        Director directorUpdate = directorRepository.update(director);
        return directorMapper.mapToDirectorDto(directorUpdate);
    }

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
                .orElseThrow(() -> new NotFoundException("Режиссёр с id=" + id + " не найден"));
    }

    public Set<Long> getDirectorsIdsByFilm(long id) {
        List<Director> directors = directorRepository.findDirectorsByFilmId(id);
        Set<Long> result = new HashSet<>();
        for (Director d : directors) {
            result.add(d.getId());
        }
        return result;
    }
}
