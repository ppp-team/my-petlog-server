package com.ppp.api.user.controller;

import com.ppp.api.user.dto.request.CheckRequest;
import com.ppp.api.user.dto.response.UserResponse;
import com.ppp.api.user.exception.ErrorCode;
import com.ppp.api.user.exception.UserException;
import com.ppp.api.user.service.UserService;

import com.ppp.common.security.PrincipalDetails;
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

    @PostMapping("/v1/users/check/nickname")
    public ResponseEntity<String> checkNickname(@RequestBody CheckRequest checkRequest) {
        String nickname = checkRequest.getNickname();
        if (userService.existsByNickname(nickname))
            throw new UserException(ErrorCode.NICKNAME_DUPLICATION);
        return ResponseEntity.ok(nickname);
    }

    @PostMapping("/v1/users/check/email")
    public ResponseEntity<String> checkEmail(@RequestBody CheckRequest checkRequest) {
        String email = checkRequest.getEmail();
        if (userService.existsByEmail(email))
            throw new UserException(ErrorCode.EMAIL_DUPLICATION);
        return ResponseEntity.ok(email);
    }

    @PostMapping("/v1/users/profile")
    public ResponseEntity<Void> createProfile(
            @RequestParam(required = false, value = "profileImage") MultipartFile profileImage,
            @RequestParam("nickname") String nickname,
            @AuthenticationPrincipal PrincipalDetails principalDetails
            ) {
        userService.createProfile(principalDetails.getUser(), profileImage, nickname);
        return ResponseEntity.ok().build();
    }

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

    @GetMapping("/v1/users/me")
    public ResponseEntity<UserResponse> displayMe(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userService.displayMe(principalDetails.getUser()));
    }
}
