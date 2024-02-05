package com.ppp.api.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException{
    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    public AuthException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getStatus();
        this.code = errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }
}