package com.ppp.domain.log;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.log.constant.LogLocationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class LogLocation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLocationType type;

    @Column
    private Long mapId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false)
    private Log log;

    @Builder
    public LogLocation(LogLocationType type, Long mapId, Log log) {
        this.type = type;
        this.mapId = mapId;
        this.log = log;
    }
}
