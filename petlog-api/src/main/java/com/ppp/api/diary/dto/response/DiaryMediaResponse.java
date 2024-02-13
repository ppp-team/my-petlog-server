package com.ppp.api.diary.dto.response;

import com.ppp.domain.diary.DiaryMedia;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "육아 일기 미디어")
@Builder
public record DiaryMediaResponse(
        @Schema(description = "미디어 아이디")
        Long id,
        @Schema(description = "미디어 path")
        String path
) {
    public static DiaryMediaResponse from(DiaryMedia diaryMedia){
        return DiaryMediaResponse.builder()
                .id(diaryMedia.getId())
                .path(diaryMedia.getPath())
                .build();
    }
}
