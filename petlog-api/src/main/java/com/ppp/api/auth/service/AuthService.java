package com.ppp.api.auth.service;


import com.ppp.api.auth.exception.AuthException;
import com.ppp.api.auth.dto.response.AuthenticationResponse;
import com.ppp.api.auth.dto.request.RegisterRequest;
import com.ppp.api.auth.dto.request.SigninRequest;
import com.ppp.api.auth.exception.ErrorCode;
import com.ppp.api.user.exception.NotFoundUserException;
import com.ppp.common.security.jwt.JwtTokenProvider;
import com.ppp.common.service.RedisService;
import com.ppp.domain.user.User;
import com.ppp.domain.user.constant.Role;
import com.ppp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

import static com.ppp.api.user.exception.ErrorCode.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RedisService redisService;

    public void signup(RegisterRequest registerRequest) {
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AuthException(ErrorCode.EXISTS_EMAIL);
        }

        String rawPwd= registerRequest.getPassword();
        String encPwd = passwordEncoder.encode(rawPwd);

        User newUser = User.createUserByEmail(registerRequest.getEmail());
        newUser.setNickname(registerRequest.getNickname());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(encPwd);
        newUser.setRole(Role.USER);

        userRepository.save(newUser);
    }

    public AuthenticationResponse signin(SigninRequest signinRequest) {
        String email = signinRequest.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException(NOT_FOUND_USER));

        if (passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            // 레디스 토큰 저장
            redisService.setValues(email, refreshToken, Duration.ofMillis(jwtTokenProvider.getRefreshExpiration(refreshToken)));

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }

        throw new AuthException(ErrorCode.NOTMATCH_PASSWORD);
    }

    public AuthenticationResponse regenerateToken(String refreshToken) {
        jwtTokenProvider.validateRefreshToken(refreshToken);
        return generateNewToken(refreshToken);
    }

    private AuthenticationResponse generateNewToken(String refreshToken) {
        String email = jwtTokenProvider.getUserEmailFromRefreshToken(refreshToken);

        String refreshInRedis = redisService.getValues(email);
        // 없을 경우 -> 로그아웃된 사용자는 재발급 x
        if (Objects.isNull(refreshInRedis) || !refreshInRedis.equals(refreshToken))
            throw new AuthException(ErrorCode.NOT_FOUND_TOKEN);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER.getMessage()));
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String accessToken) {
        Long accessTokenExpiration = jwtTokenProvider.getAccessExpiration(accessToken);
        String email = jwtTokenProvider.getUserEmailFromAccessToken(accessToken);
        if (redisService.getValues(email) != null) redisService.deleteValues(email);

        redisService.setValues(accessToken, "logout", Duration.ofMillis(accessTokenExpiration));
    }
}
