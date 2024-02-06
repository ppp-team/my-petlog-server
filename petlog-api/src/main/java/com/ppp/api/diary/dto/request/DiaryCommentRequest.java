package com.ppp.api.diary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryCommentRequest {
    @Schema(description = "내용", example = "체리 너무 귀엽다")
    @Size(min = 1, max = 5000, message = "1자 이상 5000자 이하의 내용을 입력해주세요.")
    private String content;

    @Schema(description = "태깅 유저 아이디", example = "abcde123")
    private List<String> taggedUserIds = new ArrayList<>();
}

