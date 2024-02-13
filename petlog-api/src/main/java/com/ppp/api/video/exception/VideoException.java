package com.ppp.api.video.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;
    public VideoException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getStatus();
        this.code = errorCode.getCode();
    }
}
