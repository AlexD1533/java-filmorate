package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.dto.event.EventDto;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/feed")
@RequiredArgsConstructor
public class FeedController {

    private final EventService eventService;
    private final Validation validation;


    @GetMapping
    public List<EventDto> getUserFeed(@PathVariable long userId) {
        validation.validateUserExists(userId);
        return eventService.getUserFeed(userId);
    }
}
