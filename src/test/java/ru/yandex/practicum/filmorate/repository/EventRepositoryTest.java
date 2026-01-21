package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.repository.EventRepository;
import ru.yandex.practicum.filmorate.dao.repository.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
class EventRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository = new EventRepository(jdbcTemplate, new EventRowMapper());

        jdbcTemplate.update("DELETE FROM events");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (1, 'user1@mail.ru', 'user1', 'User One', '1990-01-01')"
        );

        jdbcTemplate.update(
                "INSERT INTO users (user_id, email, login, name, birthday) " +
                        "VALUES (2, 'user2@mail.ru', 'user2', 'User Two', '1991-01-01')"
        );
    }

    @Test
    void testAddEvent() {
        Event event = new Event();
        event.setTimestamp(1000L);
        event.setUserId(1L);
        event.setEventType(EventType.LIKE);
        event.setOperation(EventOperation.ADD);
        event.setEntityId(10L);

        eventRepository.addEvent(event);

        List<Event> events = eventRepository.getUserFeed(1L);
        assertThat(events).hasSize(1);

        Event saved = events.get(0);
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getEventType()).isEqualTo(EventType.LIKE);
        assertThat(saved.getOperation()).isEqualTo(EventOperation.ADD);
        assertThat(saved.getEntityId()).isEqualTo(10L);
    }

    @Test
    void testGetUserFeed_ReturnsEventsSortedByTimestamp() {
        jdbcTemplate.update(
                "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) " +
                        "VALUES (2000, 1, 'FRIEND', 'ADD', 5)"
        );
        jdbcTemplate.update(
                "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) " +
                        "VALUES (1000, 1, 'LIKE', 'ADD', 10)"
        );

        List<Event> feed = eventRepository.getUserFeed(1L);

        assertThat(feed).hasSize(2);
        assertThat(feed.get(0).getTimestamp()).isEqualTo(1000L);
        assertThat(feed.get(1).getTimestamp()).isEqualTo(2000L);
    }

    @Test
    void testGetUserFeed_WhenUserHasNoEvents() {
        List<Event> feed = eventRepository.getUserFeed(1L);
        assertThat(feed).isEmpty();
    }

    @Test
    void testGetUserFeed_DoesNotReturnOtherUsersEvents() {
        jdbcTemplate.update(
                "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) " +
                        "VALUES (1000, 2, 'REVIEW', 'ADD', 5)"
        );

        List<Event> feed = eventRepository.getUserFeed(1L);

        assertThat(feed).isEmpty();
    }
}
