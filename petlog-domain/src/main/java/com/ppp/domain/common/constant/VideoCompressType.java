package com.ppp.domain.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoCompressType {
    LOW(480),
    MEDIUM(540);
    private final int resolution;
}
