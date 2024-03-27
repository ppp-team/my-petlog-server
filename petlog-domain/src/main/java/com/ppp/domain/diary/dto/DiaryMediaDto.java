package com.ppp.domain.diary.dto;

import com.ppp.domain.diary.constant.DiaryMediaType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.Objects;

@Data
public class DiaryMediaDto {
    private Long id;
    private DiaryMediaType type;
    private String path;

    @QueryProjection
    public DiaryMediaDto(Long id, DiaryMediaType type, String path) {
        this.id = id;
        this.type = type;
        this.path = path;
    }

    public boolean isNonNull() {
        return Objects.nonNull(id);
    }
}
