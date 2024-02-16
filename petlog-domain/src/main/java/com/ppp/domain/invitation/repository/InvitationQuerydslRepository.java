package com.ppp.domain.invitation.repository;

import com.ppp.domain.invitation.dto.MyInvitationDto;
import com.ppp.domain.invitation.constant.InviteStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ppp.domain.invitation.QInvitation.invitation;
import static com.ppp.domain.user.QProfileImage.profileImage;
import static com.ppp.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class InvitationQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    public List<MyInvitationDto> findMyInvitationByInviterId(String inviterId) {
        return queryFactory
                .select(Projections.fields(MyInvitationDto.class,
                        invitation.id.as("invitationId"),
                        user.nickname.as("inviteeName"),
                        invitation.inviteStatus.stringValue().as("inviteStatus"),
                        profileImage.url.as("profilePath"),
                        invitation.createdAt.as("createdAt")
                ))
                .from(invitation)
                .leftJoin(user).on(invitation.inviteeId.eq(user.id))
                .leftJoin(profileImage).on(invitation.inviteeId.eq(profileImage.user.id))
                .where(invitation.inviterId.eq(inviterId))
                .where(invitation.inviteStatus.in(InviteStatus.PENDING, InviteStatus.REJECTED))
                .fetch();
    }
}
