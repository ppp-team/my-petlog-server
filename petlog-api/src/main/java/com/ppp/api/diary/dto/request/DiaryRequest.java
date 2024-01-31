package com.ppp.api.diary.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryRequest {
    @Size(min = 1, max = 255, message = "1자 이상 255자 이하의 제목을 입력해주세요.")
    private String title;

    @Size(min = 1, max = 5000, message = "1자 이상 5000자 이하의 내용을 입력해주세요.")
    private String content;

    @PastOrPresent(message = "날짜를 확인해주세요.")
    private LocalDate date;
}
