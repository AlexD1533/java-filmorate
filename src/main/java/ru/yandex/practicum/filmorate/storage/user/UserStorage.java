package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Collection<User> getAll();

    User getById(int id);
}