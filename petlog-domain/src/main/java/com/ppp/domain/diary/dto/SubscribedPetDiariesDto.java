package com.ppp.domain.diary.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SubscribedPetDiariesDto(
        List<PetDiaryDto> contents,
        boolean hasNext
) {
    public static SubscribedPetDiariesDto of(List<PetDiaryDto> contents, boolean hasNext){
        return SubscribedPetDiariesDto.builder()
                .contents(contents)
                .hasNext(hasNext)
                .build();
    }
}
