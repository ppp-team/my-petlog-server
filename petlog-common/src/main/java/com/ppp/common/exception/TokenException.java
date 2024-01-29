package com.ppp.common.exception;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException{
    private final String message;
    private final String errorCode;
    private final int status;

    public TokenException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus().value();
        this.errorCode = errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }
}