package com.ppp.api.diary.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppp.api.pet.dto.response.PetResponse;
import com.ppp.api.user.dto.response.UserResponse;
import com.ppp.domain.diary.Diary;
import com.ppp.domain.diary.DiaryMedia;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record DiaryDetailResponse(
        Long diaryId,
        String title,
        String content,
        @JsonFormat(pattern = "yyyy.MM.dd")
        LocalDate date,
        List<String> images,
        boolean isCurrentUserLiked,
        UserResponse writer,
        int commentCount,
        int likeCount,
        PetResponse pet
) {
    public static DiaryDetailResponse from(Diary diary, String currentUserId, int commentCount, boolean isCurrentUserLiked, int likeCount) {
        return DiaryDetailResponse.builder()
                .diaryId(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .commentCount(commentCount)
                .images(diary.getDiaryMedias().stream()
                        .map(DiaryMedia::getPath)
                        .collect(Collectors.toList()))
                .date(diary.getDate())
                .isCurrentUserLiked(isCurrentUserLiked)
                .likeCount(likeCount)
                .writer(UserResponse.from(diary.getUser(), currentUserId))
                .pet(PetResponse.from(diary.getPet()))
                .build();
    }
}
