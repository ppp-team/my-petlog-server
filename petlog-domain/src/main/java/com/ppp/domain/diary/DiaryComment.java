package com.ppp.domain.diary;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DiaryComment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "blob", nullable = false)
    private String content;

    @Column(name = "tagged_users", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> taggedUsersIdNicknameMap = new HashMap<>();

    @Column(columnDefinition = "bit(1) default 0")
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void delete() {
        isDeleted = true;
    }

    public void update(String content, Map<String, String> taggedUsersIdNicknameMap) {
        this.content = content;
        this.taggedUsersIdNicknameMap = taggedUsersIdNicknameMap;
    }

    @Builder
    public DiaryComment(String content, Map<String, String> taggedUsersIdNicknameMap, Diary diary, User user) {
        this.content = content;
        this.taggedUsersIdNicknameMap = taggedUsersIdNicknameMap;
        this.diary = diary;
        this.user = user;
    }
}
