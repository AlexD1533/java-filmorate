package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private long reviewId;
    private String content;
    private boolean isPositive;
    private long userId;
    private long filmId;
    private long useful;
}
