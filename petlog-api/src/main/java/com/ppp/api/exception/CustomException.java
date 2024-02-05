package com.ppp.api.exception;

import com.ppp.api.mock.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    public CustomException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
    }
}