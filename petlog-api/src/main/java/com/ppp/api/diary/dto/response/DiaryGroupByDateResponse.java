package com.ppp.api.diary.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppp.domain.diary.Diary;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record DiaryGroupByDateResponse(
        @JsonFormat(pattern = "yyyy년 MM월 dd일 E요일")
        LocalDate date,
        List<DiaryResponse> diaries
) {
    public static DiaryGroupByDateResponse of(LocalDate date, List<Diary> diaries, String currentUserId) {
        return DiaryGroupByDateResponse.builder()
                .date(date)
                .diaries(diaries.stream()
                        .map(diary -> DiaryResponse.from(diary, currentUserId))
                        .collect(Collectors.toList()))
                .build();
    }
}
