package com.ppp.api.pet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PetException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public PetException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
    }
}