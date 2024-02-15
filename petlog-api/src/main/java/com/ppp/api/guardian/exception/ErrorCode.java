package com.ppp.api.guardian.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    GUARDIAN_NOT_FOUND(HttpStatus.NOT_FOUND, "GUARDIAN-0001", "해당 그룹에서 공동집사를 찾을 수 없습니다."),
    NOT_ALLOWED_DELETE_LEADER(HttpStatus.BAD_REQUEST, "GUARDIAN-0002", "다른 멤버가 있을 때 탈퇴할 수 없습니다."),
    NOT_INVITED_EMAIL(HttpStatus.BAD_REQUEST, "GUARDIAN-0003", "자신의 이메일은 초대가 불가능 합니다."),
    NOT_FOUND_INVITEE(HttpStatus.NOT_FOUND, "GUARDIAN-0004", "초대한 사용자를 찾을 수 없습니다."),
    NOT_INVITED_ALREADY_GUARDIAN(HttpStatus.BAD_REQUEST, "GUARDIAN-0005", "해당 반려동물의 공동집사입니다."),
    NOT_INVITED(HttpStatus.BAD_REQUEST, "GUARDIAN-0006", "초대가 불가능합니다. 다시 확인해주세요."),
    NOT_DELETED_IF_READER(HttpStatus.BAD_REQUEST, "GUARDIAN-0007", "그룹 생성자의 경우, 탈퇴는 관리자에게 문의해주세요."),
    FORBIDDEN_PET_SPACE(HttpStatus.FORBIDDEN, "GUARDIAN-0008", "해당 공간에 대한 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}