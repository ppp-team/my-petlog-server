package com.ppp.api.user.dto.response;

import com.ppp.domain.user.User;
import lombok.Builder;

import java.util.Objects;

@Builder
public record UserResponse(
        String id,
        String nickname,
        String profilePath,
        boolean isCurrentUser
) {
    public static UserResponse from(User user, String currentUserId) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .isCurrentUser(Objects.equals(user.getId(), currentUserId))
                .build();
    }
}
