package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;


@Data

public class User {
    private long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private List<Friend> friends;
}