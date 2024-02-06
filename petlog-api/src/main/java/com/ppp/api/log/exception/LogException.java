package com.ppp.api.log.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LogException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public LogException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getStatus();
        this.code = errorCode.getCode();
    }
}