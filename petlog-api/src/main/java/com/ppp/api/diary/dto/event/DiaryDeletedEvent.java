package com.ppp.api.diary.dto.event;

import com.ppp.domain.diary.DiaryMedia;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DiaryDeletedEvent {
    private final long diaryId;
    private final List<String> deletedPaths;

    public DiaryDeletedEvent(long diaryId, List<DiaryMedia> deletedDiaryMedias, String deletedThumbnailPath) {
        this.diaryId = diaryId;
        List<String> deletedPaths = new ArrayList<>(deletedDiaryMedias.stream().map(DiaryMedia::getPath).toList());
        if (deletedThumbnailPath != null)
            deletedPaths.add(deletedThumbnailPath);
        this.deletedPaths = deletedPaths;
    }
}
