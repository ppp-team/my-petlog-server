package com.ppp.api.invitation.controller;

import com.ppp.api.exception.ExceptionResponse;
import com.ppp.api.invitation.dto.request.InvitationRequest;
import com.ppp.api.invitation.dto.request.RegisterInvitationRequest;
import com.ppp.api.invitation.dto.response.InvitationResponse;
import com.ppp.api.invitation.service.InvitationService;
import com.ppp.common.security.PrincipalDetails;
import com.ppp.domain.invitation.MyInvitationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

    @Operation(summary = "초대받은 내역 리스트", description = "펫메이트 그룹 관리에서 초대 받은 내역을 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = InvitationResponse.class)))}),
    })
    @GetMapping("/v1/my/invitations")
    public ResponseEntity<List<InvitationResponse>> displayInvitations(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(invitationService.displayInvitations(principalDetails.getUser()));
    }

    @Operation(summary = "초대 수락", description = "펫메이트 그룹 관리에서 초대를 수락합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "해당 초대내역이 존재하지 않습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/v1/my/acceptance")
    public ResponseEntity<Void> acceptInvitation(@RequestBody InvitationRequest invitationRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        invitationService.acceptInvitation(invitationRequest, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대 거절", description = "펫메이트 그룹 관리에서 초대를 거절합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "해당 초대내역이 존재하지 않습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/v1/my/refusal")
    public ResponseEntity<Void> refuseInvitation(@RequestBody InvitationRequest invitationRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        invitationService.refuseInvitation(invitationRequest, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내가 초대한 내역", description = "펫메이트 초대 내역에서 내가 초대한 내역을 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = MyInvitationDto.class)))}),
    })
    @GetMapping("/v1/my/invitations/my-invitations")
    public ResponseEntity<List<MyInvitationDto>> displayMyInvitations(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(invitationService.displayMyInvitations(principalDetails.getUser()));
    }

    @Operation(summary = "초대 취소", description = "펫메이트 초대 내역에서 내가 초대한 사람을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "해당 초대내역이 존재하지 않습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/v1/my/invitations/cancel")
    public ResponseEntity<Void> cancelInvitation(@RequestBody InvitationRequest invitationRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        invitationService.cancelInvitation(invitationRequest, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대 코드 등록", description = "초대 코드를 입력하여 공동집사에 등록됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "해당 반려동물의 공동집사입니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 반려 동물이 없습니다.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/v1/my/invitations/register")
    public ResponseEntity<Void> registerInvitation(@RequestBody RegisterInvitationRequest registerInvitationRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        invitationService.registerInvitation(registerInvitationRequest, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
