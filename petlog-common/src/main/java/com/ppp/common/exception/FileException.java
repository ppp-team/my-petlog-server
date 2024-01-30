package com.ppp.common.exception;

import lombok.Getter;

@Getter
public class FileException extends RuntimeException {
    private final String code;
    private final int status;

    public FileException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getHttpStatus().value();
        this.code = errorCode.name();
    }
}