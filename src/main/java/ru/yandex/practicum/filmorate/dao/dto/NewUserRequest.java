package ru.yandex.practicum.filmorate.dao.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    private Long id;
    private String email;

    private String login;

    private String name;

    private LocalDate birthday;
}