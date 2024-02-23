package com.ppp.api.auth.controller;

import com.ppp.api.auth.dto.request.SocialRequest;
import com.ppp.api.auth.service.AuthService;
import com.ppp.api.auth.dto.request.SigninRequest;
import com.ppp.api.auth.dto.response.AuthenticationResponse;
import com.ppp.api.auth.dto.request.RegisterRequest;
import com.ppp.api.exception.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Auth APIs")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "409", description = "이메일 중복", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Invalid password", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = AuthenticationResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Invalid password", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@Valid @RequestBody SigninRequest signinRequest, HttpServletResponse response) {
        AuthenticationResponse authenticationResponse = authService.signin(signinRequest);
        authService.setHeaderAccessToken(response, authenticationResponse.getAccessToken());
        authService.setHeaderRefreshToken(response, authenticationResponse.getRefreshToken());

        response.addHeader("Authorization", "Bearer " + authenticationResponse.getAccessToken());
        response.addHeader("refreshToken", authenticationResponse.getRefreshToken());
        return ResponseEntity.ok(authenticationResponse);
    }

    @Operation(description = "소셜 로그인 성공시 해당 유저를 회원가입/로그인 시켜 토큰을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = AuthenticationResponse.class))}),
    })
    @PostMapping("/login/social")
    public ResponseEntity<AuthenticationResponse> socialLogin(@RequestBody SocialRequest socialRequest) {
        return ResponseEntity.ok(authService.socialLogin(socialRequest));
    }

    @Operation(summary = "로그아웃")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", description = "Invalid token", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("accessToken") String accessToken) {
        authService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Renew Access Token", description = "Refresh token을 통해 Access Token을 갱신합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = AuthenticationResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid token", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> regenerateToken(@RequestHeader("refreshToken") String refreshToken) {
        return ResponseEntity.ok(authService.regenerateToken(refreshToken));
    }

}
