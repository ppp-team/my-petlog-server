package com.ppp.domain.notification.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Type {
    INVITATION("초대"),
    DIARY("일기"),
    SUBSCRIBE("구독")
    ;

    private final String description;
}