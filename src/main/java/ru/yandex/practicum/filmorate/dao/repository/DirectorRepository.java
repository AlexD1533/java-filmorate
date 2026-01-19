package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dao.repository.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> {

    // SQL для режиссёров
    private static final String FIND_ALL_DIRECTORS_SQL = "SELECT * FROM directors ORDER BY name";
    private static final String FIND_DIRECTOR_BY_ID_SQL = "SELECT * FROM directors WHERE director_id = ?";
    private static final String INSERT_DIRECTOR_SQL = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_DIRECTOR_SQL = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String DELETE_DIRECTOR_SQL = "DELETE FROM directors WHERE director_id = ?";
    private static final String DIRECTOR_EXISTS_SQL = "SELECT COUNT(*) FROM directors WHERE director_id = ?";

    // SQL для связей фильмов с режиссёрами
    private static final String FIND_BY_FILM_ID_SQL =
            "SELECT fd.*, d.name FROM film_directors fd " +
                    "JOIN directors d ON fd.director_id = d.director_id " +
                    "WHERE fd.film_id = ?";

    private static final String FIND_BY_DIRECTOR_ID_SQL =
            "SELECT * FROM film_directors WHERE director_id = ?";

    private static final String FIND_FILM_IDS_BY_DIRECTOR_SQL =
            "SELECT fd.film_id FROM film_directors fd WHERE fd.director_id = ?";

    private static final String INSERT_FILM_DIRECTOR_SQL =
            "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

    private static final String DELETE_BY_FILM_ID_SQL =
            "DELETE FROM film_directors WHERE film_id = ?";

    private static final String DELETE_BY_FILM_AND_DIRECTOR_SQL =
            "DELETE FROM film_directors WHERE film_id = ? AND director_id = ?";

    private static final String DELETE_BY_DIRECTOR_ID_SQL =
            "DELETE FROM film_directors WHERE director_id = ?";

    private static final String FILM_DIRECTOR_EXISTS_SQL =
            "SELECT COUNT(*) FROM film_directors WHERE film_id = ? AND director_id = ?";

    public DirectorRepository(JdbcTemplate jdbc, DirectorRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_DIRECTORS_SQL);
    }

    public Optional<Director> findById(Long id) {
        return findOne(FIND_DIRECTOR_BY_ID_SQL, id);
    }

    public Director save(Director director) {
        if (director.getId() != null && exists(director.getId())) {
            update(UPDATE_DIRECTOR_SQL, director.getName(), director.getId());
        } else {
            Long id = insert(INSERT_DIRECTOR_SQL, director.getName());
            director.setId(id);
        }
        return director;
    }

    public Director create(DirectorDto director) {
        Long id = insert(INSERT_DIRECTOR_SQL, director.getName());
        return new Director(id, director.getName());
    }

    public Director update(DirectorDto director) {
        if (director.getId() == null) {
            throw new IllegalArgumentException("Director ID must not be null for update");
        }

        update(UPDATE_DIRECTOR_SQL, director.getName(), director.getId());
        return new Director(director.getId(), director.getName());
    }

    public boolean delete(Long id) {
        return delete(DELETE_DIRECTOR_SQL, id);
    }

    public boolean exists(Long id) {
        Integer count = jdbc.queryForObject(DIRECTOR_EXISTS_SQL, Integer.class, id);
        return count != null && count > 0;
    }

    public List<Director> findDirectorsByFilmId(Long filmId) {
        return findMany(FIND_BY_FILM_ID_SQL, filmId);
    }

    public List<Long> findFilmIdsByDirectorId(Long directorId) {
        return jdbc.queryForList(FIND_FILM_IDS_BY_DIRECTOR_SQL, Long.class, directorId);
    }

    public void addDirectorToFilm(Long filmId, Long directorId) {
        if (!filmDirectorExists(filmId, directorId)) {
            jdbc.update(INSERT_FILM_DIRECTOR_SQL, filmId, directorId);
        }
    }

    public void addDirectorsToFilm(Long filmId, List<Long> directorIds) {
        if (directorIds != null && !directorIds.isEmpty()) {
            // Удаляем старые связи
            deleteDirectorsFromFilm(filmId);

            // Добавляем новые связи
            for (Long directorId : directorIds) {
                if (exists(directorId)) {
                    addDirectorToFilm(filmId, directorId);
                }
            }
        }
    }

    public void updateFilmDirectors(Long filmId, List<Director> directors) {
        if (directors != null) {
            // Удаляем старые связи
            deleteDirectorsFromFilm(filmId);

            // Добавляем новые связи
            for (Director director : directors) {
                if (director.getId() != null && exists(director.getId())) {
                    addDirectorToFilm(filmId, director.getId());
                }
            }
        }
    }

    public boolean deleteDirectorsFromFilm(Long filmId) {
        return delete(DELETE_BY_FILM_ID_SQL, filmId);
    }

    public boolean deleteDirectorFromFilm(Long filmId, Long directorId) {
        return delete(DELETE_BY_FILM_AND_DIRECTOR_SQL, filmId, directorId);
    }

    public boolean deleteAllByDirectorId(Long directorId) {
        return delete(DELETE_BY_DIRECTOR_ID_SQL, directorId);
    }

    public boolean filmDirectorExists(Long filmId, Long directorId) {
        Integer count = jdbc.queryForObject(FILM_DIRECTOR_EXISTS_SQL, Integer.class, filmId, directorId);
        return count != null && count > 0;
    }

    }