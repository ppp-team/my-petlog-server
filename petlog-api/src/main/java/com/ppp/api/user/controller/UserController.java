package com.ppp.api.user.controller;

import com.ppp.api.user.dto.request.CheckRequest;
import com.ppp.api.user.dto.response.UserCommonResponse;
import com.ppp.api.user.service.UserService;

import com.ppp.common.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/v1/users/check/nickname")
    public ResponseEntity<UserCommonResponse<String>> checkNickname(@RequestBody CheckRequest checkRequest) {
        if (!userService.existsByNickname(checkRequest.getNickname())) {
            return ResponseEntity.ok(UserCommonResponse.success("사용 가능한 닉네임입니다.", checkRequest.getNickname()));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(UserCommonResponse.fail("이미 사용 중인 닉네임입니다.", checkRequest.getNickname()));
        }
    }

    @PostMapping("/v1/users/check/email")
    public ResponseEntity<UserCommonResponse<String>> checkEmail(@RequestBody CheckRequest checkRequest) {
        if (!userService.existsByEmail(checkRequest.getEmail())) {
            return ResponseEntity.ok(UserCommonResponse.success("사용 가능한 이메일입니다.", checkRequest.getEmail()));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(UserCommonResponse.fail("이미 가입된 이메일입니다.", checkRequest.getEmail()));
        }
    }

    @PostMapping("/v1/users/profile")
    public ResponseEntity<UserCommonResponse<String>> createProfile(
            @RequestParam(required = false, value = "profileImage")MultipartFile profileImage,
            @RequestParam("nickname") String nickname,
            @AuthenticationPrincipal PrincipalDetails principalDetails
            ) {
        userService.createProfile(principalDetails.getUser(), profileImage, nickname);
        return ResponseEntity.ok(UserCommonResponse.success("등록되었습니다!", null));
    }
}
