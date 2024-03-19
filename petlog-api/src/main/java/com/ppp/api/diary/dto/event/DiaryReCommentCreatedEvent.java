package com.ppp.api.diary.dto.event;

import com.ppp.domain.diary.DiaryComment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class DiaryReCommentCreatedEvent {
    private long diaryId;
    private long ancestorId;

    public DiaryReCommentCreatedEvent(DiaryComment comment) {
        this.diaryId = comment.getDiary().getId();
        this.ancestorId = comment.getAncestorCommentId();
    }
}
