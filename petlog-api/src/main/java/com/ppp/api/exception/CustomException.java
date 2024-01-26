package com.ppp.api.exception;

import com.ppp.api.mock.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final String message;
    private final String code;
    private final int status;

    public CustomException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.status = errorCode.getHttpStatus().value();
        this.code = errorCode.getCode();
    }
}