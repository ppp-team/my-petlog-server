package com.ppp.api.diary.handler;

import com.ppp.api.diary.service.DiaryCommentRedisService;
import com.ppp.api.diary.service.DiaryRedisService;
import com.ppp.api.diary.service.DiarySearchService;
import com.ppp.api.diary.dto.event.DiaryCreatedEvent;
import com.ppp.api.diary.dto.event.DiaryDeletedEvent;
import com.ppp.api.diary.dto.event.DiaryUpdatedEvent;
import com.ppp.common.service.FileManageService;
import com.ppp.domain.diary.DiaryMedia;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiaryEventHandler {
    private final DiarySearchService diarySearchService;
    private final DiaryCommentRedisService diaryCommentRedisService;
    private final DiaryRedisService diaryRedisService;
    private final FileManageService fileManageService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryCreatedEvent(DiaryCreatedEvent event) {
        diarySearchService.save(event.getDiaryId());
        diaryCommentRedisService.setDiaryCommentCountByDiaryId(event.getDiaryId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryUpdatedEvent(DiaryUpdatedEvent event) {
        diarySearchService.update(event.getDiaryId());
        fileManageService.deleteImages(event.getDiaryMedias().stream().map(DiaryMedia::getPath)
                .collect(Collectors.toList()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryDeletedEvent(DiaryDeletedEvent event) {
        diarySearchService.delete(event.getDiaryId());
        diaryCommentRedisService.deleteDiaryCommentCountByDiaryId(event.getDiaryId());
        diaryRedisService.deleteAllLikeByDiaryId(event.getDiaryId());
        fileManageService.deleteImages(event.getDiaryMedias().stream().map(DiaryMedia::getPath)
                .collect(Collectors.toList()));
    }
}
