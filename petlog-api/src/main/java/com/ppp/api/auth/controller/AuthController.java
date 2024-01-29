package com.ppp.api.auth.controller;

import com.ppp.api.auth.service.AuthService;
import com.ppp.api.user.dto.SigninDto;
import com.ppp.api.user.dto.AuthenticationResponse;
import com.ppp.api.user.dto.RegisterRequest;
import com.ppp.common.exception.ErrorCode;
import com.ppp.common.exception.TokenException;
import com.ppp.common.security.jwt.JwtTokenProvider;
import com.ppp.common.util.ApiResponse;
import com.ppp.domain.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> signup(@RequestBody RegisterRequest signupDto) {
        User user = authService.signup(signupDto);
        if(user == null) {
            ApiResponse<User> failResponse = ApiResponse.fail("fail", "회원가입에 실패했습니다!");
            return new ResponseEntity<>(failResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 로그인시 토큰 발급
     */
    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody SigninDto signinDto) {
        return ResponseEntity.ok(authService.signin(signinDto));
    }

    /**
     * 로그아웃, refresh 토큰 삭제 - 추후 구현..
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @RequestHeader("refreshToken") String refreshToken
    ) {
        if (refreshToken == null) throw new TokenException(ErrorCode.NOT_FOUND_TOKEN);
        jwtTokenProvider.validateRefreshToken(refreshToken, request);

        // 추후 레디스에서 토큰 삭제

        return ResponseEntity.noContent().build();
    }

    /**
     * AT 토큰 만료시 AT/RT 재발급
     * RT 갱신, AT
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> regenerateToken(
            HttpServletRequest request,
            @RequestHeader("refreshToken") String refreshToken
        ) {
        if (refreshToken == null) throw new TokenException(ErrorCode.NOT_FOUND_TOKEN);
        jwtTokenProvider.validateRefreshToken(refreshToken, request);

        return ResponseEntity.ok(authService.regenerateToken(refreshToken));
    }

}
