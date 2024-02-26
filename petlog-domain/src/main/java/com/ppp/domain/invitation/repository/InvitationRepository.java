package com.ppp.domain.invitation.repository;

import com.ppp.domain.invitation.Invitation;
import com.ppp.domain.invitation.constant.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findFirstByInviteeIdAndPetIdOrderByCreatedAtDesc(String inviteeId, Long petId);

    List<Invitation> findByInviteeIdAndInviteStatus(String inviteeId, InviteStatus inviteStatus);

    Optional<Invitation> findByIdAndInviteStatusAndInviteeId(Long invitationId, InviteStatus inviteStatus, String inviteeId);

    List<Invitation> findByInviterIdAndInviteStatus(String inviteeId, InviteStatus inviteStatus);

    Optional<Invitation> findByIdAndInviteStatusAndInviterId(Long invitationId, InviteStatus inviteStatus, String inviteeId);
}
