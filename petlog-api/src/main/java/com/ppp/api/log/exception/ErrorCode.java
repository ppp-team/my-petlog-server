package com.ppp.api.log.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    FORBIDDEN_PET_SPACE(HttpStatus.FORBIDDEN, "LOG-0001", "해당 기록 공간에 대한 권한이 없습니다."),
    LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "LOG-0002","일치하는 기록이 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

