package com.ppp.api.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ppp.domain.user.User;
import com.ppp.domain.user.UserDocument;
import lombok.Builder;

import java.util.Objects;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        String id,
        String nickname,
        String email,
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

    public static UserResponse from(UserDocument userDocument, String currentUserId) {
        return UserResponse.builder()
                .id(userDocument.getId())
                .nickname(userDocument.getNickname())
                .isCurrentUser(Objects.equals(userDocument.getId(), currentUserId))
                .build();
    }

    public static UserResponse of(String userId, String nickname, String currentUserId) {
        return UserResponse.builder()
                .id(userId)
                .nickname(nickname)
                .isCurrentUser(Objects.equals(userId, currentUserId))
                .build();
    }
}
