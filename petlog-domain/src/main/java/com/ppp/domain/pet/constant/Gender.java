package com.ppp.domain.pet.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {

    MALE("male"),
    FEMALE("female");

    @Getter
    private final String value;
}
