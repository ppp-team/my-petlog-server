package com.ppp.api.pet.exception;

import lombok.Getter;

@Getter
public class PetException extends RuntimeException {
    private final int status;
    private final String code;

    public PetException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getHttpStatus().value();
        this.code = errorCode.name();
    }
}