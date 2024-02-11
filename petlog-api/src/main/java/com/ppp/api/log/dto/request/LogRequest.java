package com.ppp.api.log.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ppp.common.validator.DateTime;
import com.ppp.common.validator.EnumValue;
import com.ppp.domain.log.constant.LogType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogRequest {
    @Schema(description = "대분류", example = "FEED")
    @EnumValue(enumClass = LogType.class, message = "지원되지 않는 기록 항목입니다.")
    private String type;

    @Schema(description = "중분류", example = "습식")
    @Size(max = 255, message = "255자 이하의 중분류를 입력해주세요.")
    private String subType;

    @Schema(description = "장소인경우 직접 입력 여부", example = "true")
    private Boolean isCustomLocation;

    @Schema(description = "카카오 데이터인 경우 카카오 장소 아이디", example = "12345")
    private Long kakaoLocationId;

    @Schema(description = "날짜 및 시간", example = "2024-02-06T11:11")
    @DateTime(message = "적합한 datetime 을 입력해주세요.")
    private String datetime;

    @Schema(description = "완료 여부", example = "true")
    private Boolean isComplete = false;

    @Schema(description = "중요 여부", example = "true")
    @NotNull(message = "중요도를 체크해주세요.")
    private Boolean isImportant;

    @Schema(description = "메모", example = "로얄캐닌 130g 줬어요")
    @Size(max = 5000, message = "5000자 이하의 메모를 입력해주세요.")
    private String memo;

    @Schema(description = "담당자 유저 아이디", example = "abcde123")
    @NotBlank(message = "담당자 유저 아이디를 입력해주세요.")
    private String managerId;

    @JsonIgnore
    public LocalDateTime getLocalDatetime() {
        return LocalDateTime.parse(this.datetime);
    }

    @JsonIgnore
    public LogType getLogType() {
        return LogType.valueOf(this.type);
    }
}
