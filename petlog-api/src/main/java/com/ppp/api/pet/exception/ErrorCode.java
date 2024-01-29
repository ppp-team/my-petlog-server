package com.ppp.api.pet.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "PET-0001", "일치하는 반려 동물이 없습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

