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

    private String thumbnailUrl;

    @Column(columnDefinition = "bit default 0")
    private Boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "diary")
    private List<DiaryMedia> diaryMedias = new ArrayList<>();

    public void addThumbnail(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void update(String title, String content, LocalDate date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public void delete() {
        this.isDeleted = true;
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
