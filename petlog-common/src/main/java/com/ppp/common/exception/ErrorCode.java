package com.ppp.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    REQUEST_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "GLOBAL-0001", "요청 파라미터를 확인해주세요."),
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "FILE-0001", "파일 업로드에 실패했습니다."),
    FILE_CLEAN_JOB_FAILED(HttpStatus.BAD_REQUEST, "FILE-0002", "파일 삭제에 실패했습니다."),
    EXTRACT_THUMBNAIL_FAILED(HttpStatus.BAD_REQUEST, "FILE-0003", "썸네일 추출에 실패했습니다."),
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-0001", "토큰을 찾을 수 없습니다."),
    REFRESHTOKEN_EXPIRATION(HttpStatus.UNAUTHORIZED, "TOKEN-0002", "리프레시 토큰 만료, 로그인 필요"),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "TOKEN-0003", "JWT의 서명이 올바르지 않음"),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-0004", "토큰 형식이 잘못됨"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-0005", "토큰 만료"),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-0006", "지원하지 않는 토큰 형식"),
    ILLEGALARGUMENT_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-0007", "JWT 클레임 문자열이 비어 있음"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "TOKEN-0008", "인증실패");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
