package com.ppp.api.diary.dto.response;

import com.ppp.api.user.dto.response.UserResponse;
import com.ppp.common.util.TimeUtil;
import com.ppp.domain.diary.DiaryComment;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record DiaryCommentResponse(
        Long commentId,
        String content,
        String createdAt,
        boolean isCurrentUserLiked,
        UserResponse writer,
        List<UserResponse> taggedUsers
) {
    public static DiaryCommentResponse from(DiaryComment comment, String currentUserId) {
        return DiaryCommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .createdAt(TimeUtil.calculateTerm(comment.getCreatedAt()))
                .writer(UserResponse.from(comment.getUser(), currentUserId))
                .taggedUsers(comment.getTaggedUsersIdNicknameMap().keySet()
                        .stream().map(id -> UserResponse.of(id,
                                comment.getTaggedUsersIdNicknameMap().get(id), currentUserId))
                        .collect(Collectors.toList()))
                .build();
    }
}
