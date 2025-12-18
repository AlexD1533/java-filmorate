package ru.yandex.practicum.filmorate.dao.dto.friend;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Friend;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FriendMapper {

    public static Friend mapToFriend(Long userId, NewFriendRequest request) {
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(request.getFriendId());
        friend.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");
        return friend;
    }

    public static FriendDto mapToFriendDto(Friend friend) {
        FriendDto dto = new FriendDto();
        dto.setUserId(friend.getUserId());
        dto.setFriendId(friend.getFriendId());
        dto.setStatus(friend.getStatus());
        return dto;
    }

    public static Friend updateFriendFields(Friend friend, UpdateFriendRequest request) {
        if (request.hasStatus()) {
            friend.setStatus(request.getStatus());
        }
        return friend;
    }


}