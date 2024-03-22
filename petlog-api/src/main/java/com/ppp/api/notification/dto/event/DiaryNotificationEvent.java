package com.ppp.api.notification.dto.event;

import com.ppp.domain.diary.Diary;
import com.ppp.domain.notification.constant.MessageCode;
import com.ppp.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryNotificationEvent {
    private MessageCode messageCode;
    private User actor;
    private Diary diary;
}