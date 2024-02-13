package com.ppp.api.pet.dto.response;

import com.ppp.common.util.TimeUtil;
import com.ppp.domain.pet.Pet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "반려 동물")
@Builder
public record PetResponse(
        @Schema(description = "반려 동물 아이디")
        Long id,
        @Schema(description = "반려 동물 종", example = "말티즈")
        String breed,
        @Schema(description = "반려 동물 나이", example = "4개월")
        String age
) {
    public static PetResponse from(Pet pet) {
        return PetResponse.builder()
                .id(pet.getId())
                .breed(pet.getBreed())
                .age(pet.getBirth() != null ? TimeUtil.calculateAge(pet.getBirth()) : null)
                .build();
    }
}
