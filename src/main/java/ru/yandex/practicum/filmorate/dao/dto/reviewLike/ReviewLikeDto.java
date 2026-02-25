package ru.yandex.practicum.filmorate.dao.dto.reviewLike;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLikeDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long reviewId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;

    private Boolean isLike;
}