package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.dto.event.EventDto;
import ru.yandex.practicum.filmorate.dao.dto.event.EventMapper;
import ru.yandex.practicum.filmorate.dao.repository.EventRepository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public void addEvent(long userId,
                         EventType eventType,
                         EventOperation operation,
                         long entityId) {

        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build();

        eventRepository.addEvent(event);
    }

    public List<EventDto> getUserFeed(long userId) {

        return eventRepository.getUserFeed(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }
}
