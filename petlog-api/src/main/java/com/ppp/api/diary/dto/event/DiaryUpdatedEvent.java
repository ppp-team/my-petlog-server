package com.ppp.api.diary.dto.event;

import com.ppp.domain.diary.DiaryMedia;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DiaryUpdatedEvent {
    private long diaryId;
    private List<DiaryMedia> diaryMedias;
}
