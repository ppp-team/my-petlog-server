package com.ppp.api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    REQUEST_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "GLOBAL-0001", "request argument error");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

