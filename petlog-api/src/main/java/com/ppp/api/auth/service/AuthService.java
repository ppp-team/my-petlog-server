package com.ppp.api.auth.service;


import com.ppp.api.auth.exception.AuthException;
import com.ppp.api.user.dto.response.AuthenticationResponse;
import com.ppp.api.user.dto.request.RegisterRequest;
import com.ppp.api.user.dto.request.SigninRequest;
import com.ppp.api.user.exception.ErrorCode;
import com.ppp.api.user.exception.NotFoundUserException;
import com.ppp.common.security.jwt.JwtTokenProvider;
import com.ppp.domain.auth.repository.RefreshToken;
import com.ppp.domain.auth.repository.RefreshTokenRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.user.constant.Role;
import com.ppp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    final PasswordEncoder passwordEncoder;

    public void signup(RegisterRequest signupDto) {
        if(userRepository.existsByEmail(signupDto.getEmail())) {
            throw new AuthException(com.ppp.api.auth.exception.ErrorCode.EXISTS_EMAIL);
        }

        String rawPwd= signupDto.getPassword();
        String encPwd = passwordEncoder.encode(rawPwd);

        User newUser = User.createUserByEmail(signupDto.getEmail());
        newUser.setNickname(signupDto.getNickname());
        newUser.setEmail(signupDto.getEmail());
        newUser.setPassword(encPwd);
        newUser.setRole(Role.USER);

        userRepository.save(newUser);
    }

    public AuthenticationResponse signin(SigninRequest signinRequest) {
        String email = signinRequest.getEmail();
        User user = userRepository.findByEmail(signinRequest.getEmail())
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));

        if (passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            RefreshToken persistenceToken = refreshTokenRepository.findByEmail(email)
                    .orElseGet(() -> new RefreshToken(email, refreshToken));
            persistenceToken.setRefreshToken(refreshToken);
            refreshTokenRepository.save(persistenceToken);

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }

        throw new AuthException(com.ppp.api.auth.exception.ErrorCode.NOTMATCH_PASSWORD);
    }

    public AuthenticationResponse regenerateToken(String refreshToken) {
        jwtTokenProvider.validateRefreshToken(refreshToken);
        return generateNewToken(refreshToken);
    }

    private AuthenticationResponse generateNewToken(String refreshToken) {
        String email = jwtTokenProvider.getUserEmailFromRefreshToken(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.NOT_FOUND_USER.getMessage()));

        String at = jwtTokenProvider.generateAccessToken(user);
        String rt = jwtTokenProvider.generateRefreshToken(user);

        // save rt
        RefreshToken persistenceToken = refreshTokenRepository.findByEmail(email)
                .orElseGet(() -> new RefreshToken(email, refreshToken));
        persistenceToken.setRefreshToken(rt);
        refreshTokenRepository.save(persistenceToken);

        return AuthenticationResponse.builder()
                .accessToken(at)
                .refreshToken(rt)
                .build();
    }
}
