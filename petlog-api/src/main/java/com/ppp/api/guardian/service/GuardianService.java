package com.ppp.api.guardian.service;

import com.ppp.api.guardian.dto.request.InviteGuardianRequest;
import com.ppp.api.guardian.dto.response.GuardianResponse;
import com.ppp.api.guardian.dto.response.GuardiansResponse;
import com.ppp.api.guardian.exception.ErrorCode;
import com.ppp.api.guardian.exception.GuardianException;
import com.ppp.api.pet.exception.PetException;
import com.ppp.domain.guardian.Guardian;
import com.ppp.domain.guardian.constant.GuardianRole;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.invitation.Invitation;
import com.ppp.domain.invitation.constant.InviteStatus;
import com.ppp.domain.invitation.repository.InvitationRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.ProfileImage;
import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.ProfileImageRepository;
import com.ppp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuardianService {
    private final GuardianRepository guardianRepository;
    private final ProfileImageRepository profileImageRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final InvitationRepository invitationRepository;

    public GuardiansResponse displayGuardians(Long petId) {
        List<GuardianResponse> guardianResponseList = new ArrayList<>();
        List<Guardian> guardianList = guardianRepository.findAllByPetIdOrderByCreatedAtDesc(petId);
        for (Guardian guardian : guardianList) {
            ProfileImage profileImage = profileImageRepository.findByUser(guardian.getUser())
                    .orElse(new ProfileImage());

            guardianResponseList.add(GuardianResponse.from(guardian, profileImage));
        }

        return new GuardiansResponse(guardianList.size(), guardianResponseList);
    }

    public void createGuardian(Pet pet, User user, GuardianRole guardianRole) {
        guardianRepository.save(new Guardian(guardianRole, pet, user));
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
        }
        else {
            // 삭제 과정 - 본인이 이방의 리더일 때
            if (isReaderGuardian(guardianMe)) {
                guardianRepository.deleteById(requestedGuardian.getId());
            }
        }
    }

    private boolean isReaderGuardian(Guardian guardian) {
        return GuardianRole.LEADER.name().equals(guardian.getGuardianRole().name());
    }

    public void deleteReaderGuardian(Guardian guardian, Long petId) {
        if (guardianRepository.existsGuardianOfMember(petId)) {
            throw new GuardianException(ErrorCode.NOT_ALLOWED_DELETE_LEADER);
        }
        guardianRepository.deleteById(guardian.getId());
    }

    public void inviteGuardian(Long petId, InviteGuardianRequest inviteGuardianRequest, User inviterUser) {
        User inviteeUser = userRepository.findByEmail(inviteGuardianRequest.getEmail())
                .orElseThrow(() -> new GuardianException(ErrorCode.NOT_FOUND_INVITEE));

        validateInvitation(petId, inviteeUser, inviterUser);

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(com.ppp.api.pet.exception.ErrorCode.PET_NOT_FOUND));
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
        if (guardianRepository.existsByUserIdAndPetId(inviteeUser.getId(), petId))
            throw new GuardianException(ErrorCode.NOT_INVITED_ALREADY_GUARDIAN);

        // 이미 초대한 사용자일 때
        Optional<Invitation> invitationOfInvitee = invitationRepository.findByInviteeIdAndPetId(inviteeUser.getId(), petId);
        invitationOfInvitee.ifPresent(invitation -> {
            if (InviteStatus.PENDING.name().equals(invitation.getInviteStatus().name()) ||
                    InviteStatus.ACCEPTED.name().equals(invitation.getInviteStatus().name()))
                throw new GuardianException(ErrorCode.NOT_INVITED);
        });
    }

    public Guardian findByUserIdAndPetId(User user, Long petId) {
        return guardianRepository.findByUserIdAndPetId(user.getId(), petId)
                .orElseThrow(() -> new GuardianException(ErrorCode.GUARDIAN_NOT_FOUND));
    }
}
