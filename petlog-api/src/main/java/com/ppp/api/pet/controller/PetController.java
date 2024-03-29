package com.ppp.api.pet.controller;

import com.ppp.api.exception.ExceptionResponse;
import com.ppp.api.pet.dto.request.CheckPetRequest;
import com.ppp.api.pet.dto.request.PetRequest;
import com.ppp.api.pet.dto.response.MyPetResponse;
import com.ppp.api.pet.dto.response.MyPetsResponse;
import com.ppp.api.pet.service.PetService;
import com.ppp.common.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "Pets", description = "Pets APIs")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@MultipartConfig(maxFileSize = 1024 * 1024 * 15)
public class PetController {
    private final PetService petService;

    @Operation(summary = "반려동물 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping(value = "/v1/my/pets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPet(
            @Valid @RequestPart PetRequest petRequest,
            @RequestPart(required = false) MultipartFile petImage,
            @AuthenticationPrincipal PrincipalDetails principalDetails
            ) {
        petService.createPet(petRequest, principalDetails.getUser(), petImage);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "반려동물 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "해당 그룹에서 공동집사를 찾을 수 없습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PutMapping(value = "/v1/my/pets/{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePet(
            @PathVariable("petId") Long petId,
            @Valid @RequestPart PetRequest petRequest,
            @RequestPart(required = false) MultipartFile petImage,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        petService.updatePet(petId, petRequest, principalDetails.getUser(), petImage);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "반려동물 조회", description = "공동집사로 관리하고 있는 반려동물를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MyPetResponse.class))}),
            @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없습니다", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @GetMapping("/v1/my/pets/{petId}")
    public ResponseEntity<MyPetResponse> displayPet(
            @PathVariable("petId") Long petId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(petService.findMyPetById(petId, principalDetails.getUser()));
    }

    @Operation(summary = "반려동물 코드 조회", description = "반려동물 id 를 통해 초대코드를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @GetMapping("/v1/my/pets/{petId}/code")
    public ResponseEntity<String> displayPetCode(@PathVariable Long petId) {
        return ResponseEntity.ok(petService.findPetCode(petId));
    }

    @Operation(summary = "반려동물 리스트", description = "공동집사로 관리하는 반려동물 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MyPetsResponse.class))})
    })
    @GetMapping("/v1/my/pets")
    public ResponseEntity<MyPetsResponse> displayPets(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(petService.findMyPetByInGuardian(principalDetails.getUser()));
    }

    @Operation(summary = "반려동물 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "다른 멤버가 있을 때 반려동물을 삭제할 수 없습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @DeleteMapping("/v1/my/pets/{petId}")
    public ResponseEntity<Void> deleteMyPet(@PathVariable("petId") Long petId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        petService.deleteMyPet(petId, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "반려동물 이름 중복 검사")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "비어있을 수 없습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "409", description = "중복된 펫이름 입니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PostMapping("/v1/my/pets/check/name")
    public ResponseEntity<Void> checkPetName(@Valid @RequestBody CheckPetRequest checkPetRequest) {
        petService.validatePetName(checkPetRequest.name());
        return ResponseEntity.ok().build();
    }
}
