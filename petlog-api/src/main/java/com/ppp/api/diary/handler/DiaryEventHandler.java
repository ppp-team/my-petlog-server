package com.ppp.api.diary.handler;

import com.ppp.api.diary.dto.event.DiaryCreatedEvent;
import com.ppp.api.diary.dto.event.DiaryDeletedEvent;
import com.ppp.api.diary.dto.event.DiaryUpdatedEvent;
import com.ppp.api.diary.service.DiaryCommentRedisService;
import com.ppp.api.diary.service.DiaryRedisService;
import com.ppp.api.diary.service.DiarySearchService;
import com.ppp.api.diary.service.DiaryService;
import com.ppp.common.service.FileStorageManageService;
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
public class DiaryEventHandler {
    private final DiaryService diaryService;
    private final DiarySearchService diarySearchService;
    private final DiaryCommentRedisService diaryCommentRedisService;
    private final DiaryRedisService diaryRedisService;
    private final FileStorageManageService fileStorageManageService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryCreatedEvent(DiaryCreatedEvent event) {
        CompletableFuture.runAsync(() -> {
                    log.info("Class : {}, Method : {}", this.getClass(), "handleDiaryCreatedEvent");
                })
                .thenRunAsync(() -> diarySearchService.save(diaryService.saveThumbnail(event.getDiaryId())))
                .thenRunAsync(() -> diaryCommentRedisService.setDiaryCommentCountByDiaryId(event.getDiaryId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryUpdatedEvent(DiaryUpdatedEvent event) {
        CompletableFuture.runAsync(() -> {
                    log.info("Class : {}, Method : {}", this.getClass(), "handleDiaryUpdatedEvent");
                })
                .thenRunAsync(() -> diarySearchService.update(diaryService.saveThumbnail(event.getDiaryId())))
                .thenRunAsync(() -> fileStorageManageService.deleteImages(event.getDeletedPaths()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiaryDeletedEvent(DiaryDeletedEvent event) {
        CompletableFuture.runAsync(() -> {
                    log.info("Class : {}, Method : {}", this.getClass(), "handleDiaryDeletedEvent");
                })
                .thenRunAsync(() -> diarySearchService.delete(event.getDiaryId()))
                .thenRunAsync(() -> diaryCommentRedisService.deleteDiaryCommentCountByDiaryId(event.getDiaryId()))
                .thenRunAsync(() -> diaryRedisService.deleteAllLikeByDiaryId(event.getDiaryId()))
                .thenRunAsync(() -> fileStorageManageService.deleteImages(event.getDeletedPaths()));
    }
}
