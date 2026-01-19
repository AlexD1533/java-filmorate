package ru.yandex.practicum.filmorate.dao.repository.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class DirectorRowMapper implements RowMapper<Director> {

    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        Director director = new Director();

        try {
            director.setId(rs.getLong("director_id"));
        } catch (SQLException e) {
            director.setId(rs.getLong("id"));
        }
        director.setName(rs.getString("name"));
        return director;
    }
}