package com.ppp.domain.common;

import jakarta.persistence.Id;

public abstract class BaseDocument {
    @Id
    private String id;

    public BaseDocument(String id) {
        this.id = id;
    }
}
