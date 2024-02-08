package com.ppp.domain.log;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(indexes = {
        @Index(name = "idx_pet_id_datetime", columnList = "pet_id, datetime")
})
public class Log extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime datetime;

    @Column(name = "type", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> typeMap = new HashMap<>();

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

    @OneToOne(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
    private LogLocation location;

    private void deleteLocation() {
        location = null;
    }

    public void addLocation(LogLocation location) {
        this.location = location;
    }

    public void delete() {
        this.isDeleted = true;
        deleteLocation();
    }

    public void checkComplete() {
        isComplete = !isComplete;
    }

    public void update(LocalDateTime datetime, Map<String, String> typeMap, String memo, boolean isImportant, boolean isComplete, User manager, LogLocation location) {
        this.datetime = datetime;
        this.typeMap = typeMap;
        this.memo = memo;
        this.isImportant = isImportant;
        this.isComplete = isComplete;
        this.manager = manager;
        addLocation(location);
    }

    @Builder
    public Log(LocalDateTime datetime, Map<String, String> typeMap, String memo, boolean isImportant, boolean isComplete, Pet pet, User manager) {
        this.datetime = datetime;
        this.typeMap = typeMap;
        this.memo = memo;
        this.isImportant = isImportant;
        this.isComplete = isComplete;
        this.pet = pet;
        this.manager = manager;
    }
}
