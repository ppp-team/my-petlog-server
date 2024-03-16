package com.ppp.api.pet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record CheckPetRequest (
    @Schema(description = "펫 명")
    @NotEmpty
    String name
) {

}