package com.ppp.domain.invitation.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InviteStatus {
    PENDING("대기중"),
    ACCEPTED("수락"),
    REJECTED("거절"),
    CANCELED("취소됨")
    ;

    private final String value;
}
