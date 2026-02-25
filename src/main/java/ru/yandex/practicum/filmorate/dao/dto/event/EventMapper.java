package ru.yandex.practicum.filmorate.dao.dto.event;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

@Component
public class EventMapper {

    public EventDto toDto(Event event) {
        return EventDto.builder()
                .eventId(event.getEventId())
                .timestamp(event.getTimestamp())
                .userId(event.getUserId())
                .eventType(event.getEventType().name())
                .operation(event.getOperation().name())
                .entityId(event.getEntityId())
                .build();
    }
}