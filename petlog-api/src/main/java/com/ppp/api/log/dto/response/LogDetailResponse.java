package com.ppp.api.log.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ppp.domain.log.Log;
import com.ppp.domain.log.constant.LogType;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LogDetailResponse(
        Long logId,
        String type,
        String subType,
        String memo
) {
    public static LogDetailResponse from(Log log) {
        return LogDetailResponse.builder()
                .logId(log.getId())
                .type(LogType.valueOf(log.getTypeMap().get("type")).getTitle())
                .subType(log.getTypeMap().get("subType"))
                .memo(log.getMemo())
                .build();
    }
}
