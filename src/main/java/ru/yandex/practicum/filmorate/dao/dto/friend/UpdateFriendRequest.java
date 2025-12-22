package ru.yandex.practicum.filmorate.dao.dto.friend;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateFriendRequest {
    @NotNull(message = "ID пользователя обязателен")
    @Positive(message = "ID друга быть положительным")
    private Long userId;
    @NotNull(message = "ID пользователя обязателен")
    @Positive(message = "ID друга быть положительным")
    private Long friendId;
    private String status;

    public boolean hasStatus() {
        return status != null && !status.isBlank();
    }
}