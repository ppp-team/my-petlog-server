package com.ppp.domain.invitation.repository;

import com.ppp.domain.invitation.constant.InviteStatus;
import com.ppp.domain.invitation.dto.MyInvitationDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ppp.domain.invitation.QInvitation.invitation;
import static com.ppp.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class InvitationQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    public List<MyInvitationDto> findMyInvitationByInviterId(Long petId, String inviterId) {
        return queryFactory
                .select(Projections.fields(MyInvitationDto.class,
                        invitation.id.as("invitationId"),
                        user.nickname.as("inviteeName"),
                        invitation.inviteStatus.stringValue().as("inviteStatus"),
                        user.profilePath.as("profilePath"),
                        invitation.createdAt.as("createdAt")
                ))
                .from(invitation)
                .leftJoin(user).on(invitation.inviteeId.eq(user.id))
                .where(invitation.pet.id.eq(petId))
                .where(invitation.inviterId.eq(inviterId))
                .where(invitation.inviteStatus.in(InviteStatus.PENDING, InviteStatus.REJECTED))
                .fetch();
    }
}
