package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    // SQL константы
    private static final String FIND_ALL_SQL = "SELECT * FROM genre ORDER BY genre_id";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String FIND_ALL_BY_FILM_ID_SQL = "SELECT genre_id FROM film_genre WHERE film_id = ?";
    private static final String DELETE_GENRES_BY_FILM_SQL = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String INSERT_FILM_GENRE_SQL = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

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

    public void saveGenresIdsByFilm(long filmId, Set<Long> genreIds) {
        deleteGenresByFilm(filmId);
        if (genreIds != null && !genreIds.isEmpty()) {
            insertFilmGenres(filmId, genreIds);
        }
    }

    private void deleteGenresByFilm(long filmId) {
        jdbc.update(DELETE_GENRES_BY_FILM_SQL, filmId);
    }

    private void insertFilmGenres(long filmId, Set<Long> genreIds) {
        List<Object[]> batchArgs = new ArrayList<>();
        for (Long genreId : genreIds) {
            batchArgs.add(new Object[]{filmId, genreId});
        }

        jdbc.batchUpdate(INSERT_FILM_GENRE_SQL, batchArgs);
    }
}