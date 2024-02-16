package com.ppp.api.invitation.service;

import com.ppp.api.guardian.service.GuardianService;
import com.ppp.api.invitation.dto.request.InvitationRequest;
import com.ppp.api.invitation.dto.request.RegisterInvitationRequest;
import com.ppp.api.invitation.dto.response.InvitationResponse;
import com.ppp.api.invitation.exception.ErrorCode;
import com.ppp.api.invitation.exception.InvitationException;
import com.ppp.api.pet.exception.PetException;
import com.ppp.common.util.TimeUtil;
import com.ppp.domain.guardian.constant.GuardianRole;
import com.ppp.domain.invitation.Invitation;
import com.ppp.domain.invitation.MyInvitationDto;
import com.ppp.domain.invitation.constant.InviteStatus;
import com.ppp.domain.invitation.repository.InvitationQuerydslRepository;
import com.ppp.domain.invitation.repository.InvitationRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.PetImage;
import com.ppp.domain.pet.repository.PetImageRepository;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.ProfileImageRepository;
import com.ppp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ppp.api.pet.exception.ErrorCode.PET_NOT_FOUND;


@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final GuardianService guardianService;
    private final PetRepository petRepository;
    private final PetImageRepository petImageRepository;
    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;
    private final InvitationQuerydslRepository invitationQuerydslRepository;

    @Transactional(readOnly = true)
    public List<InvitationResponse> displayInvitations(User user) {
        List<InvitationResponse> invitationResponseList = new ArrayList<>();
        List<Invitation> invitations = invitationRepository.findByInviteeIdAndInviteStatus(user.getId(), InviteStatus.PENDING);
        for (Invitation invitation : invitations) {
            Pet pet = invitation.getPet();
            PetImage petImage = petImageRepository.findByPet(pet).orElse(new PetImage());

            InvitationResponse invitationResponse = InvitationResponse.builder()
                    .invitationId(invitation.getId())
                    .inviteStatus(invitation.getInviteStatus().name())
                    .invitedAt(TimeUtil.calculateTerm(invitation.getCreatedAt()))
                    .petId(pet.getId())
                    .petName(pet.getName())
                    .petImageUrl(petImage.getUrl())
                    .build();
            invitationResponseList.add(invitationResponse);
        }
        return invitationResponseList;
    }

    @Transactional
    public void acceptInvitation(InvitationRequest invitationRequest, User user) {
        Invitation invitation = invitationRepository.findByIdAndInviteStatusAndInviteeId(invitationRequest.getInvitationId(), InviteStatus.PENDING, user.getId())
                .orElseThrow(() -> new InvitationException(ErrorCode.INVITATION_NOT_FOUND));
        guardianService.createGuardian(invitation.getPet(), user, GuardianRole.MEMBER);
        invitation.updateInviteStatus(InviteStatus.ACCEPTED);
    }

    @Transactional
    public void refuseInvitation(InvitationRequest invitationRequest, User user) {
        Invitation invitation = invitationRepository.findByIdAndInviteStatusAndInviteeId(invitationRequest.getInvitationId(), InviteStatus.PENDING, user.getId())
                .orElseThrow(() -> new InvitationException(ErrorCode.INVITATION_NOT_FOUND));
        invitation.updateInviteStatus(InviteStatus.REJECTED);
    }

    public List<MyInvitationDto> displayMyInvitations(User user) {
        List<MyInvitationDto> myInvitationDtoResponseList = invitationQuerydslRepository.findMyInvitationByInviterId(user.getId());
        myInvitationDtoResponseList.forEach(myInvitationDto -> {
                String status = null;
                if (InviteStatus.PENDING.name().equals(myInvitationDto.getInviteStatus())) {
                    status = InviteStatus.PENDING.getValue();
                } else if (InviteStatus.REJECTED.name().equals(myInvitationDto.getInviteStatus())) {
                    status = InviteStatus.REJECTED.getValue();
                }
                myInvitationDto.setInviteStatus(status);
                myInvitationDto.setInvitedAt(TimeUtil.calculateTerm(myInvitationDto.getCreatedAt()));
        });
        return myInvitationDtoResponseList;
    }

    @Transactional
    public void cancelInvitation(InvitationRequest invitationRequest, User user) {
        Invitation invitation = invitationRepository.findByIdAndInviteStatusAndInviterId(invitationRequest.getInvitationId(), InviteStatus.PENDING, user.getId())
            .orElseThrow(() -> new InvitationException(ErrorCode.INVITATION_NOT_FOUND));
        invitation.updateInviteStatus(InviteStatus.CANCELED);
    }

    public void registerInvitation(RegisterInvitationRequest registerInvitationRequest, User user) {
        Pet pet = petRepository.findByInvitedCode(registerInvitationRequest.getInviteCode())
                .orElseThrow(() -> new PetException(PET_NOT_FOUND));
        guardianService.createGuardian(pet, user, GuardianRole.MEMBER);
    }
}
