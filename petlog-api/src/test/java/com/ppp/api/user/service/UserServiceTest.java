package com.ppp.api.user.service;

import com.ppp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("닉네임이 존재할 때")
    void existsByNicknameWhenNotExists() {
        //given
        String existingNickname  = "닉네임";

        when(userRepository.existsByNickname(anyString())).thenReturn(true);

        //when
        boolean result = userService.existsByNickname(existingNickname);

        //then
        assertTrue(result,"닉네임이 이미 존재하는 경우 테스트 실패");
    }

    @Test
    @DisplayName("이메일이 존재할 때")
    void existsByEmailWhenNotExists() {
        //given
        String existingEmail  = "a@naver.com";

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        //when
        boolean result = userService.existsByEmail(existingEmail);

        //then
        assertTrue(result,"이메일이 이미 존재하는 경우 테스트 실패");
    }
}
