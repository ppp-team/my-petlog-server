package com.ppp.api.invitation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvitationException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public InvitationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
    }
}