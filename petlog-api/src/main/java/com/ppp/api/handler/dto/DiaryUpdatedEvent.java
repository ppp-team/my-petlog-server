package com.ppp.api.handler.dto;

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
