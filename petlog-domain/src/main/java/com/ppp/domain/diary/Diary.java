package com.ppp.domain.diary;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Diary extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "blob", nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDate date;

    private String thumbnailPath;

    @Column(columnDefinition = "bit(1) default 0")
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryMedia> diaryMedias = new ArrayList<>();

    public void deleteDiaryMedias() {
        diaryMedias.clear();
    }


    public void addDiaryMedias(List<DiaryMedia> diaryMedias) {
        if (this.diaryMedias.isEmpty()) {
            this.diaryMedias = diaryMedias;
        } else {
            this.diaryMedias.clear();
            this.diaryMedias.addAll(diaryMedias);
        }
    }

    public void update(String title, String content, LocalDate date, List<DiaryMedia> diaryMedias) {
        this.title = title;
        this.content = content;
        this.date = date;
        addDiaryMedias(diaryMedias);
    }

    public void delete() {
        this.isDeleted = true;
        deleteDiaryMedias();
    }

    @Builder
    public Diary(String title, String content, LocalDate date, Pet pet, User user) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.pet = pet;
        this.user = user;
    }
}
