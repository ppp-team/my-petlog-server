package com.ppp.domain.diary.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PetDiaryDto {
    private Long diaryId;
    private Long petId;
    private String petName;
    private String petProfilePath;
    private String content;
    private String title;
    private LocalDateTime createdAt;
    private Set<DiaryMediaDto> diaryMedias;

    @QueryProjection
    public PetDiaryDto(Long diaryId, Long petId, String petName, Set<DiaryMediaDto> diaryMedias,
                       String petProfilePath, String content, String title, LocalDateTime createdAt) {
        this.diaryId = diaryId;
        this.petId = petId;
        this.petName = petName;
        this.petProfilePath = petProfilePath;
        this.content = content;
        this.title = title;
        this.createdAt = createdAt;
        this.diaryMedias = diaryMedias
                .stream().filter(DiaryMediaDto::isNonNull)
                .collect(Collectors.toSet());
    }
}
