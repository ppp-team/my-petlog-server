package com.ppp.api.user.handler;

import com.ppp.api.diary.service.DiarySearchService;
import com.ppp.api.user.dto.event.UserProfileUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventHandler {
    private final DiarySearchService diarySearchService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserProfileUpdatedEvent(UserProfileUpdatedEvent event) {
        diarySearchService.updateUser(event.getUserId());
    }
}
