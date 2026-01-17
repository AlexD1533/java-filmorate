package ru.yandex.practicum.filmorate.dao.repository.mappers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.repository.BaseRepository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class DirectorRepository extends BaseRepository<Director> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE director_id = ?";

    private static final String FIND_DIRECTORS_BY_FILM_ID =
            "SELECT d.* FROM directors d " +
                    "JOIN film_directors fd ON d.director_id = fd.director_id " +
                    "WHERE fd.film_id = ?";

    private static final String FIND_FILMS_BY_DIRECTOR_SORTED_BY_LIKES =
            "SELECT f.* FROM films f " +
                    "JOIN film_directors fd ON f.film_id = fd.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) DESC";

    private static final String FIND_FILMS_BY_DIRECTOR_SORTED_BY_YEAR =
            "SELECT f.* FROM films f " +
                    "JOIN film_directors fd ON f.film_id = fd.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY f.release_date";

    public DirectorRepository(JdbcTemplate jdbc) {
        super(jdbc, new DirectorRowMapper());
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Director> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Director save(Director director) {
        if (director.getId() == null) {
            Long id = insert(INSERT_QUERY, director.getName());
            director.setId(id);
            return director;
        } else {
            update(UPDATE_QUERY, director.getName(), director.getId());
            return director;
        }
    }

    public boolean delete(Long id) {
        return delete(DELETE_QUERY, id);
    }

    public Set<Director> findDirectorsByFilmId(Long filmId) {
        List<Director> directors = findMany(FIND_DIRECTORS_BY_FILM_ID, filmId);
        return new HashSet<>(directors);
    }

    // Метод для связи фильмов с режиссерами
    public void linkFilmWithDirectors(Long filmId, List<Long> directorIds) {
        // Удаляем старые связи
        jdbc.update("DELETE FROM film_directors WHERE film_id = ?", filmId);

        // Добавляем новые связи
        if (directorIds != null && !directorIds.isEmpty()) {
            for (Long directorId : directorIds) {
                jdbc.update("INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)",
                        filmId, directorId);
            }
        }
    }

    public List<Long> findFilmIdsByDirectorSortedByLikes(Long directorId) {
        String sql = "SELECT f.film_id FROM films f " +
                "JOIN film_directors fd ON f.film_id = fd.film_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.film_id, f.release_date " +
                "ORDER BY COUNT(l.user_id) DESC, f.film_id";
        return jdbc.queryForList(sql, Long.class, directorId);
    }

    public List<Long> findFilmIdsByDirectorSortedByYear(Long directorId) {
        String sql = "SELECT f.film_id FROM films f " +
                "JOIN film_directors fd ON f.film_id = fd.film_id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY f.release_date, f.film_id";
        return jdbc.queryForList(sql, Long.class, directorId);
    }
}