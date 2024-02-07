package com.ppp.api.log.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record LogGroupByDateResponse(
        @JsonFormat(pattern = "yyyy년 M월 d일 E요일")
        LocalDateTime date,
        List<LogResponse> logs
) {
    public static LogGroupByDateResponse of(LocalDateTime date, List<LogResponse> logResponses) {
        return LogGroupByDateResponse.builder()
                .date(date)
                .logs(logResponses)
                .build();
    }
}
