package com.ppp.api.user.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileUpdatedEvent {
    private String userId;
}
