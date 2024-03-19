package com.ppp.api.diary.dto.response;

import com.ppp.api.user.dto.response.UserResponse;
import com.ppp.common.util.TimeUtil;
import com.ppp.domain.diary.DiaryComment;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "육아 일기 댓글")
@Builder
public record DiaryReCommentResponse(
        @Schema(description = "댓글 아이디")
        Long commentId,
        @Schema(description = "내용")
        String content,
        @Schema(description = "생성 날짜")
        String createdAt,
        @Schema(description = "유저가 좋아요를 누른 댓글인지 여부")
        boolean isCurrentUserLiked,
        @Schema(description = "댓글 좋아요 수")
        int likeCount,
        @Schema(description = "글쓴이 정보")
        UserResponse writer,
        @Schema(description = "대댓글 받는 유저 정보")
        UserResponse receiver,
        @ArraySchema(schema = @Schema(description = "태깅 유저 정보"))
        List<UserResponse> taggedUsers
) {
    public static DiaryReCommentResponse from(DiaryComment comment, String currentUserId, boolean isCurrentUserLiked, int likeCount) {
        return DiaryReCommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .createdAt(TimeUtil.calculateTerm(comment.getCreatedAt()))
                .writer(UserResponse.from(comment.getUser(), currentUserId))
                .isCurrentUserLiked(isCurrentUserLiked)
                .likeCount(likeCount)
                .receiver(UserResponse.from(comment.getParent().getUser(), currentUserId))
                .taggedUsers(comment.getTaggedUsersIdNicknameMap().keySet()
                        .stream().map(id -> UserResponse.of(id,
                                comment.getTaggedUsersIdNicknameMap().get(id), currentUserId))
                        .collect(Collectors.toList()))
                .build();
    }

    public static DiaryReCommentResponse from(DiaryComment comment, String currentUserId) {
        return DiaryReCommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .createdAt(TimeUtil.calculateTerm(comment.getCreatedAt()))
                .writer(UserResponse.from(comment.getUser(), currentUserId))
                .receiver(UserResponse.from(comment.getParent().getUser(), currentUserId))
                .taggedUsers(comment.getTaggedUsersIdNicknameMap().keySet()
                        .stream().map(id -> UserResponse.of(id,
                                comment.getTaggedUsersIdNicknameMap().get(id), currentUserId))
                        .collect(Collectors.toList()))
                .build();
    }
}
