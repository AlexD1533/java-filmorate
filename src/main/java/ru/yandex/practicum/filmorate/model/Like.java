package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    private long filmId;
    private long userId;


}
