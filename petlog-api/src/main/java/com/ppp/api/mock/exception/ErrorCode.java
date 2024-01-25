package com.ppp.api.mock.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    MEMBER_NOT_WRITER(HttpStatus.BAD_REQUEST, "게시물 작성자가 아닙니다."),
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 게시물이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 토큰입니다. 다시 로그인해주세요."),
    PASSWORD_UNMATCHED(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "등록되지 않은 이메일입니다."),
    MEMBER_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");
    private final HttpStatus httpStatus;
    private final String detail;
}
