package com.ppp.api.diary.exception;

import lombok.Getter;

@Getter
public class DiaryException extends RuntimeException {
    private final int status;
    private final String code;

    public DiaryException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus().value();
        this.code = errorCode.getCode();
    }
}