package com.ppp.api.diary.handler;

import com.ppp.api.diary.dto.event.DiaryCommentCreatedEvent;
import com.ppp.api.diary.dto.event.DiaryCommentDeletedEvent;
import com.ppp.api.diary.dto.event.DiaryReCommentCreatedEvent;
import com.ppp.api.diary.service.DiaryCommentRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiaryCommentEventHandler {
    private final DiaryCommentRedisService diaryCommentRedisService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryCommentCreatedEvent(DiaryCommentCreatedEvent event) {
        CompletableFuture.runAsync(() -> diaryCommentRedisService.setDiaryReCommentCountByCommentId(event.getDiaryCommentId()))
                .thenRunAsync(() -> diaryCommentRedisService.increaseDiaryCommentCountByDiaryId(event.getDiaryId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryCommentDeletedEvent(DiaryCommentDeletedEvent event) {
        CompletableFuture.runAsync(() -> diaryCommentRedisService.decreaseDiaryCommentCountByDiaryId(event.getDiaryComment().getDiary().getId()))
                .thenRunAsync(() -> {if(event.getDiaryComment().isReComment())
                    diaryCommentRedisService.decreaseDiaryReCommentCountByCommentId(event.getDiaryComment().getAncestorCommentId());})
                .thenRunAsync(() -> diaryCommentRedisService.deleteAllLikeByCommentId(event.getDiaryComment().getId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryReCommentCreatedEvent(DiaryReCommentCreatedEvent event) {
        CompletableFuture.runAsync(() -> diaryCommentRedisService.increaseDiaryReCommentCountByCommentId(event.getAncestorId()))
                .thenRunAsync(() -> diaryCommentRedisService.increaseDiaryCommentCountByDiaryId(event.getDiaryId()));
    }
}
