package com.ppp.api.diary.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DiaryException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public DiaryException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getStatus();
        this.code = errorCode.getCode();
    }
}