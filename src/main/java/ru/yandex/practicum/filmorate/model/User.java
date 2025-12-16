package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"email"})

public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}