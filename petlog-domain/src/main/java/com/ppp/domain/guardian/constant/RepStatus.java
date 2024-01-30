package com.ppp.domain.guardian.constant;

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
