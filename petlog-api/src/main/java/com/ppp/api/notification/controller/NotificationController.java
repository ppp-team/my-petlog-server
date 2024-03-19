package com.ppp.api.notification.controller;

import com.ppp.api.notification.dto.response.NotificationResponse;
import com.ppp.api.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림", description = "알림 APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    @PatchMapping("/v1/notifications")
    public Page<NotificationResponse> displayNotifications(
            @AuthenticationPrincipal AuthenticationPrincipal authenticationPrincipal,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size) {
        return notificationService.displayNotifications(page, size);
    }
}
