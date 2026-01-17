package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
public class EventRepository extends BaseRepository<Event> {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_EVENT_SQL =
            "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_USER_FEED_SQL =
            "SELECT e.* FROM events e WHERE e.user_id = ? OR e.user_id IN" +
                    "(SELECT f.friend_id FROM friends f WHERE f.user_id = ?) ORDER BY e.timestamp ASC";

    public EventRepository(JdbcTemplate jdbc, EventRowMapper mapper) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbc;
    }

    public void addEvent(Event event) {
        jdbcTemplate.update(
                INSERT_EVENT_SQL,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId()
        );
    }

    public List<Event> getUserFeed(long userId) {
        return jdbcTemplate.query(
                FIND_USER_FEED_SQL,
                mapper,
                userId,
                userId
        );
    }
}
