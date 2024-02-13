package com.ppp.api.guardian.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GuardianException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public GuardianException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
    }
}