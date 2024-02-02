package com.ppp.api.diary.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryRequest {
    @Schema(description = "제목", example = "오늘은 공원에서 산책을 했어요")
    @Size(min = 1, max = 255, message = "1자 이상 255자 이하의 제목을 입력해주세요.")
    private String title;

    @Schema(description = "내용", example = "날씨가 좋아서 산책을 했답니다")
    @Size(min = 1, max = 5000, message = "1자 이상 5000자 이하의 내용을 입력해주세요.")
    private String content;

    @Schema(description = "날짜", example = "2024-01-31")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
}

