package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Collection<User> getAll();

    Optional<User> getById(long id);

    Optional<User> findByEmail(String email);

    boolean validateId(long id);
}