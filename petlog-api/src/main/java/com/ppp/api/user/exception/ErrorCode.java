package com.ppp.api.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND_USER(HttpStatus.INTERNAL_SERVER_ERROR, "USER-0001", "사용자를 찾을 수 없습니다."),
    PROFILE_REGISTRATION_FAILED(HttpStatus.BAD_REQUEST, "USER-0002", "프로필 등록에 실패했습니다."),
    NICKNAME_DUPLICATION(HttpStatus.CONFLICT, "USER-0003", "닉네임 중복"),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "USER-0004", "이메일 중복"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}
