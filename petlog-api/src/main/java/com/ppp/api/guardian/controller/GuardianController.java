package com.ppp.api.guardian.controller;

import com.ppp.api.exception.ExceptionResponse;
import com.ppp.api.guardian.dto.request.DeleteGuardianRequest;
import com.ppp.api.guardian.dto.request.InviteGuardianRequest;
import com.ppp.api.guardian.dto.response.GuardianResponse;
import com.ppp.api.guardian.dto.response.GuardiansResponse;
import com.ppp.api.guardian.service.GuardianService;
import com.ppp.common.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리-공동집사(펫메이트 그룹)", description = "관리-공동집사 APIs")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GuardianController {
    private final GuardianService guardianService;

    @Operation(summary = "공동집사 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = GuardianResponse.class))})
    })
    @GetMapping("/v1/my/guardians/{petId}")
    public ResponseEntity<GuardiansResponse> displayGuardians(@PathVariable Long petId) {
        return ResponseEntity.ok(guardianService.displayGuardians(petId));
    }

    @Operation(summary = "집사 삭제 및 탈퇴")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "그룹 생성자의 경우, 탈퇴는 관리자에게 문의해주세요.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "해당 그룹에서 공동집사를 찾을 수 없습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @DeleteMapping("/v1/my/guardians/{petId}")
    public ResponseEntity<Void> deleteGuardian(
            @RequestBody DeleteGuardianRequest deleteGuardianRequest,
            @PathVariable Long petId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        guardianService.deleteGuardian(deleteGuardianRequest.getGuardianId(), petId, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "집사 초대")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "자신의 이메일은 초대가 불가능 합니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "해당 반려동물의 공동집사입니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "초대가 불가능합니다. 다시 확인해주세요.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 반려 동물이 없습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "초대한 사용자를 찾을 수 없습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/v1/my/guardians/{petId}/invite")
    public ResponseEntity<Void> inviteGuardian(
            @PathVariable Long petId,
            @RequestBody InviteGuardianRequest inviteGuardianRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
            ) {
        guardianService.inviteGuardian(petId, inviteGuardianRequest, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
