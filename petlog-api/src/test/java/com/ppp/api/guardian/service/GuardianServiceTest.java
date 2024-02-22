package com.ppp.api.guardian.service;

import com.ppp.api.guardian.dto.request.InviteGuardianRequest;
import com.ppp.api.guardian.dto.response.GuardiansResponse;
import com.ppp.api.guardian.exception.GuardianException;
import com.ppp.api.user.dto.response.UserResponse;
import com.ppp.domain.guardian.Guardian;
import com.ppp.domain.guardian.constant.GuardianRole;
import com.ppp.domain.guardian.constant.RepStatus;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.invitation.Invitation;
import com.ppp.domain.invitation.constant.InviteStatus;
import com.ppp.domain.invitation.repository.InvitationRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.user.dto.UserDto;
import com.ppp.domain.user.repository.UserQuerydslRepository;
import com.ppp.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.ppp.api.guardian.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuardianServiceTest {
    @Mock
    private GuardianRepository guardianRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private InvitationRepository invitationRepository;
    @Mock
    private UserQuerydslRepository userQuerydslRepository;
    @InjectMocks
    private GuardianService guardianService;

    private User user;

    private User user2;
    @BeforeEach
    public void init() {
        user= User.builder()
                .id("abcde1234")
                .nickname("hi")
                .build();
        user2 = User.builder()
                .id("abcde12345")
                .nickname("hi2")
                .build();
    }


    @Test
    @DisplayName("집사 리스트")
    void displayGuardians_ReturnsGuardianResponse() {
        //given
        given(guardianRepository.existsByUserIdAndPetId(anyString(), anyLong())).willReturn(true);

        given(guardianRepository.findAllByPetIdOrderByCreatedAtDesc(anyLong())).willReturn(
                List.of(Guardian.builder().user(user).guardianRole(GuardianRole.LEADER).build(),
                        Guardian.builder().user(user2).guardianRole(GuardianRole.MEMBER).build())
        );

        //when
        GuardiansResponse guardiansResponse = guardianService.displayGuardians(1L, user);

        //then
        Assertions.assertThat(guardiansResponse).isNotNull();
    }

    @Test
    @DisplayName("집사 삭제")
    void deleteOtherGuardian() {
        //given
        Long guardianId = 1L;

        Pet pet = Pet.builder().id(1L).user(user).build();
        lenient().when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));

        Guardian requestedGuardian = Guardian.builder().id(guardianId).user(user2).pet(pet).guardianRole(GuardianRole.MEMBER).build();
        Guardian guardianMe = Guardian.builder().user(user).pet(pet).guardianRole(GuardianRole.LEADER).build();

        //when
        lenient().when(guardianRepository.findById(guardianId)).thenReturn(Optional.of(requestedGuardian));
        lenient().when(guardianRepository.findByUserIdAndPetId(user.getId(), pet.getId())).thenReturn(Optional.of(guardianMe));

        //then
        assertAll(() -> guardianService.deleteGuardian(requestedGuardian.getId(),pet.getId(),user));
    }

    @Test
    @DisplayName("집사 탈퇴시 리더일 경우 GuardianException")
    void deleteGuardianThatIsLEADER_GuardianException() {
        //given
        Long guardianId = 1L;
        Pet pet = Pet.builder().id(1L).user(user).build();

        Guardian requestedGuardian = Guardian.builder().id(guardianId).user(user).pet(pet).build();
        Guardian guardianMe = Guardian.builder().id(guardianId).user(user).pet(pet).guardianRole(GuardianRole.LEADER).build();

        //when
        given(guardianRepository.findById(guardianId)).willReturn(Optional.of(requestedGuardian));
        given(guardianRepository.findByUserIdAndPetId(user.getId(), pet.getId())).willReturn(Optional.of(guardianMe));

        //then
        assertThrows(GuardianException.class, () -> guardianService.deleteGuardian(requestedGuardian.getId(),pet.getId(),user));
    }

    @Test
    @DisplayName("집사 초대")
    void inviteGuardian_ReturnVoid() {
        //given
        User inviterUser = User.builder().id("inviterId").nickname("inviter").email("inviter@test.com").build();
        Pet pet = Pet.builder().id(1L).user(inviterUser).build();
        String inviteeEmail = "invitee@test.com";
        InviteGuardianRequest inviteGuardianRequest = new InviteGuardianRequest(inviteeEmail);

        User inviteeUser = User.builder().id("inviteeId").nickname("invitee").email(inviteeEmail).build();
        when(userRepository.findByEmail(inviteeEmail)).thenReturn(Optional.of(inviteeUser));
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));

        assertAll(()-> guardianService.inviteGuardian(pet.getId(), inviteGuardianRequest, inviterUser));
    }

    @Test
    @DisplayName("집사 초대 - 공동집사일 때")
    void inviteGuardian_validateIsGuardian() {
        //given
        String inviterEmail = "inviter@test.com";
        String inviteeEmail = "invitee@test.com";
        User inviterUser = User.builder().id("inviterId").nickname("inviter").email(inviterEmail).build();
        User inviteeUser = User.builder().id("inviteeId").nickname("invitee").email(inviteeEmail).build();
        Pet pet = Pet.builder().id(1L).user(inviterUser).build();
        InviteGuardianRequest inviteGuardianRequest = new InviteGuardianRequest(inviteeEmail);

        when(userRepository.findByEmail(inviteeEmail)).thenReturn(Optional.of(inviteeUser));
        when(guardianRepository.existsByUserIdAndPetId(inviteeUser.getId(), pet.getId())).thenReturn(true);

        //when
        GuardianException guardianException = assertThrows(GuardianException.class, () -> guardianService.inviteGuardian(pet.getId(), inviteGuardianRequest, inviterUser));

        //then
        assertEquals(guardianException.getCode(), NOT_INVITED_ALREADY_GUARDIAN.getCode());
    }

    @Test
    @DisplayName("집사 초대 - 이미 초대한 사용자일 때")
    void inviteGuardian_AlreadyGuardian() {
        //given
        String inviterEmail = "inviter@test.com";
        String inviteeEmail = "invitee@test.com";
        User inviterUser = User.builder().id("inviterId").nickname("inviter").email(inviterEmail).build();
        User inviteeUser = User.builder().id("inviteeId").nickname("invitee").email(inviteeEmail).build();
        Pet pet = Pet.builder().id(1L).user(inviterUser).build();
        InviteGuardianRequest inviteGuardianRequest = new InviteGuardianRequest(inviteeEmail);

        when(userRepository.findByEmail(inviteeEmail)).thenReturn(Optional.of(inviteeUser));

        Invitation invitation = Invitation.builder().pet(pet).inviteStatus(InviteStatus.PENDING).inviterId(inviterUser.getId()).inviteeId(inviteeUser.getId()).build();
        when(invitationRepository.findByInviteeIdAndPetId(inviteeUser.getId(), pet.getId())).thenReturn(Optional.of(invitation));

        //when
        GuardianException guardianException = assertThrows(GuardianException.class, () -> guardianService.inviteGuardian(pet.getId(), inviteGuardianRequest, inviterUser));

        //then
        assertEquals(guardianException.getCode(), NOT_INVITED.getCode());
    }

    @Test
    @DisplayName("반려 동물에 대한 공동 집사 리스트 조회 성공")
    void displayGuardiansByPetId_success() {
        //given
        given(guardianRepository.existsByUserIdAndPetId(anyString(), anyLong()))
                .willReturn(true);
        given(userQuerydslRepository.findGuardianUserByPetId(anyLong()))
                .willReturn(List.of(new UserDto("abcde1234", "hi"),
                        new UserDto("qwerty1456", "체리엄마")));
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

    @Test
    @DisplayName("대표 반려동물 지정")
    void selectRepresentativePet_switchFromNormalToRepresentative() {
        //given
        Pet pet1 = Pet.builder().id(1L).user(user).isNeutered(false).build();
        Pet pet2 = Pet.builder().id(2L).user(user).isNeutered(false).build();
        Guardian guardian1 = Guardian.builder().pet(pet1).user(user).repStatus(RepStatus.REPRESENTATIVE).build();
        Guardian guardian2 = Guardian.builder().pet(pet2).user(user).repStatus(RepStatus.NORMAL).build();

        when(guardianRepository.findByUserIdAndRepStatus(user.getId(), RepStatus.REPRESENTATIVE)).thenReturn(Optional.of(guardian1));
        when(guardianRepository.findByUserIdAndPetId(user.getId(), pet2.getId())).thenReturn(Optional.of(guardian2));

        //when
        guardianService.selectRepresentative(pet2.getId(), user);

        //then
        assertEquals(RepStatus.NORMAL, guardian1.getRepStatus());
        assertEquals(RepStatus.REPRESENTATIVE, guardian2.getRepStatus());
    }
}