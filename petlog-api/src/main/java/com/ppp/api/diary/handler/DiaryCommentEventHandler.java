package com.ppp.api.diary.handler;

import com.ppp.api.diary.dto.event.DiaryCommentCreatedEvent;
import com.ppp.api.diary.dto.event.DiaryCommentDeletedEvent;
import com.ppp.api.diary.service.DiaryCommentRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DiaryCommentEventHandler {
    private final DiaryCommentRedisService diaryCommentRedisService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryCommentCreatedEvent(DiaryCommentCreatedEvent event) {
        diaryCommentRedisService.increaseDiaryCommentCountByDiaryId(event.getDiaryId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryCommentDeletedEvent(DiaryCommentDeletedEvent event) {
        diaryCommentRedisService.decreaseDiaryCommentCountByDiaryId(event.getDiaryId());
        diaryCommentRedisService.deleteAllLikeByCommentId(event.getCommentId());
    }
}
