package com.ppp.api.diary.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    FORBIDDEN_PET_SPACE(HttpStatus.FORBIDDEN, "DIARY-0001","해당 기록 공간에 대한 권한이 없습니다."),
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY-0002","일치하는 일기가 없습니다."),
    NOT_DIARY_OWNER(HttpStatus.BAD_REQUEST, "DIARY-0003", "일기 작성자가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

