package com.ppp.api.auth.controller;

import com.ppp.api.auth.service.AuthService;
import com.ppp.api.user.dto.request.SigninRequest;
import com.ppp.api.user.dto.response.AuthenticationResponse;
import com.ppp.api.user.dto.request.RegisterRequest;
import com.ppp.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody SigninRequest signinRequest) {
        return ResponseEntity.ok(authService.signin(signinRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout( @RequestHeader("refreshToken") String refreshToken) {
        // todo: 레디스에서 토큰 삭제

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> regenerateToken(@RequestHeader("refreshToken") String refreshToken) {
        return ResponseEntity.ok(authService.regenerateToken(refreshToken));
    }

}
