package com.ppp.api.notification.controller;

import com.ppp.api.notification.dto.response.NotificationResponse;
import com.ppp.api.notification.service.NotificationService;
import com.ppp.common.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림", description = "알림 APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/v1/notifications")
    public Page<NotificationResponse> displayNotifications(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        return notificationService.displayNotifications(principal.getUser(), page, size);
    }
}