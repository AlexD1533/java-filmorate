package ru.yandex.practicum.filmorate.dao.dto;

import lombok.Data;

@Data
public class NewFriendRequest {

    private Long userId;
    private Long friendId;

    private String status = "not_confirmed"; // Значение по умолчанию
}
