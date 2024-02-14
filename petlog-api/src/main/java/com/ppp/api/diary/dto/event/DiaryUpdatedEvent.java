package com.ppp.api.diary.dto.event;

import com.ppp.domain.diary.DiaryMedia;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class DiaryUpdatedEvent {
    private final long diaryId;
    private final List<String> deletedPaths;

    public DiaryUpdatedEvent(long diaryId, List<DiaryMedia> deletedDiaryMedias, String deletedThumbnailPath) {
        this.diaryId = diaryId;
        List<String> deletedPaths = new ArrayList<>(deletedDiaryMedias.stream().map(DiaryMedia::getPath).toList());
        if (deletedThumbnailPath != null)
            deletedPaths.add(deletedThumbnailPath);
        this.deletedPaths = deletedPaths;
    }
}
