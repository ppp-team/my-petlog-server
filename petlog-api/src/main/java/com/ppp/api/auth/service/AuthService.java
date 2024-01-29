package com.ppp.api.auth.service;


import com.ppp.api.auth.exception.SigninException;
import com.ppp.api.user.dto.AuthenticationResponse;
import com.ppp.api.user.dto.RegisterRequest;
import com.ppp.api.user.dto.SigninDto;
import com.ppp.api.user.exception.ErrorCode;
import com.ppp.api.auth.exception.ExistsEmailException;
import com.ppp.api.user.exception.NotFoundUserException;
import com.ppp.common.security.jwt.JwtTokenProvider;
import com.ppp.domain.auth.repository.RefreshToken;
import com.ppp.domain.auth.repository.RefreshTokenRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.user.constant.Role;
import com.ppp.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public User signup(RegisterRequest signupDto) {
        if(userRepository.existsByEmail(signupDto.getEmail())) {
            throw new ExistsEmailException(com.ppp.api.auth.exception.ErrorCode.EXISTS_EMAIL);
        }

        String rawPwd= signupDto.getPassword();
        String encPwd = passwordEncoder.encode(rawPwd);

        User newUser = User.createUserByEmail(signupDto.getEmail());
        newUser.setNickname(signupDto.getNickname());
        newUser.setEmail(signupDto.getEmail());
        newUser.setPassword(encPwd);
        newUser.setRole(Role.USER);

        return userRepository.save(newUser);
    }

    /**
     * 인증 할 때 Access, Refresh 새로 발급
     */
    public AuthenticationResponse signin(SigninDto signinDto) {
        String email = signinDto.getEmail();
        User user = userRepository.findByEmail(signinDto.getEmail())
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));

        // 유저가 존재하면, 패스워드 확인
        if (passwordEncoder.matches(signinDto.getPassword(), user.getPassword())) {
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            // save rt
            RefreshToken persistenceToken = refreshTokenRepository.findByEmail(email)
                    .orElseGet(() -> new RefreshToken(email, refreshToken));
            persistenceToken.setRefreshToken(refreshToken);
            refreshTokenRepository.save(persistenceToken);

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }

        throw new SigninException(com.ppp.api.auth.exception.ErrorCode.NOTMATCH_PASSWORD);
    }

    // AT, RT 발급
    public AuthenticationResponse regenerateToken(String refreshTokenParam) {
        return generateNewToken(refreshTokenParam);
    }

    private AuthenticationResponse generateNewToken(String refreshToken) {
        Claims claims = jwtTokenProvider.getUserFromRefreshToken(refreshToken);
        String email = claims.get("email").toString();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));

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
