package com.ppp.domain.user.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {

    USER("USER"),
    ADMIN("ADMIN");

    @Getter
    private final String type;
}
