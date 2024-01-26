package com.ppp.api.mock.exception;

import lombok.Getter;

@Getter
public class MockException extends RuntimeException {
    private final String message;
    private final String code;
    private final int status;

    public MockException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.status = errorCode.getHttpStatus().value();
        this.code = errorCode.getCode();
    }
}