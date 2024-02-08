package com.ppp.api.diary.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryCommentCreatedEvent {
    private long diaryId;
}
