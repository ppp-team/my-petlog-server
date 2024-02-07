package com.ppp.api.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryCreatedEvent {
    private long diaryId;
}
