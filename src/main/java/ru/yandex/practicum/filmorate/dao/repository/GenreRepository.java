package ru.yandex.practicum.filmorate.dao.repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository

public class GenreRepository extends BaseRepository<Genre> {

    // SQL константы
    private static final String FIND_ALL_SQL = "SELECT * FROM genre ORDER BY genre_id";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String FIND_ALL_BY_FILM_ID_SQL = "SELECT genre_id FROM film_genre WHERE film_id = ?";

    public GenreRepository(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    // Существующие методы
    public List<Genre> findAll() {
        return findMany(FIND_ALL_SQL);
    }

    public Optional<Genre> findById(Long id) {
        return findOne(FIND_BY_ID_SQL, id);
    }

    public Set<Long> findIdsByFilm(long filmId) {
        List<Long> result = jdbc.queryForList(FIND_ALL_BY_FILM_ID_SQL, Long.class, filmId);
        return new HashSet<>(result);
    }
}