package com.ppp.domain.log.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogType {
    FEED("사료"),
    HEALTH("건강"),
    WALK("산책"),
    TREAT("간식/영양제"),
    GROOMING("위생/미용"),
    CUSTOM("직접입력");
    private final String title;
}
