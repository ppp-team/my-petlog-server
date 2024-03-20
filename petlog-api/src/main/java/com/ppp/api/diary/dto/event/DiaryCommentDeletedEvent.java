package com.ppp.api.diary.dto.event;

import com.ppp.domain.diary.DiaryComment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryCommentDeletedEvent {
    private DiaryComment diaryComment;
}
