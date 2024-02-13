package com.ppp.domain.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Domain {
    USER(false),
    DIARY(true),
    DIARY_COMMENT(false),
    PET(false);
    private final boolean hasVideo;
}
