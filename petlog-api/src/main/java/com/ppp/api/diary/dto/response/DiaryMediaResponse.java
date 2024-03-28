package com.ppp.api.diary.dto.response;

import com.ppp.domain.diary.DiaryMedia;
import com.ppp.domain.diary.dto.DiaryMediaDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "육아 일기 미디어")
@Builder
public record DiaryMediaResponse(
        @Schema(description = "미디어 아이디")
        Long mediaId,
        @Schema(description = "미디어 path")
        String path
) {
    public static DiaryMediaResponse from(DiaryMedia diaryMedia) {
        return DiaryMediaResponse.builder()
                .mediaId(diaryMedia.getId())
                .path(diaryMedia.getPath())
                .build();
    }

    public static DiaryMediaResponse from(DiaryMediaDto dto) {
        return DiaryMediaResponse.builder()
                .mediaId(dto.getId())
                .path(dto.getPath())
                .build();
    }
}
