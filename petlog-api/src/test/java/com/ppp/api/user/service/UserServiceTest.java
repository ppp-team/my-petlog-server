package com.ppp.api.user.service;

import com.ppp.api.auth.service.AuthService;
import com.ppp.api.user.dto.response.ProfileResponse;
import com.ppp.common.service.FileStorageManageService;
import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private FileStorageManageService fileStorageManageService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private UserService userService;


    @Mock
    private AuthService authService;

    @Test
    @DisplayName("닉네임이 존재할 때")
    void existsByNicknameWhenNotExists() {
        //given
        String existingNickname = "닉네임";

        when(userRepository.existsByNickname(anyString())).thenReturn(true);

        //when
        boolean result = userService.existsByNickname(existingNickname);

        //then
        assertTrue(result, "닉네임이 이미 존재하는 경우 테스트 실패");
    }

    @Test
    @DisplayName("이메일이 존재할 때")
    void existsByEmailWhenNotExists() {
        //given
        String existingEmail = "a@naver.com";

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        //when
        boolean result = userService.existsByEmail(existingEmail);

        //then
        assertTrue(result, "이메일이 이미 존재하는 경우 테스트 실패");
    }

    @Test
    @DisplayName("프로필 등록")
    void createProfile() {
        //given
        User user = User.builder()
                .id("randomstring")
                .nickname("닉네임")
                .build();

        MockMultipartFile file = new MockMultipartFile("profileImage", "test.jpg",
                "image/jpeg", "test data".getBytes());

        //when
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        given(fileStorageManageService.uploadImage((MultipartFile) any(), any()))
                .willReturn(Optional.of("/USER/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));

        userService.createProfile(user, file, "새로운닉네임");

        //then
        verify(userRepository, times(1)).findByEmail(any());
    }

    @Test
    @DisplayName("프로필 수정")
    void updateProfile() {
        //given
        String rawPassword = "비밀번호";
        User user = User.builder()
                .id("randomstring")
                .email("test@example.com")
                .nickname("닉네임")
                .password("비밀번호")
                .build();

        MockMultipartFile file = new MockMultipartFile("profileImage", "test.jpg",
                "image/jpeg", "test data".getBytes());

        //when
        lenient().when(authService.checkPasswordMatches(rawPassword, user.getPassword())).thenReturn(true);
        lenient().when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        lenient().when(fileStorageManageService.uploadImage((MultipartFile) any(), any()))
                .thenReturn(Optional.of("/USER/2024-01-31/805496ad51ee46ab94394c5635a2abd820240131183104956.jpg"));

        userService.updateProfile(user, user.getNickname(), user.getPassword());

        //then
        verify(userRepository, times(1)).findByEmail(any());

    }

    @Test
    @DisplayName("내 정보 조회")
    void displayMe() {
        //given
        User user = User.builder()
                .id("randomstring")
                .email("test@example.com")
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        ProfileResponse profileResponse = userService.displayMe(user);

        assertEquals(user.getEmail(), profileResponse.getEmail());
    }
}
