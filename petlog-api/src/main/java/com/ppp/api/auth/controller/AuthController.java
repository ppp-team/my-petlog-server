package com.ppp.api.auth.controller;

import com.ppp.api.auth.service.AuthService;
import com.ppp.api.auth.dto.request.SigninRequest;
import com.ppp.api.auth.dto.response.AuthenticationResponse;
import com.ppp.api.auth.dto.request.RegisterRequest;
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

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody SigninRequest signinRequest) {
        return ResponseEntity.ok(authService.signin(signinRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout( @RequestHeader("accessToken") String accessToken) {
        authService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> regenerateToken(@RequestHeader("refreshToken") String refreshToken) {
        return ResponseEntity.ok(authService.regenerateToken(refreshToken));
    }

}
