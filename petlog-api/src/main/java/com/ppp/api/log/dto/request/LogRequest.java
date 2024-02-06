package com.ppp.api.log.dto.request;

import com.ppp.common.validator.DateTime;
import com.ppp.common.validator.EnumValue;
import com.ppp.domain.log.constant.LogType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogRequest {
    @EnumValue(enumClass = LogType.class, message = "지원되지 않는 기록 항목입니다.")
    private String type;

    @Size(max = 255, message = "255자 이하의 중분류를 입력해주세요.")
    private String subType;

    private Boolean isCustomLocation;

    private Long kakaoLocationId;

    @DateTime(message = "적합한 datetime 을 입력해주세요.")
    private String datetime;

    private Boolean isComplete = false;

    @NotNull(message = "중요도를 체크해주세요.")
    private Boolean isImportant;

    @Size(max = 5000, message = "5000자 이하의 메모를 입력해주세요.")
    private String memo;

    @NotBlank(message = "담당자 유저 아이디를 입력해주세요.")
    private String managerId;
}
