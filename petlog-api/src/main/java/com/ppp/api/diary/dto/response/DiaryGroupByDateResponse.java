package com.ppp.api.diary.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record DiaryGroupByDateResponse(
        @JsonFormat(pattern = "yyyy년 M월 d일 E요일")
        LocalDate date,
        List<DiaryResponse> diaries
) {
    public static DiaryGroupByDateResponse of(LocalDate date, List<DiaryResponse> diaryResponses) {
        return DiaryGroupByDateResponse.builder()
                .date(date)
                .diaries(diaryResponses)
                .build();
    }
}
