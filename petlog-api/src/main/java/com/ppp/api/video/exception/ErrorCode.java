package com.ppp.api.video.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VIDEO_UPLOAD_NOT_ALLOWED(HttpStatus.FORBIDDEN, "VIDEO-0001", "동영상 업로드가 허용되지 않습니다."),
    NOT_ALLOWED_EXTENSION(HttpStatus.BAD_REQUEST, "VIDEO-0002", "허용되지 않는 확장자입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

