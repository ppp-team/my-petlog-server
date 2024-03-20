package com.ppp.domain.notification.dto;

import com.ppp.domain.notification.constant.Type;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private Type type;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private String profilePath;
    private String thumbnailPath;

    @Builder
    @QueryProjection
    public NotificationDto(Long id, Type type, String message, Boolean isRead, LocalDateTime createdAt, String thumbnailPath) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.thumbnailPath = thumbnailPath;
    }
}