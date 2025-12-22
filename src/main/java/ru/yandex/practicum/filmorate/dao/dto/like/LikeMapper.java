package ru.yandex.practicum.filmorate.dao.dto.like;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Like;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LikeMapper {
    public static Like mapToLike(NewLikeRequest request) {
        return new Like(request.getFilmId(), request.getUserId());
    }

    public static LikeDto mapToLikeDto(Like like) {
        return new LikeDto(like.getFilmId(), like.getUserId());
    }


}