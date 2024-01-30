package com.ppp.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST,"GLOBAL-0002", "피일 업로드에 실패했습니다."),
    NOT_VALID_EXTENSION(HttpStatus.BAD_REQUEST,"GLOBAL-0003", "적합한 확장자가 아닙니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

