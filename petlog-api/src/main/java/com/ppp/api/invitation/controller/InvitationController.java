package com.ppp.api.invitation.controller;

import com.ppp.api.exception.ExceptionResponse;
import com.ppp.api.invitation.dto.request.InvitationRequest;
import com.ppp.api.invitation.dto.response.InvitationResponse;
import com.ppp.api.invitation.service.InvitationService;
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

import java.util.List;

@Tag(name = "관리-초대", description = "관리-초대 APIs")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;

    @Operation(summary = "초대받은 내역 리스트")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = InvitationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "그룹 생성자의 경우, 탈퇴는 관리자에게 문의해주세요.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @GetMapping("/v1/my/invitations")
    public ResponseEntity<List<InvitationResponse>> displayInvitations(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(invitationService.displayInvitations(principalDetails.getUser()));
    }

    @Operation(summary = "초대 수락")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "해당 초대내역이 존재하지 않습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/v1/my/acceptance")
    public ResponseEntity<Void> acceptInvitation(@RequestBody InvitationRequest invitationRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        invitationService.acceptInvitation(invitationRequest, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대 거절")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "해당 초대내역이 존재하지 않습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/v1/my/refusal")
    public ResponseEntity<Void> refuseInvitation(@RequestBody InvitationRequest invitationRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        invitationService.refuseInvitation(invitationRequest, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
