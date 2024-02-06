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
        UserResponse manager
) {
    public static LogResponse from(Log log, String currentUserId) {
        LogType type = LogType.valueOf(log.getTypeMap().get("type"));
        return LogResponse.builder()
                .logId(log.getId())
                .taskName(Objects.equals(LogType.CUSTOM, type) ?
                        log.getTypeMap().get("subType") : type.getTitle())
                .isComplete(log.isComplete())
                .isImportant(log.isImportant())
                .time(log.getDatetime())
                .manager(UserResponse.of(log.getManager().getId(), log.getManager().getNickname(), currentUserId))
                .build();
    }
}
