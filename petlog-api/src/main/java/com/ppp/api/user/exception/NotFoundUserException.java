package com.ppp.api.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundUserException extends RuntimeException{
    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    public NotFoundUserException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getStatus();
        this.code = errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }
}