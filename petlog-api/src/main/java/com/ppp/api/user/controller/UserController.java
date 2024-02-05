package com.ppp.api.user.controller;

import com.ppp.api.exception.ExceptionResponse;
import com.ppp.api.user.dto.request.EmailRequest;
import com.ppp.api.user.dto.request.NicknameRequest;
import com.ppp.api.user.dto.response.UserResponse;
import com.ppp.api.user.exception.ErrorCode;
import com.ppp.api.user.exception.UserException;
import com.ppp.api.user.service.UserService;

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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User APIs")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "409", description = "닉네임 중복", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PostMapping("/v1/users/check/nickname")
    public ResponseEntity<String> checkNickname(@RequestBody NicknameRequest nicknameRequest) {
        if (userService.existsByNickname(nicknameRequest.getNickname()))
            throw new UserException(ErrorCode.NICKNAME_DUPLICATION);
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "409", description = "이메일 중복", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PostMapping("/v1/users/check/email")
    public ResponseEntity<String> checkEmail(@RequestBody EmailRequest emailRequest) {
        if (userService.existsByEmail(emailRequest.getEmail()))
            throw new UserException(ErrorCode.EMAIL_DUPLICATION);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 등록", description = "유저 정보를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "409", description = "이메일 중복", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PostMapping("/v1/users/profile")
    public ResponseEntity<Void> createProfile(
            @RequestParam(required = false, value = "profileImage") MultipartFile profileImage,
            @RequestParam("nickname") String nickname,
            @AuthenticationPrincipal PrincipalDetails principalDetails
            ) {
        userService.createProfile(principalDetails.getUser(), profileImage, nickname);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Invalid password", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PutMapping("/v1/users/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestParam(required = false, value = "profileImage") MultipartFile profileImage,
            @RequestParam(required = false, value = "nickname") String nickname,
            @RequestParam(required = false, value = "password") String password,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        userService.updateProfile(principalDetails.getUser(), profileImage, nickname, password);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = UserResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @GetMapping("/v1/users/me")
    public ResponseEntity<UserResponse> displayMe(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userService.displayMe(principalDetails.getUser()));
    }
}
