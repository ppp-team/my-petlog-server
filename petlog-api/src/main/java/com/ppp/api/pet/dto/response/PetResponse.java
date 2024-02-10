package com.ppp.api.pet.dto.response;

import com.ppp.common.util.TimeUtil;
import com.ppp.domain.pet.Pet;
import lombok.Builder;

@Builder
public record PetResponse(
        Long id,
        String breed,
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
