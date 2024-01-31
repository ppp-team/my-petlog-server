package com.ppp.domain.invitation.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InviteStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    @Getter
    private final String value;
}
