package com.ppp.domain.pet.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {

    MALE("MALE"),
    FEMALE("FEMALE");

    @Getter
    private final String value;
}
