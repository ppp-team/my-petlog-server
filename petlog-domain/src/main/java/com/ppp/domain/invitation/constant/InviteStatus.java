package com.ppp.domain.invitation.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InviteStatus {
    PENDING("대기중"),
    ACCEPTED("수락"),
    REJECTED("거절");

    @Getter
    private final String value;
}
