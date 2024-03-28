package com.ppp.domain.diary;


import com.ppp.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(indexes = {
        @Index(name = "idx_diaryId_userId", columnList = "diaryId, userId")
})
public class DiaryLike extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long diaryId;

    @Column(nullable = false, length = 20)
    private String userId;

    public DiaryLike(Long diaryId, String userId) {
        this.diaryId = diaryId;
        this.userId = userId;
    }
}
