package com.ppp.domain.notification;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.notification.constant.Type;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "notifications")
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false, length = 100)
    private String receiverId;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(columnDefinition = "bit(1) default 0")
    private Boolean isRead;

    public static Notification of(Type type, String receiverId, String content) {
        return Notification.builder()
                .type(type)
                .receiverId(receiverId)
                .content(content)
                .isRead(false)
                .build();
    }
}
