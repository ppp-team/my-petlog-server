package com.ppp.api.notification.handler;

import com.ppp.api.notification.dto.event.NotificationEvent;
import com.ppp.api.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHandler {
    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saveNotification(NotificationEvent event) {
        String content = "";
        switch (event.getType()) {
            case INVITATION_REQUEST:
                content = String.format("%s님이 %s의 펫메이트로 초대했습니다.", event.getActorName(), event.getPetName());
                break;
            case INVITATION_ACCEPT:
                content = String.format("%s님이 %s 펫메이트 초대를 수락하셨습니다.", event.getActorName(), event.getPetName());
                break;
            case INVITATION_REJECT:
                content = String.format("%s님이 %s의 초대를 거절하셨습니다.", event.getActorName(), event.getPetName());
                break;
            case GUARDIAN_KICK:
                content = String.format("%s님이 %s 펫메이트 멤버에서 회원님을 삭제했습니다.", event.getActorName(), event.getPetName());
                break;

            case DIARY_COMMENT_CREATE:
                content = String.format("%s의 일기에 %s님이 댓글을 달았습니다.", event.getPetName(), event.getActorName());
                break;
            case DIARY_TAG:
                content = String.format("%s의 일기에 %s님이 회원님을 태그했습니다.", event.getPetName(), event.getActorName());
                break;
            case DIARY_LIKE:
                content = String.format("%s님이 %s의 일기를 좋아합니다.", event.getActorName(), event.getPetName());
                break;

            case SUBSCRIBE:
                content = String.format("%s님이 %s의 일기를 구독하기 시작했습니다.", event.getActorName(), event.getPetName());
                break;
            case SUBSCRIBE_CANCEL:
                content = String.format("%s님이 회원님의 구독을 취소했습니다.", event.getActorName());
                break;

        }
        notificationService.createNotification(event.getType(), event.getReceiverId(), content);
    }

}
