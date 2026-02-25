package ru.yandex.practicum.filmorate.dao.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class ReviewDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long reviewId;

    private String content;
    private Boolean isPositive;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long filmId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long useful;


}