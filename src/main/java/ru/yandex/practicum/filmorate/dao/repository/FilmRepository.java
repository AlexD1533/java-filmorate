package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;


@Component
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    private static final String DELETE_FILM_DIRECTORS = "DELETE FROM film_directors WHERE film_id = ?";
    private static final String INSERT_FILM_DIRECTORS = "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)";
    private static final String FIND_EXIST_BY_NAME_DATE_QUERY = "SELECT * FROM films WHERE name = ? AND release_date = ?";
    private static final String FIND_ID_EXIST = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
    private static final String FIND_TOP_POPULAR_FILMS_SQL =
            "SELECT f.*, COUNT(l.user_id) AS likes_count " +
                    "FROM films f " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "GROUP BY f.film_id, f.name " +
                    "ORDER BY COUNT(l.user_id) DESC, f.film_id " +
                    "FETCH FIRST ? ROWS ONLY";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }


    @Override
    public Film create(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa()
        );
        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getId()
        );
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return findMany(FIND_ALL_QUERY);
    }


    @Override
    public Optional<Film> getById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Optional<Film> findByNameAndReleaseDate(String name, LocalDate releaseDate) {
        return findOne(FIND_EXIST_BY_NAME_DATE_QUERY, name, releaseDate);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return findMany(FIND_TOP_POPULAR_FILMS_SQL, count);
    }

    @Override
    public boolean validateId(long id) {
        return existsById(FIND_ID_EXIST, id);
    }

    public void saveDirectors(long filmId, Set<Long> directorIds) {
        update(DELETE_FILM_DIRECTORS, filmId); // Удаляем старые
        if (directorIds != null) {
            for (Long directorId : directorIds) {
                update(INSERT_FILM_DIRECTORS, filmId, directorId);
            }
        }
    }

    public Set<Long> getDirectorsByFilm(long filmId) {
        List<Long> directorIds = jdbc.queryForList(
                "SELECT director_id FROM film_directors WHERE film_id = ?",
                Long.class,
                filmId
        );
        return new HashSet<>(directorIds);
    }

    @Override
    public List<Film> getFilmsByDirector(long directorId, String sortBy) {
        String sql = "SELECT f.* FROM films f " +
                "JOIN film_directors fd ON f.film_id = fd.film_id " +
                "WHERE fd.director_id = ? ";

        if ("year".equalsIgnoreCase(sortBy)) {
            sql += "ORDER BY f.release_date";
        } else { // по умолчанию сортировка по лайкам
            sql = "SELECT f.*, COUNT(l.user_id) AS likes_count " +
                    "FROM films f " +
                    "JOIN film_directors fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.user_id) DESC";
        }

        return findMany(sql, directorId);
    }
}
