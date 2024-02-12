package com.ppp.api.invitation.service;

import com.ppp.api.invitation.dto.request.InvitationRequest;
import com.ppp.api.invitation.dto.response.InvitationResponse;
import com.ppp.api.invitation.exception.ErrorCode;
import com.ppp.api.invitation.exception.InvitationException;
import com.ppp.common.util.TimeUtil;
import com.ppp.domain.guardian.Guardian;
import com.ppp.domain.guardian.constant.GuardianRole;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.invitation.Invitation;
import com.ppp.domain.invitation.constant.InviteStatus;
import com.ppp.domain.invitation.repository.InvitationRepository;
import com.ppp.domain.pet.Pet;
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


@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;
    private final GuardianRepository guardianRepository;

    @Transactional(readOnly = true)
    public List<InvitationResponse> displayInvitations(User user) {
        List<InvitationResponse> invitationResponseList = new ArrayList<>();
        List<Invitation> invitations = invitationRepository.findByInviteeId(user.getId());
        for (Invitation invitation : invitations) {
            Pet pet = invitation.getPet();
            String inviterId = invitation.getInviterId();
            User inviter = userRepository.findById(inviterId).orElse(new User());
            ProfileImage profileImage = profileImageRepository.findByUser(inviter).orElse(new ProfileImage());

            InvitationResponse invitationResponse = InvitationResponse.builder()
                    .invitationId(invitation.getId())
                    .inviteStatus(invitation.getInviteStatus().name())
                    .invitedAt(TimeUtil.calculateTerm(invitation.getCreatedAt()))
                    .petId(pet.getId())
                    .petName(pet.getName())
                    .inviterName(inviter.getNickname())
                    .profilePath(profileImage.getUrl())
                    .build();
            invitationResponseList.add(invitationResponse);
        }
        return invitationResponseList;
    }

    @Transactional
    public void acceptInvitation(InvitationRequest invitationRequest, User user) {
        Invitation invitation = invitationRepository.findByIdAndInviteeId(invitationRequest.getInvitationId(), user.getId())
                .orElseThrow(() -> new InvitationException(ErrorCode.INVITATION_NOT_FOUND));

        if (InviteStatus.PENDING.name().equals(invitation.getInviteStatus().name()))
            guardianRepository.save(new Guardian(GuardianRole.MEMBER, invitation.getPet(), user));
        else
            throw new InvitationException(ErrorCode.INVITATION_NOT_FOUND);
        invitation.updateInviteStatus(InviteStatus.ACCEPTED);
    }

    @Transactional
    public void refuseInvitation(InvitationRequest invitationRequest, User user) {
        Invitation invitation = invitationRepository.findByIdAndInviteeId(invitationRequest.getInvitationId(), user.getId())
                .orElseThrow(() -> new InvitationException(ErrorCode.INVITATION_NOT_FOUND));
        invitation.updateInviteStatus(InviteStatus.REJECTED);
    }
}
