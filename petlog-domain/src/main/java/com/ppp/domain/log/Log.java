package com.ppp.domain.log;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.log.constant.LogType;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Log extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime datetime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType type;

    private String subType;

    @Column(columnDefinition = "blob")
    private String memo;

    @Column(columnDefinition = "bit(1) default 0")
    private boolean isComplete;

    @Column(columnDefinition = "bit(1) default 0")
    private boolean isImportant;

    @Column(columnDefinition = "bit(1) default 0")
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogLocation> locations = new ArrayList<>();

    public void delete() {
        this.isDeleted = true;
    }

    public void update(LocalDateTime datetime, LogType type, String subType, String memo, boolean isImportant, boolean isComplete, User manager) {
        this.datetime = datetime;
        this.type = type;
        this.subType = subType;
        this.memo = memo;
        this.isImportant = isImportant;
        this.isComplete = isComplete;
        this.manager = manager;
    }

    @Builder
    public Log(LocalDateTime datetime, LogType type, String subType, String memo, boolean isImportant, boolean isComplete, Pet pet, User manager) {
        this.datetime = datetime;
        this.type = type;
        this.subType = subType;
        this.memo = memo;
        this.isImportant = isImportant;
        this.isComplete = isComplete;
        this.pet = pet;
        this.manager = manager;
    }
}
