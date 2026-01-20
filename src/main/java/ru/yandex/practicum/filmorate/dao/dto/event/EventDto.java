package ru.yandex.practicum.filmorate.dao.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EventDto {
    private long eventId;
    private long timestamp;
    private long userId;
    private String eventType;
    private String operation;
    private long entityId;
}
