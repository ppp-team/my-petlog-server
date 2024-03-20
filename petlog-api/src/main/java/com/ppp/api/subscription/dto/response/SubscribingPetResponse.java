package com.ppp.api.subscription.dto.response;

import com.ppp.domain.pet.dto.PetDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "구독 중인 반려 동물")
@Builder
public record SubscribingPetResponse(
        @Schema(description = "반려 동물 아이디")
        Long id,
        @Schema(description = "반려 동물 이름")
        String name,
        @Schema(description = "반려 동물 프로필")
        String profilePath
) {
    public static SubscribingPetResponse from(PetDto dto) {
        return SubscribingPetResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .profilePath(dto.getProfilePath())
                .build();
    }
}
