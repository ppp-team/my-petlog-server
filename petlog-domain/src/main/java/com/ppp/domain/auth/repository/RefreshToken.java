package com.ppp.domain.auth.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "refresh_token", nullable = false, length = 255)
    private String refreshToken;

    @Column(name = "expiration_time", nullable = false)
    private Date expirationTime;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public RefreshToken(String email, String refreshToken) {
        this.email = email;
        this.refreshToken = refreshToken;
        this.expirationTime = calculateExpirationTime();
        this.createdAt = new Date();
    }

    @Value("${application.security.jwt.refresh-token.expiration}")
    @Transient
    private long refreshExpiration;

    // expirationTime을 계산하는 메서드
    private Date calculateExpirationTime() {
        // 현재 시간을 가져옵니다.
        Date currentDate = new Date();

        // 만료 시간을 계산하여 설정합니다. (예: 7일 후)
        long expirationMillis = currentDate.getTime() + refreshExpiration; // 7일을 밀리초로 변환
        return new Date(expirationMillis);
    }
}