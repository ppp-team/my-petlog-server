package com.ppp.api.diary.dto.event;

import com.ppp.domain.diary.DiaryComment;
import lombok.Getter;

@Getter
public class DiaryCommentCreatedEvent {
    private long diaryId;
    private long diaryCommentId;

    public DiaryCommentCreatedEvent(DiaryComment comment) {
        this.diaryId = comment.getDiary().getId();
        this.diaryCommentId = comment.getId();
    }
}
