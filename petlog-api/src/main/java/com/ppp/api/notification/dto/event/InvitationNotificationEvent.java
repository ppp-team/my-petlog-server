package com.ppp.api.notification.dto.event;

import com.ppp.domain.notification.constant.MessageCode;
import com.ppp.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvitationNotificationEvent {
    private MessageCode messageCode;
    private User actor;
    private String receiverId;
    private String petName;
}
