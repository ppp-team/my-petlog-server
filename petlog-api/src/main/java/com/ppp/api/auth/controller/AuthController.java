package com.ppp.api.auth.controller;

import com.ppp.api.auth.service.AuthService;
import com.ppp.api.user.dto.AuthenticationRequest;
import com.ppp.api.user.dto.AuthenticationResponse;
import com.ppp.api.user.dto.RegisterRequest;
import com.ppp.common.exception.ErrorCode;
import com.ppp.common.exception.TokenException;
import com.ppp.common.security.jwt.JwtTokenProvider;
import com.ppp.domain.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

// 인증 X
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     * email. password
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisterRequest signupDto) throws Exception {
        log.debug("########## signup");

        // add check for email exists in DB
//        if(memberService.existsByEmail(loginDto.getEmail())) {
//            return new ResponseEntity<>("이메일이 존재합니다.",HttpStatus.BAD_REQUEST);
//        }

        User user = authService.signup(signupDto);
        if(user == null) return new ResponseEntity<>("회원가입 실패", HttpStatus.INTERNAL_SERVER_ERROR); // 후추 수정
        return ResponseEntity.noContent().build();
    }

    /**
     * 로그인시 토큰 발급
     */
    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.signin(request));
    }

    /**
     * 로그아웃, refresh 토큰 삭제
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, @RequestHeader("Authorization") final String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) throw new TokenException(ErrorCode.NOT_FOUND_TOKEN);

        final String refreshToken = authHeader.substring(7);
        jwtTokenProvider.validateRefreshToken(refreshToken, request);


        return ResponseEntity.noContent().build();
    }

    /**
     * AT 토큰 만료시 AT/RT 재발급
     * RT 갱신, AT
     * https://github.com/ali-bouali/spring-boot-3-jwt-security/blob/main/src/main/java/com/alibou/security/auth/AuthenticationService.java
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request, @RequestHeader("Authorization") final String authHeader) throws IOException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) throw new TokenException(ErrorCode.NOT_FOUND_TOKEN);

        final String refreshToken = authHeader.substring(7);
        jwtTokenProvider.validateRefreshToken(refreshToken, request);

        return ResponseEntity.ok(authService.regenerateToken(refreshToken));
    }

}
