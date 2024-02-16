package com.ppp.api.log.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record LogCalenderResponse(
        @ArraySchema(arraySchema = @Schema(description = "로그가 존재하는 날짜 리스트", example = "[\"2024-02-13\", \"2024-02-15\"]"))
        List<LocalDate> scheduledDays
) {
    public static LogCalenderResponse from(List<LocalDate> scheduledDays) {
        return LogCalenderResponse.builder()
                .scheduledDays(scheduledDays)
                .build();
    }
}
