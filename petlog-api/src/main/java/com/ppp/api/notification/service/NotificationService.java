package com.ppp.api.notification.service;

import com.ppp.api.notification.dto.response.NotificationResponse;
import com.ppp.domain.notification.Notification;
import com.ppp.domain.notification.constant.Type;
import com.ppp.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void createNotification(Type type, String receiverId, String content) {

        Notification notification = Notification.of(type, receiverId, content);
        notificationRepository.save(notification);
    }

    public Page<NotificationResponse> displayNotifications(int page, int size) {
        return null;
    }
}
