package com.ppp.api.diary.dto.response;

import com.ppp.domain.diary.dto.DiaryPopularTermsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

@Schema(description = "자주 사용한 용어")
@Builder
public record DiaryMostUsedTermsResponse(
        Set<String> terms
) {
    public static DiaryMostUsedTermsResponse from(DiaryPopularTermsDto dto) {
        return DiaryMostUsedTermsResponse.builder()
                .terms(dto.getPopularTerms())
                .build();
    }
}
