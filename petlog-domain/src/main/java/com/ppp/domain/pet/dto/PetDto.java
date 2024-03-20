package com.ppp.domain.pet.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PetDto {
    private Long id;
    private String profilePath;
    private String name;

    @QueryProjection
    public PetDto(Long id, String profilePath, String name) {
        this.id = id;
        this.profilePath = profilePath;
        this.name = name;
    }
}
