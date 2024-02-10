package com.ppp.api.pet.controller;

import com.ppp.api.exception.ExceptionResponse;
import com.ppp.api.pet.dto.request.PetRequest;
import com.ppp.api.pet.dto.response.PetResponse;
import com.ppp.api.pet.dto.response.PetsResponse;
import com.ppp.api.pet.service.PetsService;
import com.ppp.common.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Pets", description = "Pets APIs")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PetsController {
    private final PetsService petsService;

    @Operation(summary = "반려동물 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/v1/my/pets")
    public ResponseEntity<Void> createPet(
            @RequestPart(required = false, value = "petImage") MultipartFile petImage,
            @Valid @RequestPart PetRequest petRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
            ) {
        petsService.createPet(petRequest, principalDetails.getUser(), petImage);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "반려동물 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PutMapping("/v1/my/pets/{petId}")
    public ResponseEntity<Void> updatePet(
            @RequestPart(required = false, value = "petImage") MultipartFile petImage,
            @PathVariable("petId") Long petId,
            @Valid @RequestPart PetRequest petRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        petsService.updatePet(petId, petRequest, petImage, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "반려동물 조회", description = "반려동물를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = PetResponse.class))}),
            @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @GetMapping("/v1/my/pets/{petId}")
    public ResponseEntity<PetResponse> displayPet(
            @PathVariable("petId") Long petId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(petsService.findMyPetById(petId, principalDetails.getUser()));
    }

    @Operation(summary = "반려동물 리스트", description = "자신이 등록한 반려동물 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = PetsResponse.class))})
    })
    @GetMapping("/v1/my/pets")
    public ResponseEntity<PetsResponse> displayPets(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(petsService.findMyPets(principalDetails.getUser()));
    }

    @Operation(summary = "대표 반려동물 지정")
    @PostMapping("/v1/my/pets/{petId}/selectRep")
    public ResponseEntity<Void> selectRepresentative(@PathVariable("petId") Long petId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        petsService.selectRepresentative(petId, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
