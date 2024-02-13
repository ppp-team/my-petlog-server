package com.ppp.domain.invitation.repository;

import com.ppp.domain.invitation.Invitation;
import com.ppp.domain.invitation.constant.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByInviteeIdAndPetId(String id, Long petId);

    List<Invitation> findByInviteeIdAndInviteStatus(String inviteeId, InviteStatus inviteStatus);

    Optional<Invitation> findByIdAndInviteeId(Long invitationId, String inviteeId);
}
