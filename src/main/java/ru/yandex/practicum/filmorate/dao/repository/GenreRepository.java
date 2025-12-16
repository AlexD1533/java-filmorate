package ru.yandex.practicum.filmorate.dao.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Repository

public class GenreRepository extends BaseRepository<Genre> {
    private final JdbcTemplate jdbcTemplate;

    // SQL константы
    private static final String FIND_ALL_SQL = "SELECT * FROM genre ORDER BY genre_id";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String FIND_NAME_BY_ID_SQL = "SELECT name FROM genre WHERE genre_id = ?";
    private static final String FIND_BY_FILM_ID_SQL = """
        SELECT g.* 
        FROM genre g
        JOIN film_genre fg ON g.genre_id = fg.genre_id
        WHERE fg.film_id = ?
        ORDER BY g.genre_id
        """;
    private static final String FIND_FILM_IDS_BY_GENRE_ID_SQL = "SELECT film_id FROM film_genre WHERE genre_id = ?";
    private static final String INSERT_FILM_GENRE_SQL = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRE_SQL = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
    private static final String DELETE_ALL_FILM_GENRES_SQL = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String EXISTS_FILM_GENRE_SQL = "SELECT COUNT(*) FROM film_genre WHERE film_id = ? AND genre_id = ?";
    private static final String COUNT_ALL_GENRES_SQL = "SELECT COUNT(*) FROM genre";
    private static final String COUNT_FILMS_BY_GENRE_SQL = "SELECT COUNT(*) FROM film_genre WHERE genre_id = ?";

    public GenreRepository(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbc;
    }

    // Существующие методы
    public List<Genre> findAll() {
        return findMany(FIND_ALL_SQL);
    }

    public List<Genre> findByFilmId(Long filmId) {
        return findMany(FIND_BY_FILM_ID_SQL, filmId);
    }

    public Set<Long> findFilmIdsByGenreId(Integer genreId) {
        List<Long> filmIds = jdbcTemplate.queryForList(FIND_FILM_IDS_BY_GENRE_ID_SQL, Long.class, genreId);
        return new HashSet<>(filmIds);
    }

    public void addFilmGenre(Long filmId, Integer genreId) {
        jdbcTemplate.update(INSERT_FILM_GENRE_SQL, filmId, genreId);
    }

    public void removeFilmGenre(Long filmId, Integer genreId) {
        jdbcTemplate.update(DELETE_FILM_GENRE_SQL, filmId, genreId);
    }

    public void removeAllFilmGenres(Long filmId) {
        jdbcTemplate.update(DELETE_ALL_FILM_GENRES_SQL, filmId);
    }

    public boolean existsFilmGenre(Long filmId, Integer genreId) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_FILM_GENRE_SQL, Integer.class, filmId, genreId);
        return count != null && count > 0;
    }

    // НОВЫЕ МЕТОДЫ для получения названий и количества
    public Optional<String> findNameById(Long id) {
        try {
            String name = jdbcTemplate.queryForObject(FIND_NAME_BY_ID_SQL, String.class, id);
            return Optional.ofNullable(name);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Map<Integer, String> findAllGenreNames() {
        List<Genre> genres = findAll();
        return genres.stream()
                .collect(Collectors.toMap(
                        Genre::getId,
                        Genre::getName
                ));
    }

    public int countAllGenres() {
        Integer count = jdbcTemplate.queryForObject(COUNT_ALL_GENRES_SQL, Integer.class);
        return count != null ? count : 0;
    }

    public int countFilmsByGenreId(Integer genreId) {
        Integer count = jdbcTemplate.queryForObject(COUNT_FILMS_BY_GENRE_SQL, Integer.class, genreId);
        return count != null ? count : 0;
    }

    public List<String> findGenreNamesByFilmId(Long filmId) {
        List<Genre> genres = findByFilmId(filmId);
        return genres.stream()
                .map(Genre::getName)
                .collect(Collectors.toList());
    }

    public Optional<Genre> findById(Long id) {
        return findOne(FIND_BY_ID_SQL, id);
    }

    // Дополнительные полезные методы
    public Map<Integer, Integer> countFilmsForAllGenres() {
        List<Genre> genres = findAll();
        return genres.stream()
                .collect(Collectors.toMap(
                        Genre::getId,
                        genre -> countFilmsByGenreId(genre.getId())
                ));
    }

    public List<Genre> findPopularGenres(int limit) {
        String sql = """
            SELECT g.*, COUNT(fg.film_id) as film_count
            FROM genre g
            LEFT JOIN film_genre fg ON g.genre_id = fg.genre_id
            GROUP BY g.genre_id, g.name
            ORDER BY film_count DESC, g.name
            LIMIT ?
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, limit);
    }


}