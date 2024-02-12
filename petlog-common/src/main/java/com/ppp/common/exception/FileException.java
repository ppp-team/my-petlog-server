package com.ppp.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileException extends RuntimeException {
    private final String code;
    private final HttpStatus httpStatus;

    public FileException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getStatus();
        this.code = errorCode.getCode();
    }
}