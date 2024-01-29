package com.ppp.api.auth.exception;

import lombok.Getter;

@Getter
public class SigninException extends RuntimeException{
    private final String message;
    private final String code;
    private final int status;

    public SigninException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus().value();
        this.code = errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }
}