package ru.yandex.practicum.filmorate.dao.repository;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {

    Director create(Director director);

    Director update(Director director);

    void delete(Long id);

    Collection<Director> getAll();

    Optional<Director> getById(Long id);

}
