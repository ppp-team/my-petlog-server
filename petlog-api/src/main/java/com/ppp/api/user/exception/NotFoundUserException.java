package com.ppp.api.user.exception;

import lombok.Getter;

@Getter
public class NotFoundUserException extends RuntimeException{
    private final String message;
    private final String code;
    private final int status;

    public NotFoundUserException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus().value();
        this.code = errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }
}