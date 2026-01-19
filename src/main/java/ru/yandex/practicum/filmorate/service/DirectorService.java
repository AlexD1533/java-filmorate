package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.dao.repository.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public void delete(Long id) {
        directorStorage.delete(id);
    }

    public Collection<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director getById(Long id) {
        return directorStorage.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Режиссёр с id=" + id + " не найден"));
    }
}
