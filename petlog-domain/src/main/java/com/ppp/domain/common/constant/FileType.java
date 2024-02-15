package com.ppp.domain.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
    VIDEO(".mp4"),
    IMAGE(".png");
    private final String extension;
}
