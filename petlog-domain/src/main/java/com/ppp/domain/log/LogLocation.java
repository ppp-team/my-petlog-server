package com.ppp.domain.log;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class LogLocation {
    @Id
    private Long id;
    private float x;
    private float y;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false)
    private Log log;

    @Builder
    public LogLocation(Long id, float x, float y, Log log) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.log = log;
    }
}
