package com.ppp.api.diary.dto.response;

import com.ppp.api.user.dto.response.UserResponse;
import com.ppp.domain.diary.Diary;
import lombok.Builder;

@Builder
public record DiaryResponse(
        Long diaryId,
        String title,
        String content,
        String thumbnailPath,
        boolean isCurrentUserLiked,
        UserResponse writer,
        int commentCount
) {
    public static DiaryResponse from(Diary diary, String currentUserId) {
        return DiaryResponse.builder()
                .diaryId(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .thumbnailPath(diary.getThumbnailPath())
                .writer(UserResponse.from(diary.getUser(), currentUserId))
                .build();
    }
}
