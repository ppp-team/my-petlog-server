package com.ppp.api.diary.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryCommentDeletedEvent {
    private long diaryId;
    private long commentId;
}
