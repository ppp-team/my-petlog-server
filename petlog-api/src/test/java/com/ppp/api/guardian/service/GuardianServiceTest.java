package com.ppp.api.guardian.service;

import com.ppp.api.guardian.exception.GuardianException;
import com.ppp.api.user.dto.response.UserResponse;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.user.UserDao;
import com.ppp.domain.user.repository.UserQuerydslRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.ppp.api.guardian.exception.ErrorCode.FORBIDDEN_PET_SPACE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GuardianServiceTest {
    @Mock
    private GuardianRepository guardianRepository;
    @Mock
    private UserQuerydslRepository userQuerydslRepository;
    @InjectMocks
    private GuardianService guardianService;

    User user = User.builder()
            .id("abcde1234")
            .nickname("hi")
            .build();

    @Test
    @DisplayName("반려 동물에 대한 공동 집사 리스트 조회 성공")
    void displayGuardiansByPetId_success() {
        //given
        given(guardianRepository.existsByUserIdAndPetId(anyString(), anyLong()))
                .willReturn(true);
        given(userQuerydslRepository.findGuardianUserByPetId(anyLong()))
                .willReturn(List.of(new UserDao("abcde1234", "hi"),
                        new UserDao("qwerty1456", "체리엄마")));
        //when
        List<UserResponse> response = guardianService.displayGuardiansByPetId(user, 1L);
        //then
        assertEquals(response.get(0).id(), "abcde1234");
        assertEquals(response.get(0).nickname(), "hi");
        assertTrue(response.get(0).isCurrentUser());
        assertEquals(response.get(1).id(), "qwerty1456");
        assertEquals(response.get(1).nickname(), "체리엄마");
        assertFalse(response.get(1).isCurrentUser());
    }

    @Test
    @DisplayName("반려 동물에 대한 공동 집사 리스트 조회 실패-forbidden pet space")
    void displayGuardiansByPetId_fail_FORBIDDEN_PET_SPACE() {
        //given
        given(guardianRepository.existsByUserIdAndPetId(anyString(), anyLong()))
                .willReturn(false);
        //when
        GuardianException exception = assertThrows(GuardianException.class, () -> guardianService.displayGuardiansByPetId(user, 1L));
        //then
        assertEquals(exception.getCode(), FORBIDDEN_PET_SPACE.getCode());
    }
}