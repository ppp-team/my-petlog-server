package com.ppp.api.subscription.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SubscriptionException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public SubscriptionException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getStatus();
        this.code = errorCode.getCode();
    }
}