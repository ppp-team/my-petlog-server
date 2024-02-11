package com.ppp.api.diary.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "날짜별 일기들")
@Builder
public record DiaryGroupByDateResponse(
        @Schema(description = "날짜", example = "2024년 2월 11일 일요일")
        @JsonFormat(pattern = "yyyy년 M월 d일 E요일")
        LocalDate date,
        @ArraySchema(schema = @Schema(description = "일기"))
        List<DiaryResponse> diaries
) {
    public static DiaryGroupByDateResponse of(LocalDate date, List<DiaryResponse> diaryResponses) {
        return DiaryGroupByDateResponse.builder()
                .date(date)
                .diaries(diaryResponses)
                .build();
    }
}
