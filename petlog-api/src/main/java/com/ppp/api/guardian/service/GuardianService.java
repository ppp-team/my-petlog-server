package com.ppp.api.guardian.service;

import com.ppp.api.guardian.dto.request.InviteGuardianRequest;
import com.ppp.api.guardian.dto.response.GuardianResponse;
import com.ppp.api.guardian.dto.response.GuardiansResponse;
import com.ppp.api.guardian.exception.ErrorCode;
import com.ppp.api.guardian.exception.GuardianException;
import com.ppp.api.pet.exception.PetException;
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
import com.ppp.domain.user.repository.UserQuerydslRepository;
import com.ppp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ppp.api.pet.exception.ErrorCode.PET_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuardianService {
    private final GuardianRepository guardianRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final InvitationRepository invitationRepository;
    private final UserQuerydslRepository userQuerydslRepository;

    public GuardiansResponse displayGuardians(Long petId, User user) {
        validateIsGuardian(petId, user.getId());

        List<GuardianResponse> guardianResponseList = new ArrayList<>();
        List<Guardian> guardianList = guardianRepository.findAllByPetIdOrderByCreatedAtDesc(petId);
        for (Guardian guardian : guardianList) {
            guardianResponseList.add(GuardianResponse.from(guardian));
        }

        return new GuardiansResponse(guardianList.size(), guardianResponseList);
    }

    public void createGuardian(Pet pet, User user, GuardianRole guardianRole) {
        validateIsGuardian(pet.getId(), user.getId());
        guardianRepository.save(Guardian.builder().guardianRole(guardianRole).pet(pet).user(user).repStatus(RepStatus.NORMAL).build());
    }

    @Transactional
    public void deleteGuardian(Long guardianId, Long petId, User user) {
        Guardian requestedGuardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new GuardianException(ErrorCode.GUARDIAN_NOT_FOUND));

        Guardian guardianMe = guardianRepository.findByUserIdAndPetId(user.getId(), petId)
                .orElseThrow(() -> new GuardianException(ErrorCode.GUARDIAN_NOT_FOUND));

        if (requestedGuardian.getId() == guardianMe.getId()) { // 탈퇴과정
            // 본인 탈퇴일 때 - 리더이면 관리자 문의 요청 메시지
            if (isReaderGuardian(guardianMe)) {
                throw new GuardianException(ErrorCode.NOT_DELETED_IF_READER);
            } else {
                guardianRepository.deleteById(requestedGuardian.getId());
            }
        } else {
            // 삭제 과정 - 본인이 이방의 리더일 때
            if (isReaderGuardian(guardianMe)) {
                guardianRepository.deleteById(requestedGuardian.getId());
            }
        }
    }

    private boolean isReaderGuardian(Guardian guardian) {
        return GuardianRole.LEADER.equals(guardian.getGuardianRole());
    }

    public void deleteReaderGuardian(Guardian guardian, Long petId) {
        if (guardianRepository.existsByPetIdAndGuardianRole(petId, GuardianRole.MEMBER)) {
            throw new GuardianException(ErrorCode.NOT_ALLOWED_DELETE_LEADER);
        }
        guardianRepository.deleteById(guardian.getId());
    }

    public void inviteGuardian(Long petId, InviteGuardianRequest inviteGuardianRequest, User inviterUser) {
        User inviteeUser = userRepository.findByEmail(inviteGuardianRequest.getEmail())
                .orElseThrow(() -> new GuardianException(ErrorCode.NOT_FOUND_INVITEE));

        validateInvitation(petId, inviteeUser, inviterUser);

        Pet pet = petRepository.findByIdAndIsDeletedFalse(petId)
                .orElseThrow(() -> new PetException(PET_NOT_FOUND));
        Invitation invitation = Invitation.builder()
                .inviterId(inviterUser.getId())
                .inviteeId(inviteeUser.getId())
                .inviteCode(pet.getInvitedCode())
                .pet(pet)
                .inviteStatus(InviteStatus.PENDING)
                .build();
        invitationRepository.save(invitation);
    }

    private void validateInvitation(Long petId, User inviteeUser, User inviterUser) {
        if (inviteeUser.getEmail().equals(inviterUser.getEmail()))
            throw new GuardianException(ErrorCode.NOT_INVITED_EMAIL);

        // 이미 공동집사일 때
        validateIsGuardian(petId, inviteeUser.getId());

        // 이미 초대한 사용자일 때
        Optional<Invitation> invitationOfInvitee = invitationRepository.findByInviteeIdAndPetId(inviteeUser.getId(), petId);
        invitationOfInvitee.ifPresent(invitation -> {
            if (InviteStatus.PENDING.name().equals(invitation.getInviteStatus().name()) ||
                    InviteStatus.ACCEPTED.name().equals(invitation.getInviteStatus().name()))
                throw new GuardianException(ErrorCode.NOT_INVITED);
        });
    }

    public void validateIsGuardian(Long petId, String userId) {
        if (guardianRepository.existsByPetIdAndUserId(petId, userId))
            throw new GuardianException(ErrorCode.NOT_INVITED_ALREADY_GUARDIAN);
    }

    public Guardian findByUserIdAndPetId(String userId, Long petId) {
        return guardianRepository.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new GuardianException(ErrorCode.GUARDIAN_NOT_FOUND));
    }

    public List<UserResponse> displayGuardiansByPetId(User user, Long petId) {
        validateQueryGuardians(user.getId(), petId);
        return userQuerydslRepository.findGuardianUserByPetId(petId)
                .stream().map(userDao -> UserResponse.from(userDao, user.getId()))
                .collect(Collectors.toList());
    }

    private void validateQueryGuardians(String userId, Long petId) {
        if (!guardianRepository.existsByUserIdAndPetId(userId, petId))
            throw new GuardianException(ErrorCode.FORBIDDEN_PET_SPACE);
    }

    @Transactional
    public void selectRepresentative(Long petId, User user) {
        guardianRepository.findByUserIdAndRepStatus(user.getId(), RepStatus.REPRESENTATIVE).ifPresent(guardian -> {
            guardian.updateRepStatus(RepStatus.NORMAL);
            guardianRepository.save(guardian);
        });

        Guardian guardian = findByUserIdAndPetId(user.getId(), petId);
        guardian.updateRepStatus(RepStatus.REPRESENTATIVE);
    }
}
