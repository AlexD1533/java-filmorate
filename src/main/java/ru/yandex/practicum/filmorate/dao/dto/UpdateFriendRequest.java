package ru.yandex.practicum.filmorate.dao.dto;

import lombok.Data;

@Data
public class UpdateFriendRequest {
    private String status;

    public boolean hasStatus() {
        return status != null && !status.isBlank();
    }
}