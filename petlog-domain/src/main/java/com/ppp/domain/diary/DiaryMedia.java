package com.ppp.domain.diary;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.diary.constant.DiaryMediaType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DiaryMedia extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaryMediaType type;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @Builder
    public DiaryMedia(String path, DiaryMediaType type, Diary diary) {
        this.path = path;
        this.type = type;
        this.diary = diary;
    }
}
