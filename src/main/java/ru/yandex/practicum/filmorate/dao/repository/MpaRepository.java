package ru.yandex.practicum.filmorate.dao.repository;


import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository

public class MpaRepository extends BaseRepository<MpaRating> {

    private static final String FIND_ALL_SQL = "SELECT * FROM mpa_rating";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM mpa_rating WHERE id = ?";

    public MpaRepository(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Optional<MpaRating> findById(Long id) {
        return findOne(FIND_BY_ID_SQL, id);
    }
    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_SQL);
    }

}
