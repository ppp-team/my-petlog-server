package com.ppp.api.auth.service;

import com.ppp.api.auth.dto.request.RegisterRequest;
import com.ppp.api.auth.dto.request.SigninRequest;
import com.ppp.api.auth.dto.response.AuthenticationResponse;
import com.ppp.common.client.RedisClient;
import com.ppp.common.security.jwt.JwtTokenProvider;
import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    RedisClient redisClient;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입")
    void signup() {
        //given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .nickname("닉네임")
                .email("j2@gmail.com")
                .password("password")
                .build();

        //when
        authService.signup(registerRequest);
    }

    @Test
    @DisplayName("로그인")
    void signin() {
        //given
        SigninRequest signinRequest = SigninRequest.builder()
                .email("j2@gmail.com")
                .password("password")
                .build();

        User mockUser = User.builder()
                .email(signinRequest.getEmail())
                .password(passwordEncoder.encode(signinRequest.getPassword()))
                .build();

        //when
        when(userRepository.findByEmail(signinRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(signinRequest.getPassword(), mockUser.getPassword())).thenReturn(true);

        String mockAccessToken = "mockAccessToken";
        String mockRefreshToken = "mockRefreshToken";

        when(jwtTokenProvider.generateAccessToken(mockUser)).thenReturn(mockAccessToken);
        when(jwtTokenProvider.generateRefreshToken(mockUser)).thenReturn(mockRefreshToken);


        AuthenticationResponse authenticationResponse = authService.signin(signinRequest);

        assertEquals(mockAccessToken, authenticationResponse.getAccessToken());
        assertEquals(mockRefreshToken, authenticationResponse.getRefreshToken());
    }

    @Test
    @DisplayName("로그아웃")
    void logout() {
        //given
        String mockAccessToken = "mockAccessToken";
        String mockUserEmail = "test@example.com";
        Long mockAccessTokenExpiration = System.currentTimeMillis() + Duration.ofMinutes(30).toMillis();

        when(jwtTokenProvider.getAccessExpiration(mockAccessToken)).thenReturn(mockAccessTokenExpiration);
        when(jwtTokenProvider.getUserEmailFromAccessToken(mockAccessToken)).thenReturn(mockUserEmail);

        when(redisClient.getValues(mockUserEmail)).thenReturn("mockRefreshToken");

        //when
        authService.logout(mockAccessToken);

        //then
        // 메소드가 1번 호출되었는지
        verify(redisClient, times(1)).deleteValues(mockUserEmail);
        verify(redisClient, times(1)).setValues(mockAccessToken, "logout", Duration.ofMillis(mockAccessTokenExpiration));
    }

}