package com.ppp.api.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ppp.domain.user.User;
import com.ppp.domain.user.dto.UserDto;
import com.ppp.domain.user.UserDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Objects;

@Schema(description = "유저")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        @Schema(description = "유저 아이디")
        String id,
        @Schema(description = "유저 닉네임")
        String nickname,
        @Schema(description = "유저 프로필")
        String profilePath,
        @Schema(description = "현재 유저인지 여부")
        boolean isCurrentUser
) {
    public static UserResponse from(User user, String currentUserId) {
        return UserResponse.builder()
                .id(user.getId())
                .profilePath(user.getProfilePath())
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

    public static UserResponse from(UserDto user, String currentUserId) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .isCurrentUser(Objects.equals(user.getId(), currentUserId))
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
