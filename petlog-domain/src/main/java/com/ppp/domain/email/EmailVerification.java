package com.ppp.domain.email;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private int verificationCode;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static EmailVerification createVerification(String email, int verificationCode, long codeExpirationMillis) {
        long secondsToAdd = codeExpirationMillis / 1000;
        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(secondsToAdd);
        return EmailVerification.builder()
                .email(email)
                .verificationCode(verificationCode)
                .expiredAt(expiredAt)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void update(int code, LocalDateTime createdAt, long codeExpirationMillis) {
        long secondsToAdd = codeExpirationMillis / 1000;
        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(secondsToAdd);
        verificationCode = code;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public void setExpirationDate(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}