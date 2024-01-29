package com.ppp.api.auth.service;


import com.ppp.api.user.dto.AuthenticationRequest;
import com.ppp.api.user.dto.AuthenticationResponse;
import com.ppp.api.user.dto.RegisterRequest;
import com.ppp.api.user.exception.ErrorCode;
import com.ppp.api.user.exception.NotFoundUserException;
import com.ppp.common.security.PrincipalDetails;
import com.ppp.common.security.jwt.JwtTokenProvider;
import com.ppp.domain.auth.repository.RefreshToken;
import com.ppp.domain.auth.repository.RefreshTokenRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.user.constant.Role;
import com.ppp.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    final PasswordEncoder passwordEncoder;

    public User signup(RegisterRequest signupDto) {
        try{
            String rawPwd= signupDto.getPassword();
            String encPwd = passwordEncoder.encode(rawPwd);

            User newUser = User.createUserByEmail(signupDto.getEmail());
            newUser.setNickname(signupDto.getNickname());
            newUser.setEmail(signupDto.getEmail());
            newUser.setPassword(encPwd);
            newUser.setRole(Role.USER);

            return memberRepository.save(newUser);
        }catch (StringIndexOutOfBoundsException s){
            return null;
        }
    }

    /**
     * 인증 할 때 Access, Refresh 새로 발급
     */
    public AuthenticationResponse signin(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateToken(principal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        // save rt
        RefreshToken persistenceToken = refreshTokenRepository.findByEmail(principal.getUsername())
                .orElseGet(() -> new RefreshToken(principal.getUsername(),refreshToken));
        persistenceToken.setRefreshToken(refreshToken);
        refreshTokenRepository.save(persistenceToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // AT, RT 발급
    public AuthenticationResponse regenerateToken(String refreshTokenParam) {
        // 블랙리스트 조회
        if (isBlacklisted(refreshTokenParam)) {
            // 블랙리스트에 있는 경우(로그아웃됨) 에러 처리 등 수행
            throw new RuntimeException("Refresh Token이 블랙리스트에 있습니다."); // 예외처리 수정 예정
        }

        return generateNewToken(refreshTokenParam);
    }

    private AuthenticationResponse generateNewToken(String refreshTokenParam) {
        Claims claims = jwtTokenProvider.getUserFromRefreshToken(refreshTokenParam);
        String email = claims.get("email").toString();

        User user = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));
        String at = jwtTokenProvider.generateAccessToken(user);
        String rt = jwtTokenProvider.generateRefreshToken(user);

        // save rt
        RefreshToken persistenceToken = refreshTokenRepository.findByEmail(email)
                .orElseGet(() -> new RefreshToken(email, refreshTokenParam));
        persistenceToken.setRefreshToken(refreshTokenParam);
        refreshTokenRepository.save(persistenceToken);

        return AuthenticationResponse.builder()
                .accessToken(at)
                .refreshToken(rt)
                .build();
    }


    private boolean isBlacklisted(String refreshToken) {
        // 블랙리스트에 있는지 여부 확인
//        return tokenRepository.existsByToken(refreshToken);
        return false;
    }

}
