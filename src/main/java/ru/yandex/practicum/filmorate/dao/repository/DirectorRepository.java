package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

@Component
public class DirectorRepository implements DirectorStorage {

    private final JdbcTemplate jdbc;
    private final RowMapper<Director> directorMapper = (rs, rowNum) ->
            new Director(rs.getLong("director_id"), rs.getString("name"));

    public DirectorRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Director create(Director director) {
        long id = jdbc.queryForObject(
                "INSERT INTO directors(name) VALUES(?) RETURNING director_id",
                Long.class,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        jdbc.update(
                "UPDATE directors SET name = ? WHERE director_id = ?",
                director.getName(),
                director.getId()
        );
        return director;
    }

    @Override
    public void delete(Long id) {
        jdbc.update("DELETE FROM directors WHERE director_id = ?", id);
    }

    @Override
    public Collection<Director> getAll() {
        return jdbc.query("SELECT * FROM directors", directorMapper);
    }

    @Override
    public Optional<Director> getById(Long id) {
        return jdbc.query("SELECT * FROM directors WHERE director_id = ?", directorMapper, id)
                .stream()
                .findFirst();
    }
}
