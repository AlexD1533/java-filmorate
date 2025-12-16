package ru.yandex.practicum.filmorate.dao.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaRepository extends BaseRepository<MpaRating> {
    private final JdbcTemplate jdbcTemplate;

    // SQL константы
    private static final String FIND_ALL_SQL = "SELECT * FROM mpa_rating ORDER BY rating_id";
    private static final String FIND_BY_FILM_ID_SQL = """
        SELECT r.* 
        FROM mpa_rating r
        JOIN films f ON r.rating_id = f.rating_id
        WHERE f.film_id = ?
        """;
    private static final String FIND_BY_NAME_SQL = "SELECT * FROM mpa_rating WHERE name = ?";
    private static final String COUNT_FILMS_BY_RATING_SQL = "SELECT COUNT(*) FROM films WHERE rating_id = ?";

    public MpaRepository(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbc;
    }

    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_SQL);
    }

    public List<MpaRating> findByFilmId(Long filmId) {
        return findMany(FIND_BY_FILM_ID_SQL, filmId);
    }

    public List<MpaRating> findByName(String name) {
        return findMany(FIND_BY_NAME_SQL, name);
    }

    public int countFilmsByRatingId(Integer ratingId) {
        Integer count = jdbcTemplate.queryForObject(COUNT_FILMS_BY_RATING_SQL, Integer.class, ratingId);
        return count != null ? count : 0;
    }
}
