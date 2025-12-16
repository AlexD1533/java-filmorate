package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.dto.UserDto;
import ru.yandex.practicum.filmorate.dao.dto.UserMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_ID_EXIST = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)";

    private static final String FIND_ALL_FRIENDS =
            """ 
            SELECT u.* FROM users AS u
            LEFT JOIN friends AS f ON u.user_id = f.friend_id
            WHERE f.user_id = ?""";

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?) returning user_id";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ? WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }


    
    @Override
    public Collection<User> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> getById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }


    @Override
    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
               user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getId()
        );
        return user;
    }


    @Override
    public Optional<User> findByEmail(String email) {

            return findOne(FIND_BY_EMAIL_QUERY, email);
        }

    public List<UserDto> getAllFriends(long userId) {
        return findMany(FIND_ALL_FRIENDS).stream()
                .map(UserMapper::mapToUserDto)
                .toList();


    }

@Override
public boolean validateId(long id) {
        return existsById(FIND_ID_EXIST, id);
    }

    }

