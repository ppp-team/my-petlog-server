package com.ppp.api.pet.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "PET-0001", "일치하는 반려 동물이 없습니다."),
    PET_IMAGE_REGISTRATION_FAILED(HttpStatus.BAD_REQUEST, "PET-0002", "사진 등록에 실패했습니다. 다시 시도해주세요."),
    PET_NAME_DUPLICATION(HttpStatus.CONFLICT, "PET-0003", "중복된 반려 동물 이름 입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

