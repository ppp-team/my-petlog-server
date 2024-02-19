package com.ppp.api.log.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ppp.domain.log.Log;
import com.ppp.domain.log.LogLocation;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LogDetailResponse(
        Long logId,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDateTime date,
        @JsonFormat(pattern = "HH:mm")
        LocalDateTime time,
        String managerId,
        String type,
        String subType,
        String memo,
        Boolean isImportant,
        Location location
) {
    public static LogDetailResponse from(Log log) {
        return LogDetailResponse.builder()
                .logId(log.getId())
                .date(log.getDatetime())
                .time(log.getDatetime())
                .managerId(log.getManager().getId())
                .type(log.getTypeMap().get("type"))
                .subType(log.getTypeMap().get("subType"))
                .memo(log.getMemo())
                .location(Location.from(log.getLocation()))
                .build();
    }

    @Builder
    private record Location(
            Boolean isCustomLocation,
            Long kakaoLocationId
    ) {
        private static Location from(LogLocation location) {
            if (location == null) return null;
            return Location.builder()
                    .isCustomLocation(location.getIsCustomLocation())
                    .kakaoLocationId(location.getMapId())
                    .build();
        }
    }
}
