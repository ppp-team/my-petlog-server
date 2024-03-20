package com.ppp.api.notification.dto.response;

import lombok.Builder;

@Builder
public record NotificationResponse (
        Long id,
        String type,
        String message,
        String thumbnailPath,
        Boolean checked,
        String createdAt
) {

}