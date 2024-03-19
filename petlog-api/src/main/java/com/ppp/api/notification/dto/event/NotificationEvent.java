package com.ppp.api.notification.dto.event;

import com.ppp.domain.notification.constant.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationEvent {
    private Type type;
    private String receiverId;
    private String actorName;
    private String petName;
}
