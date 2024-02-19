package com.ppp.api.log.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppp.api.user.dto.response.UserResponse;
import com.ppp.domain.log.Log;
import com.ppp.domain.log.constant.LogType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
public record LogResponse(
        Long logId,
        String taskName,
        boolean isComplete,
        boolean isImportant,
        @JsonFormat(pattern = "HH:mm")
        LocalDateTime time,
        UserResponse manager,
        LogDetail detail
) {
    public static LogResponse from(Log log, String currentUserId) {
        return LogResponse.builder()
                .logId(log.getId())
                .taskName(log.getTaskName())
                .isComplete(log.isComplete())
                .isImportant(log.isImportant())
                .time(log.getDatetime())
                .manager(UserResponse.of(log.getManager().getId(), log.getManager().getNickname(), currentUserId))
                .detail(LogDetail.from(log))
                .build();
    }

    @Builder
    private record LogDetail(
            String type,
            String subType,
            String memo
    ) {
        private static LogDetail from(Log log) {
            return LogDetail.builder()
                    .type(log.getTypeMap().get("type"))
                    .subType(log.getTypeMap().get("subType"))
                    .memo(log.getMemo())
                    .build();
        }
    }
}
