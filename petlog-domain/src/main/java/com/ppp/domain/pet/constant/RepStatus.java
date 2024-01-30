package com.ppp.domain.pet.constant;

// 사용자 대표 반려동물
public enum RepStatus {
    NORMAL("normal"),
    REPRESENTATIVE("rep");

    private final String value;

    RepStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
