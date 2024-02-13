package com.ppp.api.invitation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "INVITATION-0001", "해당 초대내역이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}