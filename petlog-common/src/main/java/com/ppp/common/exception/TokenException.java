package com.ppp.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenException extends RuntimeException{
    private final String message;
    private final String errorCode;
    private final HttpStatus httpStatus;

    public TokenException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getStatus();
        this.errorCode = errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }
}